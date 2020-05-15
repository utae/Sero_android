package net.videofactory.new_audi.home;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.common.ItemOfCard;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-04-01.
 */
public class HomePageFragment extends RefreshFragment{

    private ArrayList<ItemOfCard> cardList = new ArrayList<>();

    private HomeCardListAdapter homeCardListAdapter;

    private OnHeaderItemClickListener onHeaderItemClickListener;

    private HeaderHolder headerHolder;

    private HomeCardAddDialog homeCardAddDialog;

    private HomeCardAddSearchDialog homeCardAddSearchDialog;

    private OnCardClickListener onCardClickListener;

    @BindView(R.id.homeCardAddButton) ImageButton homeCardAddButton;

    @BindView(R.id.homeCardListView) ListView homeCardListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        ButterKnife.bind(this, view);

        selectMyCards();

        View header = inflater.inflate(R.layout.header_home_card_list, null, false);

        headerHolder = new HeaderHolder(header);

        initHeader();

        initHomeCardAdding();

        homeCardListView.addHeaderView(header);

        homeCardListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        homeCardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onCardClickListener != null){
                    onCardClickListener.onCardClick(cardList.get(position-1));
                }
            }
        });

        //TODO header touch event intercept 못하도록 처리

        return view;
    }

    private void initListView(){
        homeCardListAdapter = new HomeCardListAdapter(getContext(), cardList);
        homeCardListView.setAdapter(homeCardListAdapter);
    }

    private void initHeader(){
        headerHolder.following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHeaderItemClickListener.onHeaderItemClick("020-001");
            }
        });

        headerHolder.trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHeaderItemClickListener.onHeaderItemClick("020-003");
            }
        });

        headerHolder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHeaderItemClickListener.onHeaderItemClick("020-002");
            }
        });
    }

    private void initHomeCardAdding(){
        homeCardAddDialog = new HomeCardAddDialog();

        homeCardAddDialog.setOnDialogCloseButtonCLickListener(new HomeCardAddDialog.OnDialogButtonCLickListener() {
            @Override
            public void onTypeButtonClick(int type) {
                homeCardAddSearchDialog = HomeCardAddSearchDialog.create(type);
                homeCardAddSearchDialog.setOnDialogDismissedListener(new HomeCardAddSearchDialog.OnCardSelectListener() {
                    @Override
                    public void onSelected() {
                        homeCardAddSearchDialog.dismiss();
                        homeCardAddDialog.dismiss();
                        selectMyCards();
                    }
                });
                homeCardAddSearchDialog.show(getFragmentManager(), "homeCardAddSearchDialog");
            }
        });

        homeCardAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeCardAddDialog.show(getFragmentManager(), "homeCardAddDialog");
            }
        });
    }

    public void selectMyCards(){
        setRefreshing(true);

        Network network = new Network(getContext(), "getMyCardList") {
            @Override
            protected void processFinish(JsonNode result) {
                if(!cardList.isEmpty()){
                    cardList.clear();
                }
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null) {
                    ArrayList<String> imgList;
                    ItemOfCard itemOfCard;

                    for (JsonNode cardData : Utilities.jsonParse(data.get("DATA_LIST").asText())) {
                        String type = null;
                        String title = null;
                        String profileImg = null;
                        String channelNum = null;

                        imgList = new ArrayList<>();

                        for (int i = 1; i <= 4; i++) {
                            imgList.add(cardData.get("MEDIA_IMG_URL_" + i).asText());
                        }

                        switch (cardData.get("CARD_TP").asText()) {
                            case "020-001":
                                continue;

                            case "020-002":
                                continue;

                            case "020-003":
                                continue;

                            case "020-004":
                                type = "020-004";
                                title = cardData.get("NICKNAME").asText();
                                channelNum = cardData.get("REF_NO").asText();
                                if (cardData.get("IMG_URL") != null && !"".equals(cardData.get("IMG_URL").asText())) {
                                    profileImg = cardData.get("IMG_URL").asText();
                                }
                                break;

                            case "020-005":
                                type = "020-005";
                                title = cardData.get("REF_NO").asText();
                                break;
                        }

                        itemOfCard = new ItemOfCard(type, title, imgList);

                        if(channelNum != null){
                            itemOfCard.setUserNum(channelNum);
                        }

                        if (profileImg != null) {
                            itemOfCard.setProfileUrl(profileImg);
                        }

                        cardList.add(itemOfCard);
                    }

                    if (homeCardListAdapter == null) {
                        initListView();
                    } else {
                        homeCardListAdapter.notifyDataSetChanged();
                    }

                    setRefreshing(false);
                }
            }
        };

        String url = "v001/home/card";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());

        serverCommunicator.communicate();
    }

    public void setOnHeaderItemClickListener(OnHeaderItemClickListener onHeaderItemClickListener) {
        this.onHeaderItemClickListener = onHeaderItemClickListener;
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    @Override
    public void refreshFragment() {
        if(!homeCardListView.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectMyCards();
            }
        }else{
            homeCardListView.smoothScrollToPosition(0);
        }
    }

    public interface OnHeaderItemClickListener{
        void onHeaderItemClick(String type);
    }

    static class HeaderHolder{

        @BindView(R.id.homeCardTrending) LinearLayout trending;

        @BindView(R.id.homeCardFavorite) LinearLayout favorite;

        @BindView(R.id.homeCardFollowing) LinearLayout following;

        public HeaderHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
