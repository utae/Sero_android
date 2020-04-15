package net.videofactory.new_audi.camera_upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import net.videofactory.new_audi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-29.
 */
public class VideoEditActivity extends AppCompatActivity implements VideoEditControllerFragment.OnVideoEditControllerListener{

    private String inputVideoPath;

    private VideoEditControllerFragment controllerFragment;

    private FilterPanelFragment filterPanelFragment;

    private MusicPanelFragment musicPanelFragment;

    private boolean isMusic;

    @Bind(R.id.editVideoView) VideoView editVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);

        ButterKnife.bind(this);

        isMusic = false;

        inputVideoPath = getIntent().getStringExtra("inputVideoPath");

        if(inputVideoPath == null){
            finish();
        }

        editVideoView.setVideoPath(inputVideoPath);

        editVideoView.seekTo(100);

        controllerFragment = VideoEditControllerFragment.create(VideoEditControllerFragment.VIDEO_EDIT_FILTER_MUSIC);

        getSupportFragmentManager().beginTransaction().replace(R.id.editControllerContainer, controllerFragment).commit();
    }

    @Override
    public void onPlayButtonClicked() {
        if(editVideoView.isPlaying()){
            editVideoView.pause();
        }else{
            editVideoView.start();
        }
    }

    @Override
    public void onPrevButtonClicked() {
        if(isMusic){
            isMusic = false;
        }else{
            finish();
        }
    }

    @Override
    public void onNextButtonClicked() {
        if(isMusic){
//            Intent intent = new Intent(this, VideoUploadActivity.class);
//            startActivity(intent);
        }else{
            isMusic = true;
        }
    }
}
