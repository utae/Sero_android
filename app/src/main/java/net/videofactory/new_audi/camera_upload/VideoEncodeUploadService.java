package net.videofactory.new_audi.camera_upload;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Utae on 2015-12-01.
 */
public class VideoEncodeUploadService extends Service {

    private FFmpeg ffmpeg;
    private String inputFile, outputFile, startTime, duration, text;
    private String cmd;
    private ArrayList<String> hashtagList;

    @Override
    public void onCreate() {
        super.onCreate();

        Utilities.logD("Test", "service create");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this).setContentTitle("Sero").setContentText("uploading").setSmallIcon(R.mipmap.ic_sero).setContentIntent(pendingIntent).build();
        startForeground(1234, notification);

        ffmpeg = FFmpeg.getInstance(this);

        try {
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Utilities.logD("Test", "load failure");
                }

                @Override
                public void onSuccess() {
                    Utilities.logD("Test", "load success");
                }

                @Override
                public void onStart() {
                    Utilities.logD("Test", "load start");
                }

                @Override
                public void onFinish() {
                    Utilities.logD("Test", "load finish");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utilities.logD("Test", "Service err : " + e.toString());
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        inputFile = intent.getStringExtra("inputFile");
        outputFile = intent.getStringExtra("outputFile");
        startTime = intent.getStringExtra("startTime");
        duration = intent.getStringExtra("duration");
        text = intent.getStringExtra("text");
        hashtagList = intent.getStringArrayListExtra("hashtagList");
        if(inputFile == null || outputFile == null){
            stopSelf();
        }

        Utilities.logD("Test", "start service");

        cmd = makeCmd(startTime, inputFile, duration, outputFile);
        try {
            ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String s) {
                    Utilities.logD("Test", "execute onSuccess" + s);
                    uploadS3();
                }

                @Override
                public void onProgress(String s) {
                    Utilities.logD("Test", "execute onProgress" + s);
                }

                @Override
                public void onFailure(String s) {
                    Utilities.logD("Test", "execute onFailure" + s);
                }

                @Override
                public void onStart() {
                    Utilities.logD("Test", "execute onStart");
                }

                @Override
                public void onFinish() {
                    Utilities.logD("Test", "execute onFinish");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            Utilities.logD("Test", "Service err : " + e.toString());
            stopSelf();
        } catch (Exception e) {
            Utilities.logD("Test", "Service err : " + e.toString());
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        if(ffmpeg.isFFmpegCommandRunning()){
            ffmpeg.killRunningProcesses();
        }

        Utilities.logD("Test", "destroy service");

        super.onDestroy();
    }

    private String makeCmd(String startTime, String inputFile, String duration, String outputFile){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(inputFile);
        String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if(rotation == null){
            rotation = "0";
        }
        String cmd = "-ss " + startTime + " -i " + inputFile + " -preset ultrafast -vf scale=";
        switch (rotation){
            case "0" :
            case "180" :
                if(Integer.parseInt(width) < 1280){
                    cmd += height + ":" + width;
                }else{
                    cmd += "720:1280";
                }
                break;
            case "90" :
            case "270" :
                if(Integer.parseInt(width) < 1280){
                    cmd += width + ":" + height;
                }else{
                    cmd += "1280:720";
                }
                break;
        }
        cmd += " -strict experimental -t " + duration + " -y " + outputFile;
//        return "-ss " + startTime + " -i " + inputFile + " -preset ultrafast -vf scale=1280:720 -strict experimental -t " + duration + " -y " + outputFile;
        return cmd;
    }

    private void uploadS3(){
        File output = new File(outputFile);
        final String uploadName = Utilities.transTimeFormatFromTimeMillis(System.currentTimeMillis(),"yyyy/MM/dd/HHmmss_") + UserInfo.getUserAndroidId(this);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                this,
                "us-east-1:e4dbf036-7fe6-4a50-9883-82b220f6cab4", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_WEST_1));

        TransferUtility transferUtility = new TransferUtility(s3, this);

        TransferObserver observer = transferUtility.upload(
                "project-audi/video",     /* The bucket to upload to */
                uploadName,    /* The key for the uploaded object */
                output        /* The file where the data to upload exists */
        );

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Utilities.logD("Test", "id : " + Integer.toString(id) + "////state : " + state.toString());
                if(state.toString().equals(TransferState.COMPLETED.toString())){
                    insertMedia("https://d2w1dgw1e9awhj.cloudfront.net/video/" + uploadName);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Utilities.logD("Test", "id : " + Integer.toString(id) + "////bytesCurrent / bytesTotal : " + Long.toString(bytesCurrent) + " / " + Long.toString(bytesTotal));
            }

            @Override
            public void onError(int id, Exception ex) {
                Utilities.logD("Test", "id : " + Integer.toString(id) + "////Exception : " + ex.toString());
                stopSelf();
            }
        });
    }

    private void insertMedia(String s3Url){
        Network network = new Network(this, "txMedia") {
            @Override
            protected void processFinish(JsonNode result) {
                if("Y".equals(result.get("RTN_VAL").asText())){
                    //TODO notification 띄우기
                    notifyFinishUpload();
                }else{

                }
                stopSelf();
            }
        };

        String url = "v001/home/media";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("FLG", "I");
        serverCommunicator.addData("MEDIA_NO", "");
        serverCommunicator.addData("MEDIA_CONT", text);
//        serverCommunicator.addData("MEDIA_TM", duration);
        serverCommunicator.addData("MEDIA_URL", s3Url);
        serverCommunicator.addData("MEDIA_IMG_URL", "");
        serverCommunicator.addData("SHARE_TP", "");
        serverCommunicator.addData("HASHTAG", convertHashList(hashtagList));
        serverCommunicator.addData("USERTAG", "");

        serverCommunicator.communicate();
    }

    private ArrayList<HashMap<String, String>> convertHashList(ArrayList<String> hashtagList){
        ArrayList<HashMap<String, String>> hashMapList = new ArrayList<>();
        HashMap<String, String> tempMap;
        for(String hashtag : hashtagList){
            tempMap = new HashMap<>();
            tempMap.put("HASHTAG", hashtag);
            hashMapList.add(tempMap);
        }
        return hashMapList;
    }

    private void notifyFinishUpload(){
        NotificationManager notificationManager= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class); //인텐트 생성.


        Notification.Builder builder = new Notification.Builder(this);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent는 일회용 인텐트 같은 개념입니다.
        //FLAG_UPDATE_CURRENT - > 만일 이미 생성된 PendingIntent가 존재 한다면, 해당 Intent의 내용을 변경함.
        //FLAG_CANCEL_CURRENT - .이전에 생성한 PendingIntent를 취소하고 새롭게 하나 만든다.
        //FLAG_NO_CREATE -> 현재 생성된 PendingIntent를 반환합니다.
        //FLAG_ONE_SHOT - >이 플래그를 사용해 생성된 PendingIntent는 단 한번밖에 사용할 수 없습니다
        builder.setSmallIcon(R.mipmap.ic_sero).setTicker("upload finished").setWhen(System.currentTimeMillis())
                .setContentTitle("upload finished").setContentText("upload finished")
                .setDefaults(Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
        //해당 부분은 API 4.1버전부터 작동합니다.
//setSmallIcon - > 작은 아이콘 이미지
//setTicker - > 알람이 출력될 때 상단에 나오는 문구.
//setWhen -> 알림 출력 시간.
//setContentTitle-> 알림 제목
//setConentText->푸쉬내용
        notificationManager.notify(1, builder.build()); // Notification send
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
