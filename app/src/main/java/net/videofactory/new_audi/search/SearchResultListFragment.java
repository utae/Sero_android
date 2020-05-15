package net.videofactory.new_audi.search;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.common.ItemOfCard;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.home.OnCardClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-12.
 */
public class SearchResultListFragment extends RefreshFragment {

    @BindView(R.id.searchResultList) ListView searchResultListView;

    @BindView(R.id.searchResultListEmptyView) ImageView searchResultListEmptyView;

    private int type; // 0 : channel, 1 : tag

    private String word;

    private ArrayList<ItemOfCard> cardList;

    private String IDX = null;

    private SearchResultListAdapter searchResultListAdapter;

    private OnCardClickListener onCardClickListener;

    public static SearchResultListFragment create(String word, int type){
        SearchResultListFragment searchResultListFragment = new SearchResultListFragment();
        Bundle args = new Bundle();
        args.putString("word", word);
        args.putInt("type", type);
        searchResultListFragment.setArguments(args);
        return searchResultListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        word = getArguments().getString("word");
        type = getArguments().getInt("type");
        cardList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result_list, container, false);

        ButterKnife.bind(this, view);

        searchResultListView.setEmptyView(searchResultListEmptyView);

        selectListData(true);

        return view;
    }

    private void selectListData(boolean refresh){
        setRefreshing(true);
        switch (type){
            case 0 :
                selectSearchResultChannel(refresh);
                break;

            case 1 :
                selectSearchResultTag(refresh);
                break;
        }
    }

    private void initListView(){
        searchResultListAdapter = new SearchResultListAdapter(getContext(), cardList);

        searchResultListView.setAdapter(searchResultListAdapter);

        searchResultListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onCardClickListener != null){
                    onCardClickListener.onCardClick(cardList.get(position));
                }

            }
        });
    }

    private void setAutoListUpdate(boolean autoListUpdate){
        if(autoListUpdate){
            searchResultListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
            searchResultListView.setOnScrollListener(null);
        }
    }

    public void setWord(String word) {
        this.word = word;
        selectListData(true);
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    private void selectSearchResultChannel(final boolean refresh){
        if(refresh){
            IDX = null;
        }

        Network network = new Network(getContext(), "txSearchChannel") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        if(!cardList.isEmpty()){
                            cardList.clear();
                        }
                    }

                    IDX = data.get("IDX").asText();

                    if(!IDX.equals("D")){
                        ArrayList<String> imgUrlList;
                        for(JsonNode channelData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                            imgUrlList = new ArrayList<>();
                            imgUrlList.add(channelData.get("MEDIA_IMG_URL_1").asText());
                            imgUrlList.add(channelData.get("MEDIA_IMG_URL_2").asText());
                            imgUrlList.add(channelData.get("MEDIA_IMG_URL_3").asText());
                            imgUrlList.add(channelData.get("MEDIA_IMG_URL_4").asText());

                            ItemOfCard itemOfCard = new ItemOfCard("020-004", channelData.get("NICKNAME").asText(), imgUrlList);

                            itemOfCard.setUserNum(channelData.get("USER_NO").asText());

                            itemOfCard.setProfileUrl(channelData.get("IMG_URL").asText());

                            cardList.add(itemOfCard);
                        }

                        if(searchResultListAdapter == null){
                            initListView();
                        }else{
                            searchResultListAdapter.notifyDataSetChanged();
                        }
                    }

                    setAutoListUpdate(!IDX.equals("D"));

                    setRefreshing(false);
                }
            }
        };

        String url = "v001/home/search";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("CHANNEL", word);
        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    private void selectSearchResultTag(final boolean refresh){
        if(refresh){
            IDX = null;
        }

        Network network = new Network(getContext(), "txSearchHashTag") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        if(!cardList.isEmpty()){
                            cardList.clear();
                        }
                    }

                    IDX = data.get("IDX").asText();

                    if(!IDX.equals("D")){
                        ArrayList<String> imgUrlList;
                        for(JsonNode tagData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                            imgUrlList = new ArrayList<>();
                            imgUrlList.add(tagData.get("MEDIA_IMG_URL_1").asText());
                            imgUrlList.add(tagData.get("MEDIA_IMG_URL_2").asText());
                            imgUrlList.add(tagData.get("MEDIA_IMG_URL_3").asText());
                            imgUrlList.add(tagData.get("MEDIA_IMG_URL_4").asText());

                            cardList.add(new ItemOfCard("020-005", tagData.get("HASHTAG").asText(), imgUrlList));
                        }

                        if(searchResultListAdapter == null){
                            initListView();
                        }else{
                            searchResultListAdapter.notifyDataSetChanged();
                        }
                    }

                    setAutoListUpdate(!IDX.equals("D"));

                    setRefreshing(false);
                }
            }
        };

        String url = "v001/home/search";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("HASHTAG", word);
        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!searchResultListView.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectListData(true);
            }
        }else{
            searchResultListView.smoothScrollToPosition(0);
        }
    }
}
