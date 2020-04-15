package net.videofactory.new_audi.channel_tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-30.
 */
public class FollowChannelListFragment extends RefreshFragment {

    private int type; // 0 : follower, 1 : following
    private ArrayList<ItemOfFollowListChannel> channelList;
    private String IDX = null;
    private String MAX_IDX = null;

    private FollowChannelListAdapter channelListAdapter;
    private OnFollowListItemClickListener onFollowListItemClickListener;

    @Bind(R.id.followList) ListView channelListView;
    @Bind(R.id.followListEmptyView) TextView channelListEmptyView;

    public static FollowChannelListFragment create(int type){
        FollowChannelListFragment followChannelListFragment = new FollowChannelListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        followChannelListFragment.setArguments(args);
        return followChannelListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        channelList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_list, container, false);

        ButterKnife.bind(this, view);

        channelListView.setEmptyView(channelListEmptyView);

        selectListData(true);

        return view;
    }

    private void initListView(){
        channelListAdapter = new FollowChannelListAdapter(getContext(), channelList);

        channelListView.setAdapter(channelListAdapter);

        channelListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onFollowListItemClickListener != null){
                    onFollowListItemClickListener.onFollowListChannelItemClickListener(channelList.get(position));
                }
            }
        });
    }

    private void selectListData(boolean refresh){
        setRefreshing(refresh);

        switch (type){
            case 0 :
                selectFollower(refresh);
                break;
            case 1 :
                selectFollowingChannel(refresh);
                break;
        }
    }

    private void setAutoListUpdate(boolean autoListUpdate){
        if(autoListUpdate){
            channelListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private boolean lastItemVisibleFlag = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag){
                        selectListData(false);
                        setAutoListUpdate(false);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });
        }else{
            channelListView.setOnScrollListener(null);
        }
    }

    public void setOnFollowListItemClickListener(OnFollowListItemClickListener onFollowListItemClickListener) {
        this.onFollowListItemClickListener = onFollowListItemClickListener;
    }

    private void selectFollower(final boolean refresh){
        if(refresh){
            IDX = null;
            MAX_IDX = null;
        }

        Network network = new Network(getContext(), "getChannelFollower") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        if(!channelList.isEmpty()){
                            channelList.clear();
                        }

                        MAX_IDX = data.get("MAX_IDX").asText();
                    }

                    IDX = data.get("IDX").asText();

                    ItemOfFollowListChannel itemOfFollowListChannel;
                    for(JsonNode followerData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                        itemOfFollowListChannel = new ItemOfFollowListChannel();

                        itemOfFollowListChannel.setUserNum(followerData.get("FOLLOWER").asText());
                        itemOfFollowListChannel.setNickName(followerData.get("NICKNAME").asText());
                        itemOfFollowListChannel.setName(followerData.get("NAME").asText());
                        itemOfFollowListChannel.setProfileUrl(followerData.get("IMG_URL").asText());
                        itemOfFollowListChannel.setFollow("Y".equals(followerData.get("FOLLOW_YN").asText()));

                        channelList.add(itemOfFollowListChannel);
                    }

                    if(channelListAdapter == null){
                        initListView();
                    }else{
                        channelListAdapter.notifyDataSetChanged();
                    }

                    setAutoListUpdate(!IDX.equals(MAX_IDX));

                    if(refresh){
                        setRefreshing(false);
                    }
                }
            }
        };

        String url = "v001/follow";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    private void selectFollowingChannel(final boolean refresh){
        if(refresh){
            IDX = null;
            MAX_IDX = null;
        }

        Network network = new Network(getContext(), "getChannelFollowing") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        if(!channelList.isEmpty()){
                            channelList.clear();
                        }

                        MAX_IDX = data.get("MAX_IDX").asText();
                    }

                    IDX = data.get("IDX").asText();

                    ItemOfFollowListChannel itemOfFollowListChannel;
                    for(JsonNode channelData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                        itemOfFollowListChannel = new ItemOfFollowListChannel();

                        itemOfFollowListChannel.setUserNum(channelData.get("FOLLOWING").asText());
                        itemOfFollowListChannel.setNickName(channelData.get("NICKNAME").asText());
                        itemOfFollowListChannel.setName(channelData.get("NAME").asText());
                        itemOfFollowListChannel.setProfileUrl(channelData.get("IMG_URL").asText());
                        itemOfFollowListChannel.setFollow("Y".equals(channelData.get("FOLLOW_YN").asText()));

                        channelList.add(itemOfFollowListChannel);
                    }

                    if(channelListAdapter == null){
                        initListView();
                    }else{
                        channelListAdapter.notifyDataSetChanged();
                    }

                    setAutoListUpdate(!IDX.equals(MAX_IDX));

                    if(refresh){
                        setRefreshing(false);
                    }
                }
            }
        };

        String url = "v001/follow";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!channelListView.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectListData(true);
            }
        }else{
            channelListView.smoothScrollToPosition(0);
        }
    }
}
