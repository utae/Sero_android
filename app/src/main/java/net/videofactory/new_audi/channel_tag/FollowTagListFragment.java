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
public class FollowTagListFragment extends RefreshFragment {

    private ArrayList<ItemOfFollowListTag> tagList;
    private String IDX = null;
    private String MAX_IDX = null;

    private FollowTagListAdapter tagListAdapter;
    private OnFollowListItemClickListener onFollowListItemClickListener;

    @Bind(R.id.followList) ListView tagListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tagList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_list, container, false);

        ButterKnife.bind(this, view);

        selectFollowingTag(true);

        return view;
    }

    private void initListView(){
        tagListAdapter = new FollowTagListAdapter(getContext(), tagList);

        tagListView.setAdapter(tagListAdapter);

        tagListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onFollowListItemClickListener != null){
                    onFollowListItemClickListener.onFollowListTagItemClickListener(tagList.get(position));
                }
                Utilities.logD("Test", "tag item click");
            }
        });
    }

    private void setAutoListUpdate(boolean autoListUpdate){
        if(autoListUpdate){
            tagListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private boolean lastItemVisibleFlag = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag){
                        selectFollowingTag(false);
                        setAutoListUpdate(false);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });
        }else{
            tagListView.setOnScrollListener(null);
        }
    }

    public void setOnFollowListItemClickListener(OnFollowListItemClickListener onFollowListItemClickListener) {
        this.onFollowListItemClickListener = onFollowListItemClickListener;
    }

    private void selectFollowingTag(final boolean refresh){
        if(refresh){
            setRefreshing(true);
            IDX = null;
            MAX_IDX = null;
        }

        Network network = new Network(getContext(), "getHashtagFollowing") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        if(!tagList.isEmpty()){
                            tagList.clear();
                        }

                        MAX_IDX = data.get("MAX_IDX").asText();
                    }

                    IDX = data.get("IDX").asText();

                    ItemOfFollowListTag itemOfFollowListTag;
                    for(JsonNode tagData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                        itemOfFollowListTag = new ItemOfFollowListTag();

                        itemOfFollowListTag.setTagName(tagData.get("HASHTAG").asText());
                        itemOfFollowListTag.setFollow("Y".equals(tagData.get("FOLLOW_YN").asText()));

                        tagList.add(itemOfFollowListTag);
                    }

                    if(tagListAdapter == null){
                        initListView();
                    }else{
                        tagListAdapter.notifyDataSetChanged();
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
        if(!tagListView.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectFollowingTag(true);
            }
        }else{
            tagListView.smoothScrollToPosition(0);
        }
    }
}
