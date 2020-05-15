package net.videofactory.new_audi.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-12-23.
 */
public class SignupFormActivity extends AppCompatActivity {

    @BindView(R.id.signupInputEmail) EditText signupInputEmail;
    @BindView(R.id.signupInputPW) EditText signupInputPW;
    @BindView(R.id.signupInputPWConfirm) EditText signupInputPWConfirm;
    @BindView(R.id.signupInputName) EditText signupInputName;
    @BindView(R.id.signupInputNickName) EditText signupInputNickName;
    @BindView(R.id.signupGenderRadioGroup) RadioGroup genderRadioGroup;
    @BindView(R.id.signupBirthSelectButton) Button birthSelectButton;
    @BindView(R.id.signupButton) Button signupButton;
    @BindView(R.id.signupInputNameContainer) LinearLayout inputNameContainer;
    @BindView(R.id.signupInputNickNameContainer) LinearLayout inputNickNameContainer;
    @BindView(R.id.signupInputEmailContainer) LinearLayout inputEmailContainer;
    @BindView(R.id.signupInputPWContainer) LinearLayout inputPWContainer;
    @BindView(R.id.signupInputPWConfirmContainer) LinearLayout inputPWConfirmContainer;
    @BindView(R.id.signupNameStatusButton) ImageButton nameStatusButton;
    @BindView(R.id.signupNickNameStatusButton) ImageButton nickNameStatusButton;
    @BindView(R.id.signupEmailStatusButton) ImageButton emailStatusButton;
    @BindView(R.id.signupPWStatusButton) ImageButton pwStatusButton;
    @BindView(R.id.signupPWConfirmStatusButton) ImageButton pwConfirmStatusButton;
    @BindView(R.id.signupFormContainer) LinearLayout formContainer;
    @BindView(R.id.signupPolicy) TextView policy;
    @BindView(R.id.signupBackButton) ImageButton backButton;

    private DatePickerPopWin datePickerPopWin;
    private String email = null;
    private String name = null;
    private String nickName = null;
    private String gender = null;
    private String birth = null;
    private String encryptionPW = null;

