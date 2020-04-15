package net.videofactory.new_audi.main;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.footer.FooterFragment;
import net.videofactory.new_audi.video.OnVideoThumbnailClickListener;
import net.videofactory.new_audi.video.VideoPagerAdapter;
import net.videofactory.new_audi.video.VideoViewFragment;
import net.videofactory.new_audi.common.AudiGestureListener;
import net.videofactory.new_audi.video.VideoInfo;
import net.videofactory.new_audi.custom_view.audi_layout.AudiLayout;
import net.videofactory.new_audi.custom_view.audi_video_view.AudiMediaController;
import net.videofactory.new_audi.custom_view.audi_video_view.AudiVideoView;

import java.util.ArrayList;

/**
 * Created by Utae on 2015-10-24.
 */
public class MainPresenterImpl implements MainPresenter {

    private AppCompatActivity activity;

    private VideoPagerAdapter videoPagerAdapter;

    private MainView mainView;

    public MainPresenterImpl(AppCompatActivity activity, MainView mainView) {
        this.activity = activity;
        this.mainView = mainView;
    }

    public void initViewPager(ArrayList<VideoInfo> videoInfoList, int position){
//        if(videoPagerAdapter == null){
//            videoPagerAdapter = new VideoPagerAdapter(activity.getSupportFragmentManager(), videoInfoList);
//            videoPagerAdapter.setAudiOnCompletionListener(new AudiVideoViewFragmentListener());
//            mainView.setPagerAdapter(videoPagerAdapter);
//        }else{
//            videoPagerAdapter.setVideoInfoList(videoInfoList);
//            videoPagerAdapter.notifyDataSetChanged();
//        }
        if(videoPagerAdapter != null){
            videoPagerAdapter = null;
        }
        videoPagerAdapter = new VideoPagerAdapter(activity.getSupportFragmentManager(), videoInfoList);
        videoPagerAdapter.setAudiOnCompletionListener(new AudiVideoViewFragmentListener());
        mainView.setPagerAdapter(videoPagerAdapter);

        mainView.mainViewPagerAddOnPageChangeListener(new AudiOnVideoChangeListener());
        mainView.setVideoPage(position, false);
        mainView.setVideoInfoToFooter(videoInfoList.get(position));
    }

    @Override
    public void goToVideo(VideoInfo videoInfo) {
        ArrayList<VideoInfo> videoInfoList = new ArrayList<>();
        videoInfoList.add(videoInfo);
        initViewPager(videoInfoList, 0);
    }

