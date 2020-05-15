package net.videofactory.new_audi.mypage;


import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.video.OnVideoThumbnailClickListener;
import net.videofactory.new_audi.video.VideoInfo;
import net.videofactory.new_audi.video.VideoThumbnailListAdapter;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.GridViewWithHeaderAndFooter;


/**
 * Created by Utae on 2016-01-14.
 */
public class MyPageFragment extends RefreshFragment{

    @BindView(R.id.myName) TextView nameView;
    @BindView(R.id.settingButton) ImageButton settingButton;
    @BindView(R.id.myVideoList) GridViewWithHeaderAndFooter videoListView;

    private ArrayList<VideoInfo> videoInfoList;
    private String IDX = null;
    private String MAX_IDX = null;

    private VideoThumbnailListAdapter videoListAdapter;
    private HeaderHolder headerHolder;
    private OnVideoThumbnailClickListener onVideoThumbnailClickListener;
    private MyPageListener myPageListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoInfoList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        ButterKnife.bind(this, view);

        View header = inflater.inflate(R.layout.header_my_page, null, false);

        headerHolder = new HeaderHolder(header);

        videoListView.addHeaderView(header);

        selectMyInfo(true);

        initButtons();

        return view;
    }

    private void initListView(){
        videoListAdapter = new VideoThumbnailListAdapter(getContext(), videoInfoList);

        videoListView.setAdapter(videoListAdapter);

        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onVideoThumbnailClickListener != null) {
                    onVideoThumbnailClickListener.onVideoThumbnailClick(videoInfoList, position);
                }
            }
        });
    }

    private void setAutoListUpdate(boolean autoListUpdate){
        if(autoListUpdate){
            videoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private boolean lastItemVisibleFlag = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag){
                        selectMyInfo(false);
                        setAutoListUpdate(false);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });
        }else{
            videoListView.setOnScrollListener(null);
        }
    }

    private void initButtons(){
        headerHolder.followerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myPageListener != null) {
                    myPageListener.onFollowerCLick();
                }
            }
        });

        headerHolder.followingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myPageListener != null){
                    myPageListener.onFollowingClick();
                }
            }
        });

        headerHolder.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myPageListener != null){
                    myPageListener.onEditProfileClick();
                }
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myPageListener != null){
                    myPageListener.onSettingClick();
                }
            }
        });
    }

    public void setMyPageListener(MyPageListener myPageListener) {
        this.myPageListener = myPageListener;
    }

    public void setOnVideoThumbnailClickListener(OnVideoThumbnailClickListener onVideoThumbnailClickListener) {
        this.onVideoThumbnailClickListener = onVideoThumbnailClickListener;
    }

    public void refreshMyInfo(){
        selectMyInfo(true);
    }

    private void selectMyInfo(final boolean refresh){
        if(refresh){
            setRefreshing(true);
            IDX = null;
            MAX_IDX = null;
        }

        Network network = new Network(getContext(), "getChannelPage") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){
                    JsonNode myInfo = Utilities.jsonParse(data.get("CHANNEL").asText());

                    if(refresh){
                        if(!videoInfoList.isEmpty()){
                            videoInfoList.clear();
                        }

                        nameView.setText(myInfo.get("NAME").asText());
                        headerHolder.videoCountView.setText(myInfo.get("MEDIA_CNT").asText());
                        headerHolder.followerCountView.setText(myInfo.get("FOLLOWER_CNT").asText());
                        headerHolder.followingCountView.setText(myInfo.get("FOLLOWING_CNT").asText());
                        headerHolder.nickNameView.setText(myInfo.get("NICKNAME").asText());
                        headerHolder.introView.setText(myInfo.get("MY_STORY").asText());

                        if(myInfo.get("IMG_URL") == null || "".equals(myInfo.get("IMG_URL").asText())){
                            headerHolder.profileImgView.setImageResource(R.drawable.ic_profile_default);
                        }else{
                            if(Utilities.cancelPotentialTask(myInfo.get("IMG_URL").asText(), headerHolder.profileImgView)){
                                ImagePickerTask profileImgPickerTask = new ImagePickerTask(headerHolder.profileImgView);
                                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, profileImgPickerTask);
                                headerHolder.profileImgView.setImageLoadingDrawable(loadingDrawable);
                                profileImgPickerTask.execute(myInfo.get("IMG_URL").asText());
                            }
                        }

                        MAX_IDX = data.get("MAX_IDX").asText();
                    }

                    IDX = data.get("IDX").asText();

                    for(JsonNode videoInfo : Utilities.jsonParse(data.get("VIDEOS").asText())){
                        VideoInfo vInfo = new VideoInfo(videoInfo.get("IMG_URL").asText(), videoInfo.get("MEDIA_NO").asText(), videoInfo.get("MEDIA_URL").asText());
                        vInfo.setMine(true);
                        videoInfoList.add(vInfo);
                    }

                    if(videoInfoList.isEmpty()){
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        videoListView.setLayoutParams(layoutParams);
                    }

                    if(videoListAdapter == null){
                        initListView();
                    }else{
                        videoListAdapter.notifyDataSetChanged();
                    }

                    setAutoListUpdate(!IDX.equals(MAX_IDX));

                    setRefreshing(false);
                }
            }
        };

        String url = "v001/home/view";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!videoListView.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectMyInfo(true);
            }
        }else{
            videoListView.smoothScrollToPosition(0);
        }
    }

    static class HeaderHolder{
        @BindView(R.id.myProfileImg) CircleLoadingImageView profileImgView;
        @BindView(R.id.myVideoCount) TextView videoCountView;
        @BindView(R.id.myFollowerCount) TextView followerCountView;
        @BindView(R.id.myFollowingCount) TextView followingCountView;
        @BindView(R.id.myNickName) TextView nickNameView;
        @BindView(R.id.myEditProfile) Button editProfile;
        @BindView(R.id.myIntro) TextView introView;
        @BindView(R.id.myFollowerContainer) LinearLayout followerContainer;
        @BindView(R.id.myFollowingContainer) LinearLayout followingContainer;

        public HeaderHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface MyPageListener {
        void onFollowerCLick();
        void onFollowingClick();
        void onEditProfileClick();
        void onSettingClick();
    }
}