    private final int REQUEST_CODE_TUTORIAL = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup_form);

        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        datePickerPopWin = new DatePickerPopWin(new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                birthSelectButton.setBackgroundResource(R.drawable.bg_toggle_button_selected);
                birthSelectButton.setTextColor(Color.parseColor("#ffffff"));
                birthSelectButton.setText(dateDesc);
            }
        }));

        birthSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerPopWin.showPopWin(SignupFormActivity.this);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formContainer.requestFocus();
                signup();
            }
        });

        signupInputName.setOnFocusChangeListener(new SignupInputOnFocusChangeListener(inputNameContainer, signupInputName, nameStatusButton));
        signupInputName.addTextChangedListener(new SignupTextChangedListener(nameStatusButton));

        signupInputNickName.setOnFocusChangeListener(new SignupInputOnFocusChangeListener(inputNickNameContainer, signupInputNickName, nickNameStatusButton));
        signupInputNickName.addTextChangedListener(new SignupTextChangedListener(nickNameStatusButton));

        signupInputEmail.setOnFocusChangeListener(new SignupInputOnFocusChangeListener(inputEmailContainer, signupInputEmail, emailStatusButton));
        signupInputEmail.addTextChangedListener(new SignupTextChangedListener(emailStatusButton));

        signupInputPW.setOnFocusChangeListener(new SignupInputOnFocusChangeListener(inputPWContainer, signupInputPW, pwStatusButton));
        signupInputPW.addTextChangedListener(new SignupTextChangedListener(pwStatusButton));

        signupInputPWConfirm.setOnFocusChangeListener(new SignupInputOnFocusChangeListener(inputPWConfirmContainer, signupInputPWConfirm, pwConfirmStatusButton));
        signupInputPWConfirm.addTextChangedListener(new SignupTextChangedListener(pwConfirmStatusButton));

        nameStatusButton.setOnClickListener(new SignupStatusButtonOnClickListener());
        nickNameStatusButton.setOnClickListener(new SignupStatusButtonOnClickListener());
        emailStatusButton.setOnClickListener(new SignupStatusButtonOnClickListener());
        pwStatusButton.setOnClickListener(new SignupStatusButtonOnClickListener());
        pwConfirmStatusButton.setOnClickListener(new SignupStatusButtonOnClickListener());

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sero.tv/privacy/"));
                startActivity(intent);
            }
        });

        formContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus() instanceof EditText){
                    Utilities.hideKeyboard(SignupFormActivity.this, (EditText) getCurrentFocus());
                }
                v.requestFocus();
            }
        });

        signupInputName.requestFocus();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validationCheck() {
//        if(nameStatusButton.getTag() == null || !nameStatusButton.getTag().equals(R.drawable.btn_input_check)){
//            return false;
//        }else
        if(nickNameStatusButton.getTag() == null || !nickNameStatusButton.getTag().equals(R.drawable.btn_input_check)) {
            Toast.makeText(this, "please check your user name.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(emailStatusButton.getTag() == null || !emailStatusButton.getTag().equals(R.drawable.btn_input_check)){
            Toast.makeText(this, "please check your email.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(pwStatusButton.getTag() == null || !pwStatusButton.getTag().equals(R.drawable.btn_input_check)){
            Toast.makeText(this, "please check your password.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(pwConfirmStatusButton.getTag() == null || !pwConfirmStatusButton.getTag().equals(R.drawable.btn_input_check)){
            Toast.makeText(this, "please check your password confirm.", Toast.LENGTH_SHORT).show();
            return false;
//        }
//        else if(genderRadioGroup.getCheckedRadioButtonId() == genderRadioGroup.getId() - 1){
//            Toast.makeText(this, "Please select your gender", Toast.LENGTH_LONG).show();
//            return false;
//        }else if(birthSelectButton.getText().equals("Select")){
//            Toast.makeText(this, "Please select your birth", Toast.LENGTH_LONG).show();
//            return false;
        }else{
            email = signupInputEmail.getText().toString();
            String password = signupInputPW.getText().toString();
            encryptionPW = Utilities.SHA256(password);
            name = signupInputName.getText().toString();
            nickName = signupInputNickName.getText().toString();

            switch(genderRadioGroup.getCheckedRadioButtonId()){
                case R.id.signupMaleRadioButton :
                    gender = "101-001";
                    break;

                case R.id.signupFemaleRadioButton :
                    gender = "101-002";
                    break;
            }

            birth = birthSelectButton.getText().toString();

            return true;
        }
    }

    private void confirmRepetitionEmail(final LinearLayout container, final ImageButton statusButton){
        Network network = new Network(this, "chkEmail") {
            @Override
            protected void processFinish(JsonNode result) {
                if("Y".equals(result.get("RTN_VAL").asText())){
                    setEditTextStatus(container, statusButton, true);
                }else{
                    if(result.get("MSG") != null && !"".equals(result.get("MSG").asText())){
                        Toast.makeText(SignupFormActivity.this, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(SignupFormActivity.this, "this id is already taken.", Toast.LENGTH_LONG).show();
                    }
                    setEditTextStatus(container, statusButton, false);
                }
            }
        };

        String url = "base";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("EMAIL", signupInputEmail.getText().toString().trim());

        serverCommunicator.communicate();
    }

    private void confirmRepetitionNickName(final LinearLayout container, final ImageButton statusButton){
        Network network = new Network(this, "chkNickName") {
            @Override
            protected void processFinish(JsonNode result) {
                if("Y".equals(result.get("RTN_VAL").asText())){
                    setEditTextStatus(container, statusButton, true);
                }else{
                    if(result.get("MSG") != null && !"".equals(result.get("MSG").asText())){
                        Toast.makeText(SignupFormActivity.this, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(SignupFormActivity.this, "this nickname is already taken.", Toast.LENGTH_LONG).show();
                    }
                    setEditTextStatus(container, statusButton, false);
                }
            }
        };

        String url = "base";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("NICKNAME", signupInputNickName.getText().toString().trim());

        serverCommunicator.communicate();
    }

    private void setEditTextStatus(LinearLayout container, ImageButton statusButton, boolean isValid){
        if(isValid){
            container.setBackgroundResource(R.drawable.bg_form_edittext_default);
            statusButton.setImageResource(R.drawable.btn_input_check);
            statusButton.setTag(R.drawable.btn_input_check);
        }else{
            container.setBackgroundResource(R.drawable.bg_form_edittext_error);
            statusButton.setImageResource(R.drawable.btn_input_delete_red);
            statusButton.setTag(R.drawable.btn_input_delete_red);
        }
    }

    private void signup(){
        if(validationCheck()){
            Network network = new Network(this, null) {
                @Override
                protected void processFinish(JsonNode result) {
                    login(nickName, encryptionPW);
                }
            };

            String url = "join";

            ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

            serverCommunicator.addData("PWD", encryptionPW);
            serverCommunicator.addData("NAME", name);
            serverCommunicator.addData("NICKNAME", nickName);
            serverCommunicator.addData("GENDER", gender);
            serverCommunicator.addData("BIRTH", birth);
            serverCommunicator.addData("APP_ID", UserInfo.getUserAndroidId(this));
            serverCommunicator.addData("EMAIL", email);
            serverCommunicator.addData("SIGN_TP", "100-001");

            serverCommunicator.communicate();
        }
    }

    private void login(String id, String pw){
        Network network = new Network(this, null) {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());
                if(data != null){
                    UserInfo.setUserNum(data.get("USER_NO").asText());
                    UserInfo.setSessAuthKey(data.get("SESS_AUTH_KEY").asText());
                    SharedPreferences prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("sessAuthKey", UserInfo.getSessAuthKey());
                    editor.putString("userNum", UserInfo.getUserNum());
                    editor.apply();
                    Intent intent = new Intent(SignupFormActivity.this, ChooseFirstFollowingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };

        String url = "login";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("ID", id);
        serverCommunicator.addData("PWD", pw);

        serverCommunicator.communicate();
    }

    private class SignupInputOnFocusChangeListener implements View.OnFocusChangeListener{

        private LinearLayout container;
        private EditText editText;
        private ImageButton imageButton;

        SignupInputOnFocusChangeListener(LinearLayout container, EditText editText, ImageButton imageButton) {
            this.container = container;
            this.editText = editText;
            this.imageButton = imageButton;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                container.setBackgroundResource(R.drawable.bg_form_edittext_focused);
                if(imageButton.getVisibility() == View.VISIBLE){
                    imageButton.setImageResource(R.drawable.btn_input_delete_gray);
                    imageButton.setTag(R.drawable.btn_input_delete_gray);
                }
            }else{
                if(editText.getText() != null && !"".equals(editText.getText().toString().trim())){

                    switch (editText.getId()){

                        case R.id.signupInputName :
                            setEditTextStatus(container, imageButton, true);
                            break;

                        case R.id.signupInputNickName :
                            confirmRepetitionNickName(container, imageButton);
                            break;

                        case R.id.signupInputEmail :
                            if(Utilities.isValidEmail(signupInputEmail.getText())){
                                confirmRepetitionEmail(container, imageButton);
                            }else{
                                setEditTextStatus(container, imageButton, false);
                                Toast.makeText(SignupFormActivity.this, "Please check your email", Toast.LENGTH_LONG).show();
                            }
                            break;

                        case R.id.signupInputPW :
                            if(Utilities.isValidPassword(editText.getText())){
                                setEditTextStatus(container, imageButton, true);
                            }else{
                                setEditTextStatus(container, imageButton, false);
                                Toast.makeText(SignupFormActivity.this, "Password is too simple. Try putting alphabet or special characters. Length should be 6~20.", Toast.LENGTH_LONG).show();
                            }

                            break;

                        case R.id.signupInputPWConfirm :
                            if(signupInputPW.getText() != null && !"".equals(signupInputPW.getText().toString().trim())){
                                setEditTextStatus(container, imageButton, signupInputPW.getText().toString().equals(signupInputPWConfirm.getText().toString()));
                            }else{
                                setEditTextStatus(container, imageButton, false);
                            }
                            break;
                    }
                }
                Utilities.hideKeyboard(SignupFormActivity.this, editText);
            }
        }
    }

    private class SignupTextChangedListener implements TextWatcher {

        private ImageButton imageButton;

        SignupTextChangedListener(ImageButton imageButton) {
            this.imageButton = imageButton;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() == 0){
                if(imageButton.getVisibility() == View.VISIBLE){
                    imageButton.setVisibility(View.INVISIBLE);
                }
            }else if(s.length() == 1){
                if(imageButton.getVisibility() == View.INVISIBLE){
                    if(imageButton.getTag() == null || !imageButton.getTag().equals(R.drawable.btn_input_delete_gray)){
                        imageButton.setImageResource(R.drawable.btn_input_delete_gray);
                        imageButton.setTag(R.drawable.btn_input_delete_gray);
                    }
                    imageButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class SignupStatusButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.signupNameStatusButton :
                    if(nameStatusButton.getTag() != null && !nameStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        signupInputName.setText("");
                    }
                    break;

                case R.id.signupNickNameStatusButton :
                    if(nickNameStatusButton.getTag() != null && !nickNameStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        signupInputNickName.setText("");
                    }
                    break;

                case R.id.signupEmailStatusButton :
                    if(emailStatusButton.getTag() != null && !emailStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        signupInputEmail.setText("");
                    }
                    break;

                case R.id.signupPWStatusButton :
                    if(pwStatusButton.getTag() != null && !pwStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        signupInputPW.setText("");
                    }
                    break;

                case R.id.signupPWConfirmStatusButton :
                    if(pwConfirmStatusButton.getTag() != null && !pwConfirmStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        signupInputPWConfirm.setText("");
                    }
                    break;
            }
        }
    }
}
