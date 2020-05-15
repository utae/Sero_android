package net.videofactory.new_audi.home;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

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
 * Created by Utae on 2016-06-09.
 */
public class HomeCardVideoListFragment extends RefreshFragment {

    private String title, cardType;

    @BindView(R.id.homeCardVideoListTitle) TextView titleView;
    @BindView(R.id.homeCardVideoList) GridViewWithHeaderAndFooter thumbnailList;
    @BindView(R.id.homeCardVideoListBackButton) ImageButton backButton;

    private VideoThumbnailListAdapter thumbnailListAdapter;

    private ArrayList<VideoInfo> videoInfoList;
    private String IDX = null;
    private String MAX_IDX = null;

    private OnVideoThumbnailClickListener onVideoThumbnailClickListener;
    private OnBackButtonClickListener onBackButtonClickListener;

    public static HomeCardVideoListFragment create(String title, String cardType){
        HomeCardVideoListFragment homeCardVideoListFragment = new HomeCardVideoListFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("cardType", cardType);
        homeCardVideoListFragment.setArguments(args);
        return homeCardVideoListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title");
        cardType = getArguments().getString("cardType");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_card_video_list, container, false);

        ButterKnife.bind(this, view);

        videoInfoList = new ArrayList<>();

        titleView.setText(title);

        selectHomeCardHeaderVideo(cardType);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onBackButtonClickListener != null){
                    onBackButtonClickListener.onBackButtonClick();
                }
            }
        });

        return view;
    }

    private void initListView(){
        thumbnailListAdapter = new VideoThumbnailListAdapter(getContext(), videoInfoList);

        thumbnailList.setAdapter(thumbnailListAdapter);

        thumbnailList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        thumbnailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            thumbnailList.setOnScrollListener(new AbsListView.OnScrollListener() {
                private boolean lastItemVisibleFlag = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag){
                        selectHomeCardHeaderVideo(cardType);
                        setAutoListUpdate(false);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });
        }else{
            thumbnailList.setOnScrollListener(null);
        }

    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.onBackButtonClickListener = onBackButtonClickListener;
    }

    public void setOnVideoThumbnailClickListener(OnVideoThumbnailClickListener onVideoThumbnailClickListener) {
        this.onVideoThumbnailClickListener = onVideoThumbnailClickListener;
    }

    private void selectHomeCardHeaderVideo(String cardType){
        selectHomeCardHeaderVideo(cardType, false);
    }

    private void selectHomeCardHeaderVideo(String cardType, final boolean refresh){
        if(refresh){
            setRefreshing(true);
            IDX = null;
            MAX_IDX = null;
        }

        Network network = new Network(getContext(), "txOnClick") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        if(!videoInfoList.isEmpty()){
                            videoInfoList.clear();
                        }

                        MAX_IDX = data.get("TOT_CNT").asText();
                    }

                    IDX = data.get("IDX").asText();

                    for(JsonNode videoData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                        videoInfoList.add(new VideoInfo(videoData.get("MEDIA_IMG_URL").asText(), videoData.get("MEDIA_NO").asText(), videoData.get("MEDIA_URL").asText()));
                    }
                }

                if(thumbnailListAdapter == null){
                    initListView();
                }else{
                    thumbnailListAdapter.notifyDataSetChanged();
                }

                setAutoListUpdate(!IDX.equals(MAX_IDX));

                if(refresh){
                    setRefreshing(false);
                }
            }
        };

        String url = "v001/home/card";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("CARD_TP", cardType);
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!thumbnailList.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectHomeCardHeaderVideo(cardType, true);
            }
        }else{
            thumbnailList.smoothScrollToPosition(0);
        }
    }
}
