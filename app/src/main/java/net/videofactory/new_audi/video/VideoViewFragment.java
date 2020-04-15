package net.videofactory.new_audi.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fasterxml.jackson.databind.JsonNode;
import com.nineoldandroids.animation.Animator;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.audi_video_view.AudiMediaController;
import net.videofactory.new_audi.custom_view.audi_video_view.AudiVideoView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-02.
 */
public class VideoViewFragment extends Fragment {

    @Bind(R.id.videoPageVideoView) AudiVideoView videoView;
    @Bind(R.id.videoPageLikeImg) ImageView likeImgView;
    @Bind(R.id.videoPageProgressBar) ProgressBar progressBar;

    private View rootView;

    private int position;

    private String videoUrl, videoNum;

    private VideoDetailInfo videoDetailInfo;

    private AudiMediaController audiMediaController;

    private boolean isFooterShow;

    private boolean isUserShowing = false;

    private VideoViewFragmentListener videoViewFragmentListener;

    private boolean isReady = false;

    public static VideoViewFragment create(int position, String videoUrl, String videoNum){
        //Fragment를 생성할 때, argument로 videoUrl을 넣어서 생성하도록 설정
        VideoViewFragment videoViewFragment = new VideoViewFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("videoUrl", videoUrl);
        args.putString("videoNum", videoNum);
        videoViewFragment.setArguments(args);
        return videoViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Adapter에서 설정한 videoUrl을 멤머변수로 가져옴
        position = getArguments().getInt("position");
        videoUrl = getArguments().getString("videoUrl");
        videoNum = getArguments().getString("videoNum");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //새로운 View 객체를 Layoutinflater를 이용해서 생성
        //만들어질 View의 설계는 res폴더>>layout폴더>>viewpager_childview.xml 레이아웃 파일 사용
        rootView = inflater.inflate(R.layout.page_main_view_pager, container, false);

        ButterKnife.bind(this, rootView);

        selectVideoDetailInfo();

        //VideoView에 activity추가
        videoView.setActivity(getActivity());

        //VideoView에 mediaNum추가
        videoView.setMediaNum(videoNum);

        //VideoView에 Adapter에서 가져온 videoUrl을 적용
        videoView.setVideoURI(Uri.parse(videoUrl));

        //프로그레스바 띄우기
        progressBar.setVisibility(View.VISIBLE);

        //다음 동영상 thumbnail 띄워놓기
        videoView.seekTo(1000);

        audiMediaController = new AudiMediaController(getContext());

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.INVISIBLE);
                if(isUserShowing){
                    videoView.start();
                    if(audiMediaController != null) {
                        audiMediaController.show();
                    }
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(videoViewFragmentListener != null){
                    videoViewFragmentListener.onCompletion(videoView, position);
                }
            }
        });

        return rootView;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        isUserShowing = menuVisible;
        if(!isUserShowing && videoView != null && videoView.isPlaying()){
            videoView.pause();
        }
    }

    public String getVideoNum() {
        return videoNum;
    }

    public AudiVideoView getVideoView(){
       return videoView;
    }

    public AudiMediaController getAudiMediaController() {
        return audiMediaController;
    }

    public void setMediaController(VideoDetailInfo videoDetailInfo){
        videoView.setMediaController(audiMediaController, videoDetailInfo);
    }

    public void setVideoViewFragmentListener(VideoViewFragmentListener videoViewFragmentListener) {
        this.videoViewFragmentListener = videoViewFragmentListener;
    }

    public void startVideo(){
        videoView.start();
    }

    public void pauseVideo(){
        videoView.pause();
    }

    public void stopVideo(){
        videoView.seekTo(0);
        videoView.pause();
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }

    public VideoDetailInfo getVideoDetailInfo() {
        return videoDetailInfo;
    }

    public void removeMediaController(){
//        if(audiMediaController.isShowing()){
//            audiMediaController.hide();
//        }
        if(audiMediaController != null){
            if(audiMediaController.getParent() != null){
                if(((ViewGroup)audiMediaController.getParent()).indexOfChild(audiMediaController) != -1){
                    ((ViewGroup)audiMediaController.getParent()).removeView(audiMediaController);
                }
            }
        }
    }

    public VideoInfo getVideoInfo(){
        return new VideoInfo(null, videoNum, videoUrl);
    }

    public void likeVideo(){
        if(isReady){
            videoDetailInfo.switchIsLike();

            if(videoDetailInfo.isLike()){
                likeImgView.setImageResource(R.drawable.ic_like_in_video_true);
            }else{
                likeImgView.setImageResource(R.drawable.ic_like_in_video_false);
            }
            likeImgView.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(1000).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    YoYo.with(Techniques.FadeOut).duration(1000).playOn(likeImgView);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(likeImgView);
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    likeVideo();
                }
            }, 1000);
        }
    }

    private void selectVideoDetailInfo(){
        Network network = new Network(getContext(), "getMedia") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                    if(data != null){
                        videoDetailInfo = new VideoDetailInfo();

                        videoDetailInfo.setVideoNum(videoNum);
                        videoDetailInfo.setVideoIntro(data.get("MEDIA_CONT").asText());
                        videoDetailInfo.setUploaderNum(data.get("USER_NO").asText());
                        videoDetailInfo.setLikeCount(data.get("LIKE_CNT").asText());
                        videoDetailInfo.setCommentCount(data.get("REPLY_CNT").asText());
                        videoDetailInfo.setUploaderNickName(data.get("NICKNAME").asText());
                        videoDetailInfo.setUploaderName(data.get("NAME").asText());
                        videoDetailInfo.setUploaderProfileUrl(data.get("IMG_URL").asText());
                        videoDetailInfo.setViewCount(data.get("VIEW_CNT").asText());
                        videoDetailInfo.setIsLike("Y".equals(data.get("LIKE_YN").asText()));
                        videoDetailInfo.setIsReport("Y".equals(data.get("REPORT_YN").asText()));

                        Utilities.logD("Time", Utilities.getCurrentTimeMinutes()+"");
                        Utilities.logD("Time", TimeUnit.MINUTES.toMillis(Utilities.getCurrentTimeMinutes())+"");

                        videoDetailInfo.setUploadTime(Utilities.transTimeFormatFromTimeMillis(Utilities.transMinutesToTimeMillis(Utilities.getCurrentTimeMinutes() - Integer.parseInt(data.get("DIFF").asText())), "yyyy.MMMMM.dd HH:mm"));

                        setMediaController(videoDetailInfo);

                        isReady = true;
                    }
                }
            }
        };

        String url = "v001/video";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("MEDIA_NO", videoNum);

        serverCommunicator.communicate();
    }

    public interface VideoViewFragmentListener{
        void onCompletion(AudiVideoView audiVideoView, int position);
        void onMediaControllerListButtonClick();
    }
}
