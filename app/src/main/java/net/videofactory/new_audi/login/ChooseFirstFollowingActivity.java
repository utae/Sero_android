package net.videofactory.new_audi.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.mikhaellopez.circularimageview.CircularImageView;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.BackPressCloseSystem;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.main.MainActivity;
import net.videofactory.new_audi.setting.TutorialActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-10-27.
 */

public class ChooseFirstFollowingActivity extends AppCompatActivity{

    @BindView(R.id.firstChooseTag1) Button tagButton1;
    @BindView(R.id.firstChooseTag2) Button tagButton2;
    @BindView(R.id.firstChooseTag3) Button tagButton3;
    @BindView(R.id.firstChooseTag4) Button tagButton4;
    private ArrayList<Button> tagButtonList = new ArrayList<>();

    @BindView(R.id.firstChooseChannelProfile1) CircleLoadingImageView channelProfile1;
    @BindView(R.id.firstChooseChannelProfile2) CircleLoadingImageView channelProfile2;
    @BindView(R.id.firstChooseChannelProfile3) CircleLoadingImageView channelProfile3;
    @BindView(R.id.firstChooseChannelProfile4) CircleLoadingImageView channelProfile4;
    private ArrayList<CircleLoadingImageView> channelProfileList = new ArrayList<>();

    @BindView(R.id.firstChooseChannelCheck1) CircularImageView channelCheck1;
    @BindView(R.id.firstChooseChannelCheck2) CircularImageView channelCheck2;
    @BindView(R.id.firstChooseChannelCheck3) CircularImageView channelCheck3;
    @BindView(R.id.firstChooseChannelCheck4) CircularImageView channelCheck4;
    private ArrayList<CircularImageView> channelCheckList = new ArrayList<>();

    @BindView(R.id.firstChooseChannelName1) TextView channelName1;
    @BindView(R.id.firstChooseChannelName2) TextView channelName2;
    @BindView(R.id.firstChooseChannelName3) TextView channelName3;
    @BindView(R.id.firstChooseChannelName4) TextView channelName4;
    private ArrayList<TextView> channelNameList = new ArrayList<>();

    @BindView(R.id.firstChooseNextButton) Button nextButton;

    private ArrayList<String> tagNameList = new ArrayList<>();
    private ArrayList<String> channelNumList = new ArrayList<>();

    private BackPressCloseSystem backPressCloseSystem;

    private final int REQUEST_CODE_TUTORIAL = 100;

