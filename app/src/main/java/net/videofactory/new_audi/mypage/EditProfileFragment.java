package net.videofactory.new_audi.mypage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.AmazonS3Uploader;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-30.
 */
public class EditProfileFragment extends Fragment {

    @Bind(R.id.editProfileBackButton) ImageButton backButton;
    @Bind(R.id.editProfileImg) CircleLoadingImageView profileImg;
    @Bind(R.id.editProfileNickName) TextView nickName;
    @Bind(R.id.editProfileIntro) EditText intro;
    @Bind(R.id.editProfileName) EditText name;
    @Bind(R.id.editProfileEmail) EditText email;
    @Bind(R.id.editProfileGenderRadioGroup) RadioGroup genderRadioGroup;
    @Bind(R.id.editProfileBirthSelectButton) Button birthSelectButton;
    @Bind(R.id.editProfileSaveButton) Button saveButton;

    private DatePickerPopWin datePickerPopWin;
    private EditProfileListener editProfileListener;
    private String newProfileImgPath = null;
    private AmazonS3Uploader amazonS3Uploader;
    private String preProfileimgUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ButterKnife.bind(this, view);

        selectMyInfo();

        return view;
    }

    private void initListener(){
        datePickerPopWin = new DatePickerPopWin(new DatePickerPopWin.Builder(getContext(), new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                birthSelectButton.setText(dateDesc);
            }
        }));

        birthSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerPopWin.showPopWin(getActivity());
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editProfileListener != null){
                    editProfileListener.onEditProfileImgClick();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validationCheck()){
                    if(newProfileImgPath != null){
                        amazonS3Uploader = new AmazonS3Uploader(getContext(), AmazonS3Uploader.MODE_PROFILE_IMG_UPLOAD, newProfileImgPath) {
                            @Override
                            protected void onUploadFinish(String s3Url) {
                                editProfile(s3Url);
                            }
                        };
                        amazonS3Uploader.uploadS3();
                    }else{
                        editProfile(preProfileimgUrl);
                    }
                }
            }
        });
    }

    public void setProfileImg(String imgPath){
        this.newProfileImgPath = imgPath;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        profileImg.setImageBitmap(bitmap);
    }

    private boolean validationCheck(){
        if(name.getText() == null || "".equals(name.getText().toString().trim())){
            Toast.makeText(getContext(), "Enter your name", Toast.LENGTH_LONG).show();
            return false;
        }else if(email.getText() == null || !Utilities.isValidEmail(email.getText())){
            Toast.makeText(getContext(), "Check your email", Toast.LENGTH_LONG).show();
            return false;
        }else if(genderRadioGroup.getCheckedRadioButtonId() == genderRadioGroup.getId() - 1){
            Toast.makeText(getContext(), "Choose your gender", Toast.LENGTH_LONG).show();
            return false;
        }else if("".equals(birthSelectButton.getText().toString().trim())){
            Toast.makeText(getContext(), "Choose your birthday", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private void selectMyInfo(){
        Network network = new Network(getContext(), "getInfo") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null && "Y".equals(result.get("RTN_VAL").asText())){
                    JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                    if(data != null){
                        if(data.get("IMG_URL") == null || "".equals(data.get("IMG_URL").asText())){
                            profileImg.setImageResource(R.drawable.ic_profile_default);
                        }else{
                            preProfileimgUrl = data.get("IMG_URL").asText();
                            if(Utilities.cancelPotentialTask(data.get("IMG_URL").asText(), profileImg)){
                                ImagePickerTask profileImgPickerTask = new ImagePickerTask(profileImg);
                                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, profileImgPickerTask);
                                profileImg.setImageLoadingDrawable(loadingDrawable);
                                profileImgPickerTask.execute(data.get("IMG_URL").asText());
                            }
                        }

                        nickName.setText(data.get("NICKNAME").asText());

                        intro.setText(data.get("MY_STORY").asText());

                        name.setText(data.get("NAME").asText());

                        email.setText(data.get("EMAIL").asText());

                        switch (data.get("GENDER").asText()){
                            case "101-001" :
                                genderRadioGroup.check(R.id.editProfileMailRadioButton);
                                break;

                            case "101-002" :
                                genderRadioGroup.check(R.id.editProfileFemailRadioButton);
                                break;
                        }

                        birthSelectButton.setText(data.get("BIRTH").asText());
                        birthSelectButton.setSelected(true);

                        initListener();
                    }
                }
            }
        };

        String url = "v001/home/view";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.communicate();
    }

    private void editProfile(String imgUrl){
        Network network = new Network(getContext(), "txEditProfile") {
            @Override
            protected void processFinish(JsonNode result) {
                if(editProfileListener != null){
                    editProfileListener.onEditProfileFinish();
                }
            }
        };

        String url = "v001/home/view";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("MY_STORY", intro.getText().toString().trim());
        serverCommunicator.addData("IMG_URL", imgUrl);
        serverCommunicator.addData("NAME", name.getText().toString().trim());
        serverCommunicator.addData("EMAIL", email.getText().toString().trim());
        switch(genderRadioGroup.getCheckedRadioButtonId()){
            case R.id.editProfileMailRadioButton :
                serverCommunicator.addData("GENDER", "101-001");
                break;

            case R.id.editProfileFemailRadioButton :
                serverCommunicator.addData("GENDER", "101-002");
                break;
        }
        serverCommunicator.addData("BIRTH", birthSelectButton.getText().toString().trim());

        serverCommunicator.communicate();
    }

    public void setEditProfileListener(EditProfileListener editProfileListener) {
        this.editProfileListener = editProfileListener;
    }

    public interface EditProfileListener{
        void onEditProfileImgClick();
        void onEditProfileFinish();
    }
}
