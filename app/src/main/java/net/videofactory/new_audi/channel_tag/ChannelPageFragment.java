package net.videofactory.new_audi.channel_tag;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.main.OnBackButtonClickListener;
import net.videofactory.new_audi.video.OnVideoThumbnailClickListener;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.video.VideoInfo;
import net.videofactory.new_audi.video.VideoThumbnailListAdapter;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by Utae on 2016-06-16.
 */
public class ChannelPageFragment extends RefreshFragment {

    private String userNum;
    private boolean follow;
    private boolean block;

    @Bind(R.id.channelPageTitle) TextView title;
    @Bind(R.id.channelPageVideoList) GridViewWithHeaderAndFooter videoList;
    @Bind(R.id.channelPageBackButton) ImageButton backButton;
    @Bind(R.id.channelShowMoreButton) ImageButton showMoreButton;

    private HeaderHolder headerHolder;
    private ArrayList<VideoInfo> videoInfoList;
    private String IDX = null;
    private String MAX_IDX = null;
    private VideoThumbnailListAdapter videoThumbnailListAdapter;
    private OnVideoThumbnailClickListener onVideoThumbnailClickListener;
    private OnBackButtonClickListener onBackButtonClickListener;

    public static ChannelPageFragment create(String userNum){
        ChannelPageFragment channelPageFragment = new ChannelPageFragment();
        Bundle args = new Bundle();
        args.putString("userNum", userNum);
        channelPageFragment.setArguments(args);
        return channelPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userNum = getArguments().getString("userNum");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_page, container, false);

        ButterKnife.bind(this, view);

        View header = inflater.inflate(R.layout.header_channel_page, null, false);

        headerHolder = new HeaderHolder(header);

        videoList.addHeaderView(header);

        videoInfoList = new ArrayList<>();

        selectChannelInfo();

        initButtons();

        return view;
    }

    private void initListView(){
        videoThumbnailListAdapter = new VideoThumbnailListAdapter(getContext(), videoInfoList);

        videoList.setAdapter(videoThumbnailListAdapter);

        videoList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            videoList.setOnScrollListener(new AbsListView.OnScrollListener() {
                private boolean lastItemVisibleFlag = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag){
                        selectChannelInfo(false);
                        setAutoListUpdate(false);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });
        }else{
            videoList.setOnScrollListener(null);
        }
    }

    private void initButtons(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onBackButtonClickListener != null){
                    onBackButtonClickListener.onBackButtonClick();
                }
            }
        });

        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelShowMoreDialog channelShowMoreDialog = ChannelShowMoreDialog.create(userNum, block);
                channelShowMoreDialog.setChannelShowMoreListener(new ChannelShowMoreDialog.ChannelShowMoreListener() {
                    @Override
                    public void onBlockUser(boolean block) {
                        setBlock(block);
                    }
                });
                channelShowMoreDialog.show(getFragmentManager(), "channelShowMoreDialog");
            }
        });
    }

    private void initFollowButton(){
        headerHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followChannel();
            }
        });
    }

    private void toggleFollowButton(boolean follow){
        if(follow){
            headerHolder.followButton.setText("FOLLOWING");
            headerHolder.followButton.setTextColor(Color.parseColor("#ffffff"));
            headerHolder.followButton.setSelected(true);
        }else{
            headerHolder.followButton.setText("FOLLOW");
            headerHolder.followButton.setTextColor(Color.parseColor("#aaaaaa"));
            headerHolder.followButton.setSelected(false);
        }
    }

    private void setBlock(boolean block) {
        this.block = block;
    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.onBackButtonClickListener = onBackButtonClickListener;
    }

    public void setOnVideoThumbnailClickListener(OnVideoThumbnailClickListener onVideoThumbnailClickListener) {
        this.onVideoThumbnailClickListener = onVideoThumbnailClickListener;
    }

    private void selectChannelInfo(){
        selectChannelInfo(true);
    }

    private void selectChannelInfo(final boolean refresh){
        if(refresh){
            setRefreshing(true);
            IDX = null;
            MAX_IDX = null;
        }

        Network network = new Network(getActivity(), "getChannelPage") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    JsonNode channelInfo = Utilities.jsonParse(data.get("CHANNEL").asText());

                    if(refresh){
                        if(!videoInfoList.isEmpty()){
                            videoInfoList.clear();
                        }

                        title.setText(channelInfo.get("NICKNAME").asText());
                        headerHolder.videoCount.setText(channelInfo.get("MEDIA_CNT").asText());
                        headerHolder.followerCount.setText(channelInfo.get("FOLLOWER_CNT").asText());
                        headerHolder.name.setText(channelInfo.get("NAME").asText());
                        headerHolder.intro.setText(channelInfo.get("MY_STORY").asText());

                        if(channelInfo.get("IMG_URL") == null || "".equals(channelInfo.get("IMG_URL").asText())){
                            headerHolder.profile.setImageResource(R.drawable.ic_profile_default);
                        }else{
                            if(Utilities.cancelPotentialTask(channelInfo.get("IMG_URL").asText(), headerHolder.profile)){
                                ImagePickerTask profileImgPickerTask = new ImagePickerTask(headerHolder.profile);
                                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, profileImgPickerTask);
                                headerHolder.profile.setImageLoadingDrawable(loadingDrawable);
                                profileImgPickerTask.execute(channelInfo.get("IMG_URL").asText());
                            }
                        }

                        follow = "Y".equals(channelInfo.get("CHK_FOLLOW").asText());

                        block = "Y".equals(data.get("BLOCK_YN").asText());

                        if(follow){
                            toggleFollowButton(true);
                        }

                        initFollowButton();

                        MAX_IDX = data.get("MAX_IDX").asText();
                    }

                    IDX = data.get("IDX").asText();


                    for(JsonNode videoInfo : Utilities.jsonParse(data.get("VIDEOS").asText())){
                        videoInfoList.add(new VideoInfo(videoInfo.get("IMG_URL").asText(), videoInfo.get("MEDIA_NO").asText(), videoInfo.get("MEDIA_URL").asText()));
                    }

                    if(videoInfoList.isEmpty()){
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        videoList.setLayoutParams(layoutParams);
                    }

                    if(videoThumbnailListAdapter == null){
                        initListView();
                    }else{
                        videoThumbnailListAdapter.notifyDataSetChanged();
                    }

                    setAutoListUpdate(!IDX.equals(MAX_IDX));

                    if(refresh){
                        setRefreshing(false);
                    }

                }
            }
        };

        String url = "v001/home/view";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", userNum);
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    private void followChannel(){
        Network network = new Network(getContext(), "txFollowCh") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        follow = !follow;
                        toggleFollowButton(follow);
                    }else{
                        Toast.makeText(getContext(), result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        String url = "v001/follow";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("FOLLOWING", userNum);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!videoList.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectChannelInfo(true);
            }
        }else{
            videoList.smoothScrollToPosition(0);
        }
    }

    static class HeaderHolder{

        @Bind(R.id.channelPageProfile) CircleLoadingImageView profile;
        @Bind(R.id.channelPageVideoCount) TextView videoCount;
        @Bind(R.id.channelPageFollowerCount) TextView followerCount;
        @Bind(R.id.channelPageName) TextView name;
        @Bind(R.id.channelPageFollowButton) Button followButton;
        @Bind(R.id.channelPageIntro) TextView intro;

        public HeaderHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
