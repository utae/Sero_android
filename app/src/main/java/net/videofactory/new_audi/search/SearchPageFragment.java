package net.videofactory.new_audi.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.flyco.pageindicator.indicator.FlycoPageIndicaor;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.horizontal_list_view.HorizontalListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-12-01.
 */
public class SearchPageFragment extends RefreshFragment {

    private ArrayList<ItemOfBanner> bannerList = new ArrayList<>();

    private ArrayList<ItemOfSearchTag> tagList = new ArrayList<>();

    private ArrayList<ItemOfSearchChannel> channelList = new ArrayList<>();

    private SearchBannerPagerAdapter bannerPagerAdapter;

    private SearchChannelListAdapter channelListAdapter;

    private SearchTagListAdapter tagListAdapter;

    private OnSearchPageItemClickListener onSearchPageItemClickListener;

    private TextView.OnEditorActionListener onSearchActionListener;

    @Bind(R.id.searchTextInput) EditText searchTextInput;

    @Bind(R.id.searchBannerPager) ViewPager bannerPager;

    @Bind(R.id.searchChannelListView) HorizontalListView channelListView;

    @Bind(R.id.searchTagListView) HorizontalListView tagListView;

    @Bind(R.id.searchBannerPagerIndicator) FlycoPageIndicaor bannerPagerIndicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_page, container, false);

        ButterKnife.bind(this, view);

        selectSearchMain();

        if(onSearchActionListener != null){
            searchTextInput.setOnEditorActionListener(onSearchActionListener);
        }

        return view;
    }

    private void initBannerPager(){
        bannerPagerAdapter = new SearchBannerPagerAdapter(getFragmentManager(), bannerList);
        bannerPager.setAdapter(bannerPagerAdapter);
        bannerPagerIndicator.setViewPager(bannerPager, bannerList.size());
    }

    private void initChannelList(){
        channelListAdapter = new SearchChannelListAdapter(getContext(), channelList);
        channelListView.setAdapter(channelListAdapter);
        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onSearchPageItemClickListener != null){
                    onSearchPageItemClickListener.onChannelItemClick(channelList.get(position).getChannelId());
                }
            }
        });
    }

    private void initTagList(){
        tagListAdapter = new SearchTagListAdapter(getContext(), tagList);
        tagListView.setAdapter(tagListAdapter);
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onSearchPageItemClickListener != null){
                    onSearchPageItemClickListener.onTagItemClick(tagList.get(position).getTagName());
                }
            }
        });
    }

    public void setOnSearchActionListener(TextView.OnEditorActionListener onSearchActionListener){
        this.onSearchActionListener = onSearchActionListener;
    }

    public void setOnSearchPageItemClickListener(OnSearchPageItemClickListener onSearchPageItemClickListener) {
        this.onSearchPageItemClickListener = onSearchPageItemClickListener;
    }

    public void selectSearchMain(){
        setRefreshing(true);

        Network network = new Network(getContext(), "getBanner") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null && "Y".equals(result.get("RTN_VAL").asText())){
                    JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                    if(data != null && result.get("TIMESTAMP").asText().equals(data.get("TIMESTAMP").asText())){

                        if(data.get("TOP_BANNER") != null && !"".equals(data.get("TOP_BANNER").asText())){
                            if(!bannerList.isEmpty()){
                                bannerList.clear();
                            }

                            for(JsonNode banner : Utilities.jsonParse(data.get("TOP_BANNER").asText())){

                                if(banner.get("IMG_URL") != null && !"".equals(banner.get("IMG_URL").asText())) {
                                    bannerList.add(new ItemOfBanner(banner.get("IMG_URL").asText(), banner.get("LINK").asText()));
                                }
                            }
                        }

                        if(bannerPagerAdapter == null){
                            initBannerPager();
                        }else{
                            bannerPagerAdapter.notifyDataSetChanged();
                        }

                        if(data.get("CHANNEL") != null && !"".equals(data.get("CHANNEL").asText())){
                            if(!channelList.isEmpty()){
                                channelList.clear();
                            }
                            for(JsonNode channel : Utilities.jsonParse(data.get("CHANNEL").asText())){
                                channelList.add(new ItemOfSearchChannel(channel.get("REF_NO").asText(), channel.get("IMG_URL").asText()));
                            }
                        }

                        if(channelListAdapter == null){
                            initChannelList();
                        }else{
                            channelListAdapter.notifyDataSetChanged();
                        }

                        if(data.get("HASHTAG") != null && !"".equals(data.get("HASHTAG").asText())){
                            if(!tagList.isEmpty()){
                                tagList.clear();
                            }
                            for(JsonNode tag : Utilities.jsonParse(data.get("HASHTAG").asText())){
                                tagList.add(new ItemOfSearchTag(tag.get("REF_NO").asText(), tag.get("IMG_URL").asText()));
                            }
                        }

                        if(tagListAdapter == null){
                            initTagList();
                        }else{
                            tagListAdapter.notifyDataSetChanged();
                        }

                        setRefreshing(false);
                    }
                }

            }
        };

        String url = "v001/home/search";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!isRefreshing()){
            selectSearchMain();
        }
    }

    public interface OnSearchPageItemClickListener{
        void onChannelItemClick(String userNum);

        void onTagItemClick(String tag);
    }
}