    //서버연결관련 메소드
    private void likeVideo(){
        if(videoPagerAdapter != null && videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) != null && videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) instanceof VideoViewFragment){
            final VideoViewFragment videoViewFragment = (VideoViewFragment)videoPagerAdapter.getItem(mainView.getCurVideoPagePosition());
            Network network = new Network(activity, "txLike") {
                @Override
                protected void processFinish(JsonNode result) {
                    if(result != null){
                        if("Y".equals(result.get("RTN_VAL").asText())){
                            videoViewFragment.likeVideo();
                            mainView.footerLikeVideo();
                        }else{
                            Toast.makeText(activity, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };

            String url = "v001/home/media";

            ServerCommunicator serverCommunicator = new ServerCommunicator(activity, network, url);

            serverCommunicator.addData("MEDIA_NO", videoViewFragment.getVideoNum());
            serverCommunicator.addData("USER_NO", UserInfo.getUserNum());

            serverCommunicator.communicate();
        }
    }

    @Override
    public void onRestartCalled() {
        if(videoPagerAdapter != null) {
            if(videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) instanceof VideoViewFragment){
                VideoViewFragment videoViewFragment = (VideoViewFragment) videoPagerAdapter.getItem(mainView.getCurVideoPagePosition());
                if(videoViewFragment.getVideoView() != null){
                    if(!videoViewFragment.getVideoView().isPlaying()){
                        videoViewFragment.startVideo();
                    }
                }
            }
        }
    }

    @Override
    public void onStopCalled() {
        if(videoPagerAdapter != null) {
            if(videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) instanceof VideoViewFragment){
                VideoViewFragment videoViewFragment = (VideoViewFragment) videoPagerAdapter.getItem(mainView.getCurVideoPagePosition());
                if(videoViewFragment.getVideoView() != null){
                    if(videoViewFragment.getVideoView().isPlaying()){
                        videoViewFragment.pauseVideo();
                    }
                }
            }
        }
    }

    private void switchMediaControllerVisiblity() {
        if(videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) != null && videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) instanceof VideoViewFragment){
            VideoViewFragment videoViewFragment = ((VideoViewFragment)videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()));
            if(videoViewFragment.getAudiMediaController() != null){
                if(videoViewFragment.getAudiMediaController().isShowing()){
                    videoViewFragment.getAudiMediaController().hide();
                }else{
                    videoViewFragment.getAudiMediaController().show();
                }
            }
        }
    }

    //Listeners

    @Override
    public AudiLayout.OnPageChangeListener getAudiOnLayoutPageChangeListener() {
        return new AudiOnLayoutPageChangeListener();
    }

    @Override
    public ViewPager.OnPageChangeListener getAudiOnVideoChangeListener() {
        return new AudiOnVideoChangeListener();
    }

    @Override
    public AudiGestureListener getAudiTapListener() {
        return new AudiTapListener();
    }

    @Override
    public OnVideoThumbnailClickListener getAudiOnVideoThumbnailClickListener() {
        return new AudiOnVideoThumbnailClickListener();
    }

    @Override
    public FooterFragment.OnFooterLikeButtonClickListener getOnFooterLikeButtonClickListener() {
        return new AudiOnFooterLikeButtonClickListener();
    }

    @Override
    public void insertConnectLog() {

    }

    public class AudiVideoViewFragmentListener implements VideoViewFragment.VideoViewFragmentListener {

        @Override
        public void onCompletion(AudiVideoView audiVideoView, int position) {
            if(mainView.isFooterShow()){
                audiVideoView.start();
            }else{
                if(position+1 < videoPagerAdapter.getCount()){
                    mainView.setVideoPage(position + 1, true);
                }else{
                    Toast.makeText(activity,"This is last video.",Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onMediaControllerListButtonClick() {
            mainView.setAudiLayoutPage(0);
        }
    }

    private class AudiOnLayoutPageChangeListener implements AudiLayout.OnPageChangeListener {

        @Override
        public void onPageChange(int pageIndex) {
            VideoViewFragment videoViewFragment = null;
            if(videoPagerAdapter != null && videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) != null && videoPagerAdapter.getItem(mainView.getCurVideoPagePosition()) instanceof VideoViewFragment){
                videoViewFragment = (VideoViewFragment) videoPagerAdapter.getItem(mainView.getCurVideoPagePosition());
            }
            switch(pageIndex){
                case 0 :
                    if(videoViewFragment != null){
                        if(videoViewFragment.getVideoView() != null){
                            if(videoViewFragment.getVideoView().isPlaying()){
                                videoViewFragment.pauseVideo();
                            }
                        }
                    }
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    break;

                case 1 :
                    if(videoViewFragment != null){
                        if(videoViewFragment.getVideoView() != null) {
                            if(!videoViewFragment.getVideoView().isPlaying()){
                                videoViewFragment.startVideo();
                            }
                        }
                    }
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    break;
            }
        }

        @Override
        public void onFooterStateChange(boolean showing) {

        }
    }

    private class AudiOnVideoChangeListener implements ViewPager.OnPageChangeListener {

        private int curPosition = 0;
        private boolean pageChangeFlag;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Utilities.logD("VideoPager", "PageScrolled : " + position);
        }

        @Override
        public void onPageSelected(int nextPosition) {
            Utilities.logD("VideoPager", "PageSelected : " + nextPosition);
            if(curPosition != nextPosition){
                curPosition = nextPosition;
                pageChangeFlag = true;
//                mainView.setFooterStateLoading();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Utilities.logD("VideoPager", "Page : " + curPosition + " ScrollState : " + state);
            VideoViewFragment videoViewFragment = null;
            if(videoPagerAdapter.getItem(curPosition) instanceof VideoViewFragment) {
                videoViewFragment = (VideoViewFragment) videoPagerAdapter.getItem(curPosition);
            }
            if(videoViewFragment != null){
                if(state == ViewPager.SCROLL_STATE_DRAGGING) {
                    if(videoViewFragment.getVideoView() != null){
                        if(videoViewFragment.getVideoView().isPlaying()){
                            videoViewFragment.pauseVideo();
                        }
                    }
                    pageChangeFlag = false;
                }else if(state == ViewPager.SCROLL_STATE_IDLE){
                    if(pageChangeFlag){
                        videoViewFragment.removeMediaController();
                        if(videoViewFragment.getProgressBar().getVisibility() == View.INVISIBLE){
                            videoViewFragment.startVideo();
                            videoViewFragment.getAudiMediaController().show();
                            mainView.setVideoInfoToFooter(videoViewFragment.getVideoInfo());
                        }
                    }else{
                        if(!videoViewFragment.getVideoView().isPlaying()){
                            videoViewFragment.startVideo();
                        }
                    }
                }
            }
        }
    }

    private class AudiTapListener extends AudiGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            likeVideo();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            switchMediaControllerVisiblity();
            //TODO 소프트키보드 없애기
            return true;
        }
    }

    private class AudiOnVideoThumbnailClickListener implements OnVideoThumbnailClickListener {

        @Override
        public void onVideoThumbnailClick(ArrayList<VideoInfo> videoInfoList, int position){
            initViewPager(videoInfoList, position);
            mainView.setAudiLayoutPage(1);
            mainView.setDraggerAvailble();
        }
    }

    private class AudiOnFooterLikeButtonClickListener implements FooterFragment.OnFooterLikeButtonClickListener{

        @Override
        public void onLikeButtonClick() {
            likeVideo();
        }
    }

}
