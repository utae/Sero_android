package net.videofactory.new_audi.camera_upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.videofactory.new_audi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-29.
 */
public class VideoEditControllerFragment extends Fragment {

    public static final int VIDEO_SELECT = 1;
    public static final int VIDEO_EDIT_FILTER_MUSIC = 2;

    private int delimiter;
    private boolean isPlaying, isMusic;

    private OnVideoEditControllerListener videoEditControllerCallback;

    private ClippingPanelFragment clippingPanelFragment;

    private FilterPanelFragment filterPanelFragment;

    private MusicPanelFragment musicPanelFragment;

    @Bind(R.id.videoEditPlayButton) ImageButton videoEditPlayButton;
    @Bind(R.id.videoEditPrevButton) ImageButton videoEditPrevButton;
    @Bind(R.id.videoEditNextButton) ImageButton videoEditNextButton;
    @Bind(R.id.videoEditCloseButton) ImageButton videoEditCloseButton;

    public static VideoEditControllerFragment create(int delimiter){
        VideoEditControllerFragment videoEditControllerFragment = new VideoEditControllerFragment();
        Bundle args = new Bundle();
        args.putInt("delimiter", delimiter);
        videoEditControllerFragment.setArguments(args);
        return videoEditControllerFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        videoEditControllerCallback = (OnVideoEditControllerListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.delimiter = getArguments().getInt("delimiter");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_edit_controller, container, false);
        ButterKnife.bind(this, view);

        isMusic = false;

        switch (delimiter){
            case VIDEO_SELECT :
                isPlaying = true;
                videoSelectInit();
                break;

            case VIDEO_EDIT_FILTER_MUSIC :
                isPlaying = false;
                videoEditFilterMusicInit();
                break;
        }

        videoEditCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        videoEditPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoEditControllerCallback.onPlayButtonClicked();
                if (isPlaying) {
//                    videoEditPlayButton.setImageResource(R.drawable.btn_clipper_play);
                } else {
//                    videoEditPlayButton.setImageResource(R.drawable.btn_clipper_stop);
                }
                isPlaying = !isPlaying;
            }
        });

        videoEditPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoEditControllerCallback.onPrevButtonClicked();
                if(delimiter == VIDEO_EDIT_FILTER_MUSIC){
                    if(isMusic){
                        isMusic = false;
                        getChildFragmentManager().beginTransaction().replace(R.id.editPanelContainer, filterPanelFragment).commit();
                    }
                }
            }
        });

        videoEditNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoEditControllerCallback.onNextButtonClicked();
                if(delimiter == VIDEO_EDIT_FILTER_MUSIC){
                    if(!isMusic){
                        isMusic = true;
                        getChildFragmentManager().beginTransaction().replace(R.id.editPanelContainer, musicPanelFragment).commit();
                    }
                }
            }
        });

        return view;
    }

    private void videoSelectInit(){
//        videoEditPlayButton.setImageResource(R.drawable.btn_clipper_stop);
//        videoEditPrevButton.setImageResource(R.drawable.btn_clipper_back);
//        videoEditNextButton.setImageResource(R.drawable.btn_camera_clipper_edit);
        clippingPanelFragment = new ClippingPanelFragment();
        getChildFragmentManager().beginTransaction().add(R.id.editPanelContainer, clippingPanelFragment).commit();
    }

    private void videoEditFilterMusicInit(){
//        videoEditPlayButton.setImageResource(R.drawable.btn_clipper_play);
//        videoEditPrevButton.setImageResource(R.drawable.btn_clipper_back);
//        videoEditNextButton.setImageResource(R.drawable.btn_video_edit_select);
        filterPanelFragment = new FilterPanelFragment();

        musicPanelFragment = new MusicPanelFragment();

        getChildFragmentManager().beginTransaction().add(R.id.editPanelContainer, filterPanelFragment).commit();
    }

    public interface OnVideoEditControllerListener{
        void onPlayButtonClicked();
        void onPrevButtonClicked();
        void onNextButtonClicked();
    }
}
