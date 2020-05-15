package net.videofactory.new_audi.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Utae on 2016-10-19.
 */

public class AudiFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = "AudiFirebaseMsgService";

    String imgsrc = "";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        Utilities.logD(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Utilities.logD(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Utilities.logD(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //sendNotification(remoteMessage.getNotification().getBody()); // Body에 적힌 대로, 서버전달시 주석함
        imgsrc = remoteMessage.getData().get("imgsrc");
        sendNotification(remoteMessage.getData().get("message")); // FCM에 메시지로 전달된 데이터를 문자열로 가져옴(이미지 전달 시 필요)
    }

    // messageBody를 핸드폰 상단 화면에 띄움
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class); // 클릭시 전달될 인텐트, 기본적으로 어플실행이 된상태
        // 에서 클릭해야 그 액티비티로 전달
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if(messageBody != null){
            Utilities.logD(TAG, messageBody);
        }

        HttpURLConnection connection = null;
        try {
            // 이미지 주소를 메시지로 넘겨 받을 경우 HTTP연결하여 받은 뒤 Bitmap으로 전환한 뒤, 이미지 생성
            URL url = new URL(imgsrc);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                    .bigPicture(myBitmap)
                    .setBigContentTitle("알림이 도착했습니다.")
                    .setSummaryText(messageBody);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_sero)
                    .setStyle(style)
                    .setContentTitle("알림이 도착했습니다.")
                    .setContentText("밑으로 드래그해주세요.")
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            notificationManager.notify(0
                    // ID of notification
                    , notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(connection!=null)
                connection.disconnect();
        }



        /* 메시지만 처리할 경우, HttpURLConnection이 필요 없으므로 아래와 같이 사용한다.
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentTitle(messageBody)
                .setContentIntent(pendingIntent);


        // set

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0
        // ID of notification
        , notificationBuilder.build());
        */
    }

}
