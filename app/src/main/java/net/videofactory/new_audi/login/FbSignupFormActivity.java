package net.videofactory.new_audi.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-12-23.
 */
public class FbSignupFormActivity extends AppCompatActivity {

    @Bind(R.id.fbSignupFormContainer) LinearLayout formContainer;
    @Bind(R.id.fbSignupInputId) EditText fbInputId;
    @Bind(R.id.fbSignupInputEmail) EditText fbInputEmail;
    @Bind(R.id.fbSignupInputIdContainer) LinearLayout inputIdContainer;
    @Bind(R.id.fbSignupInputEmailContainer) LinearLayout inputEmailContainer;
    @Bind(R.id.fbSignupIdStatusButton) ImageButton idStatusButton;
    @Bind(R.id.fbSignupEmailStatusButton) ImageButton emailStatusButton;
    @Bind(R.id.fbSignupButton) Button fbSignupButton;
    @Bind(R.id.fbSignupBackButton) ImageButton backButton;

    private String name, gender, birth, fbId, fbToken, id, email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fb_signup_form);

        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        gender = getIntent().getStringExtra("gender");
        birth = getIntent().getStringExtra("birth");
        fbId = getIntent().getStringExtra("fbId");
        fbToken = getIntent().getStringExtra("fbToken");

        if(email != null){
            inputEmailContainer.setVisibility(View.GONE);
        }

        fbSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbSignup();
            }
        });

        fbInputId.setOnFocusChangeListener(new LoginInputOnFocusChangeListener(inputIdContainer, fbInputId, idStatusButton));
        fbInputId.addTextChangedListener(new LoginTextChangedListener(idStatusButton));

        fbInputEmail.setOnFocusChangeListener(new LoginInputOnFocusChangeListener(inputEmailContainer, fbInputEmail, emailStatusButton));
        fbInputEmail.addTextChangedListener(new LoginTextChangedListener(emailStatusButton));

        idStatusButton.setOnClickListener(new LoginStatusButtonOnClickListener());
        emailStatusButton.setOnClickListener(new LoginStatusButtonOnClickListener());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        formContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
            }
        });

        fbInputId.requestFocus();
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

    private void fbSignup(){
        if(!idStatusButton.getTag().equals(R.drawable.btn_input_check)){
            Toast.makeText(this, "please check your id", Toast.LENGTH_LONG).show();
        }else if(inputEmailContainer.getVisibility() == View.VISIBLE && !emailStatusButton.getTag().equals(R.drawable.btn_input_check)){
            Toast.makeText(this, "please check your email", Toast.LENGTH_LONG).show();
        }else{
            id = fbInputId.getText().toString().trim();
            if(email == null){
                email = fbInputEmail.getText().toString().trim();
            }
            fbSignup(id, email);
        }
    }

    private void fbSignup(String id, String email){
        Network network = new Network(this, null) {
            @Override
            protected void processFinish(JsonNode result) {
                if(result.get("RTN_VAL").asText().equals("Y")){
                    JsonNode data = Utilities.jsonParse(result.get("DATA").asText());
                    if(data != null){
                        UserInfo.setUserNum(data.get("USER_NO").asText());
                        UserInfo.setSessAuthKey(data.get("SESS_AUTH_KEY").asText());
                        SharedPreferences prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("sessAuthKey", UserInfo.getSessAuthKey());
                        editor.putString("userNum", UserInfo.getUserNum());
                        editor.apply();
                        Intent intent = new Intent(FbSignupFormActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(FbSignupFormActivity.this, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                }
            }
        };

        String url = "flogin";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("SIGN_TP", "100-002");
        serverCommunicator.addData("NAME", name);
        serverCommunicator.addData("NICKNAME", id);
        serverCommunicator.addData("GENDER", gender);
        serverCommunicator.addData("BIRTH", birth);
        serverCommunicator.addData("FB_EMAIL", email);
        serverCommunicator.addData("FB_ID", fbId);
        serverCommunicator.addData("REF_KEY1", fbToken);

        serverCommunicator.communicate();
    }

    private void confirmRepetitionEmail(final LinearLayout container, final ImageButton statusButton){
        Network network = new Network(this, "chkEmail") {
            @Override
            protected void processFinish(JsonNode result) {
                setEditTextStatus(container, statusButton, true);
            }

            @Override
            protected void onResultN(JsonNode result) {
                setEditTextStatus(container, statusButton, false);
                super.onResultN(result);
            }
        };

        String url = "base";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("EMAIL", fbInputEmail.getText().toString().trim());

        serverCommunicator.communicate();
    }

    private class LoginInputOnFocusChangeListener implements View.OnFocusChangeListener{

        private LinearLayout container;
        private EditText editText;
        private ImageButton imageButton;

        LoginInputOnFocusChangeListener(LinearLayout container, EditText editText, ImageButton imageButton) {
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
                if(editText.getText() != null && !"".equals(editText.getText().toString())){
                    switch (editText.getId()){
                        case R.id.fbSignupInputId :
                            setEditTextStatus(container, imageButton, true);
                            break;

                        case R.id.fbSignupInputEmail :
                            if(Utilities.isValidEmail(fbInputEmail.getText())){
                                confirmRepetitionEmail(container, imageButton);
                            }else{
                                setEditTextStatus(container, imageButton, false);
                                Toast.makeText(FbSignupFormActivity.this, "invalid email", Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                }
                Utilities.hideKeyboard(FbSignupFormActivity.this, editText);
            }
        }
    }

    private class LoginTextChangedListener implements TextWatcher {

        private ImageButton imageButton;

        LoginTextChangedListener(ImageButton imageButton) {
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

    private class LoginStatusButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.fbSignupIdStatusButton :
                    if(idStatusButton.getTag().equals(R.drawable.btn_input_delete_gray)){
                        fbInputId.setText("");
                    }
                    break;

                case R.id.fbSignupEmailStatusButton :
                    if(emailStatusButton.getTag().equals(R.drawable.btn_input_delete_gray)){
                        fbInputEmail.setText("");
                    }
                    break;
            }
        }
    }
}
