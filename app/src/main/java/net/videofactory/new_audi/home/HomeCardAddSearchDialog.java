package net.videofactory.new_audi.home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-15.
 */
public class HomeCardAddSearchDialog extends DialogFragment {

    private int type; // 0 : channel, 1 : tag

    private ArrayList<ItemOfFollowed> followedList;

    private HomeCardAddSearchDialogListAdapter followedListAdapter;

    private HomeCardAddSearchDialogAutoCompleteAdapter autoCompleteAdapter;

    private OnCardSelectListener onCardSelectListener;

    @BindView(R.id.homeCardAddDialogSearchTitle) TextView title;
    @BindView(R.id.homeCardAddDialogSearchComment) TextView comment;
    @BindView(R.id.homeCardAddDialogSearchEditText) MultiAutoCompleteTextView editText;
    @BindView(R.id.homeCardAddDialogSearchClose) ImageButton closeButton;
    @BindView(R.id.homeCardAddDialogSearchList) ListView followedListView;

    public static HomeCardAddSearchDialog create(int type){
        HomeCardAddSearchDialog homeCardAddSearchDialog = new HomeCardAddSearchDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        homeCardAddSearchDialog.setArguments(args);
        return homeCardAddSearchDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_home_card_add_search, container, false);

        ButterKnife.bind(this, view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL);

        followedList = new ArrayList<>();
        
        switch (type){
            case 0 :
                title.setText("Add Channel");
                comment.setText("You can choose from channels you follow");
                selectFollowedChannel();
                break;

            case 1 :
                title.setText("Add Hashtag");
                comment.setText("You can choose from hashtags you follow");
                selectFollowedTag();
                break;
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void initListView(){
        followedListAdapter = new HomeCardAddSearchDialogListAdapter(getContext(), type, followedList);
        followedListView.setAdapter(followedListAdapter);

        followedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (type){
                    case 0 :
                        addCard("020-004", followedList.get(position).getUserNum());
                        break;

                    case 1 :
                        addCard("020-005", followedList.get(position).getName());
                        break;
                }
            }
        });
    }

    private void initAutoComplete(){
        autoCompleteAdapter = new HomeCardAddSearchDialogAutoCompleteAdapter(getContext(), type, followedList);
        editText.setAdapter(autoCompleteAdapter);
        editText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), followedList.get(position).getName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setOnDialogDismissedListener(OnCardSelectListener onCardSelectListener) {
        this.onCardSelectListener = onCardSelectListener;
    }

    private void selectFollowedChannel(){
        Network network = new Network(getContext(), "getMyChannel") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                    if(data != null){
                        for(JsonNode channelData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                            ItemOfFollowed itemOfFollowed = new ItemOfFollowed(channelData.get("NICKNAME").asText());
                            itemOfFollowed.setProfileUrl(channelData.get("IMG_URL").asText());
                            itemOfFollowed.setUserNum(channelData.get("USER_NO").asText());

                            followedList.add(itemOfFollowed);
                        }

                        initListView();
                        initAutoComplete();
                    }
                }
            }
        };

        String url = "v001/home/card";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());

        serverCommunicator.communicate();
    }

    private void selectFollowedTag(){
        Network network = new Network(getContext(), "getMyTag") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                    if(data != null){
                        for(JsonNode tagData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                            followedList.add(new ItemOfFollowed(tagData.get("HASHTAG").asText()));
                        }

                        initListView();
                        initAutoComplete();
                    }
                }
            }
        };

        String url = "v001/home/card";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());

        serverCommunicator.communicate();
    }

    private void addCard(String type, String id){
        Network network = new Network(getContext(), "txAddCard") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        if(onCardSelectListener != null){
                            onCardSelectListener.onSelected();
                        }else{
                            dismiss();
                        }
                    }
                }
            }
        };

        String url = "v001/home/card";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("CARD_TP", type);
        serverCommunicator.addData("REF_NO", id);

        serverCommunicator.communicate();
    }

    public interface OnCardSelectListener{
        void onSelected();
    }
}
