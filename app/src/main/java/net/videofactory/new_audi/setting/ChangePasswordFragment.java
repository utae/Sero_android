package net.videofactory.new_audi.setting;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.videofactory.new_audi.main.OnBackButtonClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-07-21.
 */

public class ChangePasswordFragment extends Fragment {

    @BindView(R.id.changePWFormContainer) LinearLayout formContainer;
    @BindView(R.id.changePWCurPWContainer) LinearLayout curPWContainer;
    @BindView(R.id.changePWCurPW) EditText curPW;
    @BindView(R.id.changePWCurPWStatusButton) ImageButton curPWStatusButton;
    @BindView(R.id.changePWNewPWContainer) LinearLayout newPWContainer;
    @BindView(R.id.changePWNewPw) EditText newPW;
    @BindView(R.id.changePWNewPWStatusButton) ImageButton newPWStatusButton;
    @BindView(R.id.changePWConfirmPWContainer) LinearLayout confirmPWContainer;
    @BindView(R.id.changePWConfirmPW) EditText confirmPW;
    @BindView(R.id.changePWConfirmPWStatusButton) ImageButton confirmPWStatusButton;
    @BindView(R.id.changePWButton) Button changePWButton;
    @BindView(R.id.changePWBackButton) ImageButton backButton;

    private ChangePasswordListener changePasswordListener;
    private OnBackButtonClickListener onBackButtonClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        ButterKnife.bind(this, view);

        initListener();

        return view;
    }

    private void initListener(){
        curPW.setOnFocusChangeListener(new ChangePWOnFocusChangeListener(curPWContainer, curPW, curPWStatusButton));
        curPW.addTextChangedListener(new ChangePWTextChangedListener(curPWStatusButton));

        newPW.setOnFocusChangeListener(new ChangePWOnFocusChangeListener(newPWContainer, newPW, newPWStatusButton));
        newPW.addTextChangedListener(new ChangePWTextChangedListener(newPWStatusButton));

        confirmPW.setOnFocusChangeListener(new ChangePWOnFocusChangeListener(confirmPWContainer, confirmPW, confirmPWStatusButton));
        confirmPW.addTextChangedListener(new ChangePWTextChangedListener(confirmPWStatusButton));

        curPWStatusButton.setOnClickListener(new ChangePWStatusButtonOnClickListener());
        newPWStatusButton.setOnClickListener(new ChangePWStatusButtonOnClickListener());
        confirmPWStatusButton.setOnClickListener(new ChangePWStatusButtonOnClickListener());

        changePWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
                if(validationCheck()){
                    changePW();
                }
            }
        });

        formContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
            }
        });

        curPW.requestFocus();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onBackButtonClickListener != null){
                    onBackButtonClickListener.onBackButtonClick();
                }
            }
        });
    }

    private boolean validationCheck(){
        if(curPWStatusButton.getTag() == null || !curPWStatusButton.getTag().equals(R.drawable.btn_input_check)){
            Toast.makeText(getContext(), "Please check your current password", Toast.LENGTH_LONG).show();
            return false;
        }else if(newPWStatusButton.getTag() == null || !newPWStatusButton.getTag().equals(R.drawable.btn_input_check)) {
            Toast.makeText(getContext(), "Please check your new password", Toast.LENGTH_LONG).show();
            return false;
        }else if(confirmPWStatusButton.getTag() == null || !confirmPWStatusButton.getTag().equals(R.drawable.btn_input_check)){
            Toast.makeText(getContext(), "Please check your new password confirm", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setEditTextStatus(LinearLayout container, ImageButton statusButton, boolean isValid){
        if(isValid){
            container.setSelected(false);
            statusButton.setImageResource(R.drawable.btn_input_check);
            statusButton.setTag(R.drawable.btn_input_check);
        }else{
            container.setSelected(true);
            statusButton.setImageResource(R.drawable.btn_input_delete_red);
            statusButton.setTag(R.drawable.btn_input_delete_red);
        }
    }

    private void changePW(){
        Network network = new Network(getContext(), "txChangePwd") {
            @Override
            protected void processFinish(JsonNode result) {
                Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                if(changePasswordListener != null){
                    changePasswordListener.onChangePassword();
                }
            }
        };

        String url = "v001/home/view";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("PWD_BAK", Utilities.SHA256(curPW.getText().toString()));
        serverCommunicator.addData("PWD", Utilities.SHA256(newPW.getText().toString()));

        serverCommunicator.communicate();
    }

    public void setChangePasswordListener(ChangePasswordListener changePasswordListener) {
        this.changePasswordListener = changePasswordListener;
    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.onBackButtonClickListener = onBackButtonClickListener;
    }

    public interface ChangePasswordListener{
        void onChangePassword();
    }

    private class ChangePWOnFocusChangeListener implements View.OnFocusChangeListener{

        private LinearLayout container;
        private EditText editText;
        private ImageButton imageButton;

        ChangePWOnFocusChangeListener(LinearLayout container, EditText editText, ImageButton imageButton) {
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

                        case R.id.changePWCurPW :
                            setEditTextStatus(container, imageButton, true);
                            break;

                        case R.id.changePWNewPw :
                            setEditTextStatus(container, imageButton, Utilities.isValidPassword(newPW.getText()));
                            break;

                        case R.id.changePWConfirmPW :
                            if(newPWStatusButton.getTag() != null && newPWStatusButton.getTag().equals(R.drawable.btn_input_check)){
                                setEditTextStatus(container, imageButton, confirmPW.getText().toString().equals(newPW.getText().toString()));
                            }else{
                                setEditTextStatus(container, imageButton, false);
                            }
                            break;
                    }
                }
                Utilities.hideKeyboard(getContext(), editText);
            }
        }
    }

    private class ChangePWTextChangedListener implements TextWatcher {

        private ImageButton imageButton;

        ChangePWTextChangedListener(ImageButton imageButton) {
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

    private class ChangePWStatusButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.changePWCurPWStatusButton :
                    if(curPWStatusButton.getTag() != null && !curPWStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        curPW.setText("");
                    }
                    break;

                case R.id.changePWNewPWStatusButton :
                    if(newPWStatusButton.getTag() != null && !newPWStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        newPW.setText("");
                    }
                    break;

                case R.id.changePWConfirmPWStatusButton :
                    if(confirmPWStatusButton.getTag() != null && !confirmPWStatusButton.getTag().equals(R.drawable.btn_input_check)){
                        confirmPW.setText("");
                    }
                    break;
            }
        }
    }
}
