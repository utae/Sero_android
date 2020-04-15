package net.videofactory.new_audi.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.Utilities;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-08-05.
 */

public class FindPasswordActivity extends AppCompatActivity {

    @Bind(R.id.findPWFormContainer) LinearLayout formContainer;
    @Bind(R.id.findPWBackButton) ImageButton backButton;
    @Bind(R.id.findPWEmailContainer) LinearLayout emailContainer;
    @Bind(R.id.findPWEmail) EditText emailEditText;
    @Bind(R.id.findPWEmailStatus) ImageButton emailStatusButton;
    @Bind(R.id.findPWButton) Button findButton;

    private AlertDialog.Builder alertBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        ButterKnife.bind(this);

        emailEditText.setOnFocusChangeListener(new FindPWOnFocusChangeListener(emailContainer, emailEditText, emailStatusButton));
        emailEditText.addTextChangedListener(new FindPWTextChangedListener(emailStatusButton));
        emailStatusButton.setOnClickListener(new FindPWStatusButtonOnClickListener());

        initDialog();

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utilities.isValidEmail(emailEditText.getText().toString().trim())){
                    findPW();
                }else{
                    Toast.makeText(FindPasswordActivity.this, "invalid email", Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        formContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus() instanceof EditText){
                    Utilities.hideKeyboard(FindPasswordActivity.this, (EditText)getCurrentFocus());
                }
                v.requestFocus();
            }
        });

        emailEditText.requestFocus();
    }

    private void initDialog(){
        alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setTitle("Check Your Email")
                .setMessage("Username and New password have just been sent to your e-mail address.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(FindPasswordActivity.this, LoginHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

        alertDialog = alertBuilder.create();
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

    private void findPW(){
        Network network = new Network(this, "findPwd") {
            @Override
            protected void processFinish(JsonNode result) {
                if(alertDialog != null){
                    alertDialog.show();
                }
            }
        };

        String url = "base";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("EMAIL", emailEditText.getText().toString().trim());

        serverCommunicator.communicate();
    }

    private class FindPWOnFocusChangeListener implements View.OnFocusChangeListener{

        private LinearLayout container;
        private EditText editText;
        private ImageButton imageButton;

        FindPWOnFocusChangeListener(LinearLayout container, EditText editText, ImageButton imageButton) {
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
                    setEditTextStatus(container, imageButton, Utilities.isValidEmail(editText.getText()));
                }
                Utilities.hideKeyboard(FindPasswordActivity.this, editText);
            }
        }
    }

    private class FindPWTextChangedListener implements TextWatcher {

        private ImageButton imageButton;

        FindPWTextChangedListener(ImageButton imageButton) {
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

    private class FindPWStatusButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.findPWEmailStatus :
                    if(emailStatusButton.getTag().equals(R.drawable.btn_input_delete_gray)){
                        emailEditText.setText("");
                    }
                    break;
            }
        }
    }
}