    int selectCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_first_following);

        ButterKnife.bind(this);

        backPressCloseSystem = new BackPressCloseSystem(this);

        initArrayList();

        selectFistFollowing();
    }

    private void initArrayList(){
        tagButtonList.add(tagButton1);
        tagButtonList.add(tagButton2);
        tagButtonList.add(tagButton3);
        tagButtonList.add(tagButton4);

        channelProfileList.add(channelProfile1);
        channelProfileList.add(channelProfile2);
        channelProfileList.add(channelProfile3);
        channelProfileList.add(channelProfile4);

        channelCheckList.add(channelCheck1);
        channelCheckList.add(channelCheck2);
        channelCheckList.add(channelCheck3);
        channelCheckList.add(channelCheck4);

        channelNameList.add(channelName1);
        channelNameList.add(channelName2);
        channelNameList.add(channelName3);
        channelNameList.add(channelName4);

    }

    private void initListener(){
        for(int i = 0; i < 4; i++){
            tagButtonList.get(i).setOnClickListener(new AudiCFFButtonOnCLickListener(tagNameList.get(i)));
            channelProfileList.get(i).setOnClickListener(new AudiCFFChannelOnClickListener(i, true));
            channelCheckList.get(i).setOnClickListener(new AudiCFFChannelOnClickListener(i, false));
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseFirstFollowingActivity.this, TutorialActivity.class);
                intent.putExtra("isSignup", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }

    private void selectTagButton(Button button, boolean select){
        if(select){
            button.setSelected(true);
            button.setTextColor(Color.parseColor("#ffffff"));
        }else{
            button.setSelected(false);
            button.setTextColor(Color.parseColor("#000000"));
        }
    }

    private void selectChannel(int index, boolean select){
        if(select){
            switch (index){
                case 0:
                    channelCheck1.setVisibility(View.VISIBLE);
                    break;

                case 1:
                    channelCheck2.setVisibility(View.VISIBLE);
                    break;

                case 2:
                    channelCheck3.setVisibility(View.VISIBLE);
                    break;

                case 3:
                    channelCheck4.setVisibility(View.VISIBLE);
                    break;
            }
        }else{
            switch (index){
                case 0:
                    channelCheck1.setVisibility(View.INVISIBLE);
                    break;

                case 1:
                    channelCheck2.setVisibility(View.INVISIBLE);
                    break;

                case 2:
                    channelCheck3.setVisibility(View.INVISIBLE);
                    break;

                case 3:
                    channelCheck4.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_TUTORIAL :
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
        }
    }

    private void selectFistFollowing(){
        Network network = new Network(this, "recommendTag") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){
                    JsonNode tagDataList = Utilities.jsonParse(data.get("HASHTAGS").asText());

                    for(int i = 0; i < 4; i++){
                        JsonNode tagData = tagDataList.get(i);
                        tagButtonList.get(i).setText(tagData.get("REF_NO").asText());
                        tagNameList.add(tagData.get("REF_NO").asText());
                    }

                    JsonNode channelDataList = Utilities.jsonParse(data.get("CHANNELS").asText());

                    for(int i = 0; i < 4; i++){
                        JsonNode channelData = channelDataList.get(i);

                        if(channelData.get("IMG_URL") == null || "".equals(channelData.get("IMG_URL").asText())){
                            channelProfileList.get(i).setImageResource(R.drawable.ic_profile_default);
                        }else{
                            if(Utilities.cancelPotentialTask(channelData.get("IMG_URL").asText(), channelProfileList.get(i))){
                                ImagePickerTask profileImgPickerTask = new ImagePickerTask(channelProfileList.get(i));
                                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, profileImgPickerTask);
                                channelProfileList.get(i).setImageLoadingDrawable(loadingDrawable);
                                profileImgPickerTask.execute(channelData.get("IMG_URL").asText());
                            }
                        }

                        channelNameList.get(i).setText(channelData.get("NICKNAME").asText());

                        channelNumList.add(channelData.get("REF_NO").asText());
                    }

                    initListener();
                }
            }
        };

        String url = "base";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.communicate();
    }

    private void followTag(String hashTag, final Button button, final boolean follow){
        Network network = new Network(this, "txFollowHt") {
            @Override
            protected void processFinish(JsonNode result) {
                if(follow){
                    selectCount++;
                }else{
                    selectCount--;
                }
                selectTagButton(button, follow);
            }
        };

        String url = "v001/follow";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("HASHTAG", hashTag);

        serverCommunicator.communicate();
    }

    private void followChannel(final int index, final boolean follow){
        Network network = new Network(this, "txFollowCh") {
            @Override
            protected void processFinish(JsonNode result) {
                if(follow){
                    selectCount++;
                }else{
                    selectCount--;
                }
                selectChannel(index, follow);
            }
        };

        String url = "v001/follow";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("FOLLOWING", channelNumList.get(index));

        serverCommunicator.communicate();
    }

    private class AudiCFFButtonOnCLickListener implements View.OnClickListener{

        private String tagName;
        private boolean isSelected = false;

        public AudiCFFButtonOnCLickListener(String tagName) {
            this.tagName = tagName;
        }

        @Override
        public void onClick(View v) {
            if(v instanceof Button){
                Button button = (Button) v;
                isSelected = !isSelected;
                followTag(tagName, button, isSelected);
            }
        }
    }

    private class AudiCFFChannelOnClickListener implements View.OnClickListener{

        private int index;
        private boolean isProfileImg;

        public AudiCFFChannelOnClickListener(int index, boolean isProfileImg) {
            this.index = index;
            this.isProfileImg = isProfileImg;
        }

        @Override
        public void onClick(View v) {
            followChannel(index, isProfileImg);
        }
    }
}
