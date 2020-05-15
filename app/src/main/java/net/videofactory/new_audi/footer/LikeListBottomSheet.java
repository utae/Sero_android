package net.videofactory.new_audi.footer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.channel_tag.FollowChannelListAdapter;
import net.videofactory.new_audi.channel_tag.ItemOfFollowListChannel;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.OnProfileImgClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-08-11.
 */

public class LikeListBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.likeListCount) TextView likeCount;
    @BindView(R.id.likeListView) ListView likeListView;

    private ArrayList<ItemOfFollowListChannel> channelList;
    private FollowChannelListAdapter channelListAdapter;

    private String mediaNum;
    private String IDX = null;

    private OnProfileImgClickListener onProfileImgClickListener;
    private AudiOnScrollListener audiOnScrollListener;

    public static LikeListBottomSheet create(String mediaNum){
        LikeListBottomSheet likeListBottomSheet = new LikeListBottomSheet();
        Bundle args = new Bundle();
        args.putString("mediaNum", mediaNum);
        likeListBottomSheet.setArguments(args);
        return likeListBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaNum = getArguments().getString("mediaNum");
        channelList = new ArrayList<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog)super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setPeekHeight(0);
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                            dismiss();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_like_list);

        ButterKnife.bind(this, bottomSheetDialog);

        selectLikeList(true);

        return bottomSheetDialog;
    }



    private void initListView(){
        audiOnScrollListener = new AudiOnScrollListener();

        channelListAdapter = new FollowChannelListAdapter(getContext(), channelList);

        likeListView.setAdapter(channelListAdapter);

        likeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onProfileImgClickListener != null){
                    onProfileImgClickListener.onProfileImgClick(channelList.get(position).getUserNum());
                }
                dismiss();
            }
        });

        likeListView.setOnScrollListener(audiOnScrollListener);
    }

    private void setLikeListViewTouchListener(final boolean requestDisallowInterceptTouchEvent){
        likeListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(requestDisallowInterceptTouchEvent);
                return false;
            }
        });
    }

    private void setAutoListUpdate(boolean autoListUpdate){
        if(audiOnScrollListener != null){
            audiOnScrollListener.setAutoUpdate(autoListUpdate);
        }
    }

    public void setOnProfileImgClickListener(OnProfileImgClickListener onProfileImgClickListener) {
        this.onProfileImgClickListener = onProfileImgClickListener;
    }

    private void selectLikeList(final boolean refresh){
        if(refresh){
            if(!channelList.isEmpty()){
                channelList.clear();
            }
            IDX = null;
        }
        Network network = new Network(getContext(), "txSearchLikeList") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        likeCount.setText(data.get("LIKE_CNT").asText());
                    }

                    IDX = data.get("IDX").asText();

                    if(!IDX.equals("D")){
                        for(JsonNode likeUserInfo : Utilities.jsonParse(data.get("DATA_LIST").asText())){

                            ItemOfFollowListChannel itemOfFollowListChannel = new ItemOfFollowListChannel();

                            itemOfFollowListChannel.setUserNum(likeUserInfo.get("USER_NO").asText());
                            itemOfFollowListChannel.setNickName(likeUserInfo.get("NICKNAME").asText());
                            itemOfFollowListChannel.setName(likeUserInfo.get("NAME").asText());
                            itemOfFollowListChannel.setProfileUrl(likeUserInfo.get("IMG_URL").asText());
                            itemOfFollowListChannel.setFollow("Y".equals(likeUserInfo.get("FOLLOW_YN").asText()));

                            channelList.add(itemOfFollowListChannel);
                        }

                        if(channelListAdapter == null){
                            initListView();
                        }else{
                            channelListAdapter.notifyDataSetChanged();
                        }
                    }

                    setAutoListUpdate(!IDX.equals("D"));
                }
            }
        };

        String url = "v001/home/media";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("RQST_NO", UserInfo.getUserNum());
        serverCommunicator.addData("MEDIA_NO", mediaNum);
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    private class AudiOnScrollListener implements AbsListView.OnScrollListener {

        private boolean firstItemVisibleFlag = true;
        private boolean lastItemVisibleFlag = false;
        private boolean requestDisallowInterceptTouchEvent = false;
        private boolean autoUpdate = false;

        public void setAutoUpdate(boolean autoUpdate) {
            this.autoUpdate = autoUpdate;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == SCROLL_STATE_IDLE && firstItemVisibleFlag){
                setLikeListViewTouchListener(false);
                requestDisallowInterceptTouchEvent = false;
            }else if(!requestDisallowInterceptTouchEvent){
                setLikeListViewTouchListener(true);
                requestDisallowInterceptTouchEvent = true;
            }
            if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag && autoUpdate){
                selectLikeList(false);
                setAutoListUpdate(false);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            firstItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem == 0);
            lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
        }
    }
}
