package net.videofactory.new_audi.channel_tag;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
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
import net.videofactory.new_audi.main.OnBackButtonClickListener;
import net.videofactory.new_audi.video.OnVideoThumbnailClickListener;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.video.VideoInfo;
import net.videofactory.new_audi.video.VideoThumbnailListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by Utae on 2016-06-16.
 */
public class TagPageFragment extends RefreshFragment {

    private String hashTag;
    private boolean follow;

    @BindView(R.id.tagPageTitle) TextView title;
    @BindView(R.id.tagPageVideoList) GridViewWithHeaderAndFooter videoList;
    @BindView(R.id.tagPageBackButton) ImageButton backButton;

    private HeaderHolder headerHolder;
    private ArrayList<VideoInfo> videoInfoList;
    private String IDX = null;
    private String MAX_IDX = null;

    private VideoThumbnailListAdapter videoThumbnailListAdapter;
    private OnVideoThumbnailClickListener onVideoThumbnailClickListener;
    private OnBackButtonClickListener onBackButtonClickListener;

    public static TagPageFragment create(String hashTag){
        TagPageFragment tagPageFragment = new TagPageFragment();
        Bundle args = new Bundle();
        args.putString("hashTag", hashTag);
        tagPageFragment.setArguments(args);
        return tagPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hashTag = getArguments().getString("hashTag");
        videoInfoList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_page, container, false);

        ButterKnife.bind(this, view);

        View header = inflater.inflate(R.layout.header_tag_page, null, false);

        headerHolder = new HeaderHolder(header);

        videoList.addHeaderView(header);

        selectTagInfo();

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
                        selectTagInfo(false);
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
                if (onBackButtonClickListener != null) {
                    onBackButtonClickListener.onBackButtonClick();
                }
            }
        });
    }

    private void initFollowButton(){
        headerHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followTag();
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

    public void setOnBackButtonClickListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.onBackButtonClickListener = onBackButtonClickListener;
    }

    public void setOnVideoThumbnailClickListener(OnVideoThumbnailClickListener onVideoThumbnailClickListener) {
        this.onVideoThumbnailClickListener = onVideoThumbnailClickListener;
    }

    private void selectTagInfo(){
        selectTagInfo(true);
    }

    private void selectTagInfo(final boolean refresh){
        if(refresh) {
            setRefreshing(true);
            IDX = null;
            MAX_IDX = null;
        }

        Network network = new Network(getContext(), "txGetHashtagPage") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){
                    JsonNode tagInfo = Utilities.jsonParse(data.get("HASHTAG").asText());

                    if(refresh){
                        if(!videoInfoList.isEmpty()){
                            videoInfoList.clear();
                        }

                        title.setText(hashTag);
                        headerHolder.videoCount.setText(tagInfo.get("MEDIA_CNT").asText());
                        headerHolder.followerCount.setText(tagInfo.get("FOLLOWER_CNT").asText());

                        follow = "Y".equals(tagInfo.get("CHK_FOLLOW").asText());

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

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("HASHTAG", hashTag);
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    private void followTag(){
        Network network = new Network(getContext(), "txFollowHt") {
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
        serverCommunicator.addData("HASHTAG", hashTag);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!videoList.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectTagInfo(true);
            }
        }else{
            videoList.smoothScrollToPosition(0);
        }
    }

    static class HeaderHolder{

        @BindView(R.id.tagPageVideoCount) TextView videoCount;
        @BindView(R.id.tagPageFollowerCount) TextView followerCount;
        @BindView(R.id.tagPageFollowButton) Button followButton;

        public HeaderHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
