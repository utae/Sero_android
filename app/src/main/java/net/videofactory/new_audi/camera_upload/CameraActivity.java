package net.videofactory.new_audi.camera_upload;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-29.
 */
public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    @BindView(R.id.cameraTextureView) TextureView cameraTextureView;
    @BindView(R.id.cameraRecordingButton) ImageButton recordingButton;
    @BindView(R.id.cameraGalleryButton) ImageButton galleryButton;
    @BindView(R.id.cameraTransitionButton) ImageButton transitionButton;
    @BindView(R.id.cameraFlashButton) ImageButton flashButton;
    @BindView(R.id.cameraDeleteButton) ImageButton deleteButton;
    @BindView(R.id.cameraEditButton) TextView editButton;
    @BindView(R.id.cameraClipContainer) LinearLayout clipContainer;
    @BindView(R.id.cameraRecordDuration) TextView recordDurationTextView;
    @BindView(R.id.cameraProgressBar) ProgressBar cameraProgressBar;
    @BindView(R.id.cameraCancelButton) TextView cancelButton;

    private Camera camera;
    private Camera.Parameters cameraParameters;
    private MediaRecorder mediaRecorder;
    private boolean isRecording, flash, isDeleting;
    private File dir;
    private int cameraId, duration;
    private Timer recordTimer;
    private RecordTimerTask recordTimerTask;
    private ArrayList<View> clipViewList;
    private ArrayList<String> clipFileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(this);

        checkCamera();

        initVariable();

        cameraTextureView.setSurfaceTextureListener(this);

        initCameraButton();
    }

    private void initVariable(){
        isRecording = flash = isDeleting = false;

        cameraId = 0;

        recordTimer = new Timer();

        clipViewList = new ArrayList<>();

        clipFileList = new ArrayList<>();
    }

    private boolean hasCamera(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }else{
            return false;
        }
    }

    private void checkCamera(){
        if(hasCamera(this)){
            if(Camera.getNumberOfCameras() < 2){
                transitionButton.setVisibility(View.INVISIBLE);
            }
        }else{
            finish();
        }
    }

    private void initCameraButton(){
        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    isRecording = false;
                    recordingButton.setImageResource(R.drawable.btn_recording_start);
                    mediaRecorder.stop();
                    stopTimer();
                    mediaRecorder.reset();
                    setCameraButtonVisibility(View.VISIBLE);
                    initRecorder();
                } else {
                    new MediaPrepareTask().execute(null, null, null);
                }
            }
        });

        transitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    releaseMediaRecorder();
                    releaseCamera();
                    switch (cameraId) {
                        case 0:
                            cameraId = 1;
                            flashButton.setVisibility(View.GONE);
                            break;

                        case 1:
                            cameraId = 0;
                            flashButton.setVisibility(View.VISIBLE);
                            break;
                    }
                    startCameraPreview(cameraId, cameraTextureView.getSurfaceTexture(), flash);
                    initRecorder();
                }
            }
        });

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    releaseMediaRecorder();
                    releaseCamera();
                    if(flash){
                        flashButton.setImageResource(R.drawable.btn_camera_flash_on);
                    }else{
                        flashButton.setImageResource(R.drawable.btn_camera_flash_off);
                    }
                    flash = !flash;
                    startCameraPreview(cameraId, cameraTextureView.getSurfaceTexture(), flash);
                    initRecorder();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraProgressBar.setVisibility(View.VISIBLE);
                new VideoMerger(CameraActivity.this, cameraProgressBar, Integer.toString(duration)).execute(dir);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDeleting) {
                    File clipFile = new File(clipFileList.get(clipFileList.size() - 1));
                    if (clipFile.delete()) {
                        removeClip();
                        isDeleting = false;
                    }
                } else {
                    clipViewList.get(clipViewList.size() - 1).setBackgroundColor(Color.parseColor("#ff3d00"));
                    isDeleting = true;
                }
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (clipFileList.size() != 0){
                    deleteButton.performClick();
                }
                finish();
            }
        });
    }

    private void removeClip(){
        View clip = clipViewList.get(clipViewList.size() - 1);
        int clipDuration = (int) clip.getTag();
        clipContainer.removeViewAt(clipViewList.size() - 1);
        duration -= clipDuration;
        recordDurationTextView.setText(getDurationFormat(duration));
        clipFileList.remove(clipFileList.size() - 1);
        clipViewList.remove(clipViewList.size() - 1);
        deleteButton.setImageResource(R.drawable.btn_camera_delete);
        if(clipFileList.size() == 0){
            deleteButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
            dir.delete();
            dir = null;
        }
    }

    private void initRecorder(){
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        profile.videoFrameWidth = 1280;
        profile.videoFrameHeight = 720;

        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(profile);
    }

    private boolean prepareVideoRecorder(){
        // Step 4: Set output file
        String outputPath = getNewOutputPath();
        clipFileList.add(outputPath);
        mediaRecorder.setOutputFile(outputPath);

        if(cameraId == 0){
            mediaRecorder.setOrientationHint(90);
        }else if(cameraId == 1){
            mediaRecorder.setOrientationHint(270);
        }
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Utilities.logD("Test", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Utilities.logD("Test", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private String getNewOutputPath() {
        Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mi = c.get(Calendar.MINUTE);
        int ss = c.get(Calendar.SECOND);

        String dirName = String.format(Locale.getDefault(), "%02d%02d%02d_%02d%02d%02d", yy, mm, dd, hh, mi, ss);

        if(dir == null){
            dir = new File(this.getExternalFilesDir(null) + "/" + dirName);

            if(!dir.exists()){
                dir.mkdir();
            }
        }

        String output = dir.getPath() + "/" + dir.getName() + "_" + Integer.toString(clipFileList.size()) + ".mp4";


        return output;
    }

    private String getDurationFormat(int duration){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(new Date(duration * 1000));
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseMediaRecorder();

        releaseCamera();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(clipViewList.size() == 0){
            finish();
        }else{
            deleteButton.performClick();
        }
    }

    private void releaseMediaRecorder(){
        if(mediaRecorder != null){
            if(isRecording){
                isRecording = false;
                mediaRecorder.stop();
            }
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
    }

    private void releaseCamera(){
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void startCameraPreview(int cameraId, SurfaceTexture surface, boolean flash){
        camera = Camera.open(cameraId);

        cameraParameters = camera.getParameters();
        camera.setDisplayOrientation(90);
        cameraParameters.setPreviewSize(1280, 720);
        cameraParameters.setRotation(90);
        cameraParameters.setPreviewFpsRange(15000, 30000);
        if(cameraId == 0){
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            if(flash){
                cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }else{
                cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
        }
        camera.setParameters(cameraParameters);
        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            camera.setPreviewTexture(surface);
            camera.startPreview();
        } catch (IOException e) {
            Log.e("Test", "Surface texture is unavailable or unsuitable" + e.getMessage());
        }
    }

    private void setCameraButtonVisibility(int visibility){
        transitionButton.setVisibility(visibility);
        flashButton.setVisibility(visibility);
        deleteButton.setVisibility(visibility);
        editButton.setVisibility(visibility);
    }

    private View addClip(){
        View clip = new View(this);
        LinearLayout.LayoutParams clipLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        clipLayoutParams.weight = 0;
        if(clipViewList.size() != 0){
            clipLayoutParams.leftMargin = (int) getResources().getDimension(R.dimen.space);
        }
        clip.setLayoutParams(clipLayoutParams);
        clip.setBackgroundColor(Color.parseColor("#ffa32d"));
        clipContainer.addView(clip);
        return clip;
    }

    private void startTimer(){
        recordTimerTask = new RecordTimerTask(addClip());
        recordTimer.schedule(recordTimerTask, 1000, 1000);
    }

    private void stopTimer(){
        if(recordTimerTask != null){
            recordTimerTask.cancel();
            recordTimerTask = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startCameraPreview(cameraId, surface, flash);
        initRecorder();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        releaseMediaRecorder();
        releaseCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                //TODO 녹화실패 처리
                CameraActivity.this.finish();
            }
            // inform the user that recording has started
            recordingButton.setImageResource(R.drawable.btn_recording_stop);
            setCameraButtonVisibility(View.INVISIBLE);
            startTimer();
            if(clipFileList.size() == 0){
                galleryButton.setVisibility(View.GONE);
            }

        }
    }

    private class RecordTimerTask extends TimerTask{

        private View clip;
        private int clipDuration;

        public RecordTimerTask(View clip) {
            this.clip = clip;
            clipDuration = 0;
        }

        @Override
        public void run() {
            duration++;
            clipDuration++;
            clipContainer.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) clip.getLayoutParams();
                    layoutParams.weight++;
                    clip.setLayoutParams(layoutParams);
                    recordDurationTextView.setText(getDurationFormat(duration));
                }
            });
        }

        @Override
        public boolean cancel() {
            clip.setTag(clipDuration);
            clip.setBackgroundColor(Color.parseColor("#00909d"));
            clipViewList.add(clip);
            return super.cancel();
        }
    }
}
