package net.videofactory.new_audi.login;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-12-23.
 */
public class LoginFormActivity extends AppCompatActivity {

    @BindView(R.id.loginFormContainer) LinearLayout formContainer;
    @BindView(R.id.loginInputId) EditText loginInputId;
    @BindView(R.id.loginInputPW) EditText loginInputPW;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.loginInputIdContainer) LinearLayout inputIdContainer;
    @BindView(R.id.loginInputPWContainer) LinearLayout inputPWContainer;
    @BindView(R.id.loginIdStatusButton) ImageButton idStatusButton;
    @BindView(R.id.loginPWStatusButton) ImageButton pwStatusButton;
    @BindView(R.id.loginFindPWButton) TextView findPWButton;
    @BindView(R.id.loginBackButton) ImageButton backButton;

    private String id, pw, encryptionPW = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_form);

        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        id = getIntent().getStringExtra("id");

        encryptionPW = getIntent().getStringExtra("pw");

        if(id != null && encryptionPW != null){
            loginInputId.setText(id);
            loginInputPW.setText(encryptionPW);
            login(id, encryptionPW);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        loginInputId.setOnFocusChangeListener(new LoginInputOnFocusChangeListener());
        loginInputId.addTextChangedListener(new LoginTextChangedListener(idStatusButton));

        loginInputPW.setOnFocusChangeListener(new LoginInputOnFocusChangeListener());
        loginInputPW.addTextChangedListener(new LoginTextChangedListener(pwStatusButton));

        idStatusButton.setOnClickListener(new LoginStatusButtonOnClickListener(loginInputId));
        pwStatusButton.setOnClickListener(new LoginStatusButtonOnClickListener(loginInputPW));

        findPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginFormActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });

        loginInputId.requestFocus();

        formContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus() instanceof EditText){
                    Utilities.hideKeyboard(LoginFormActivity.this, (EditText) getCurrentFocus());
                }
                v.requestFocus();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void login(){
        if("".equals(loginInputId.getText().toString().trim())){
            Toast.makeText(this, "please check your id", Toast.LENGTH_LONG).show();
        }else if("".equals(loginInputPW.getText().toString().trim())){
            Toast.makeText(this, "please enter your password", Toast.LENGTH_LONG).show();
        }else{
            id = loginInputId.getText().toString();
            pw = loginInputPW.getText().toString();
            encryptionPW = Utilities.SHA256(pw);
            this.login(id, encryptionPW);
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
                    Intent intent = new Intent(LoginFormActivity.this, MainActivity.class);
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

    private class LoginInputOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus && v instanceof EditText){
                Utilities.hideKeyboard(LoginFormActivity.this, (EditText)v);
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

        private EditText editText;

        public LoginStatusButtonOnClickListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onClick(View v) {
            if(v.getTag().equals(R.drawable.btn_input_delete_gray)){
                editText.setText("");
            }
        }
    }
}
