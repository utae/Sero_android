package net.videofactory.new_audi.setting;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.videofactory.new_audi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-07-21.
 */

public class SettingFragment extends Fragment {

    @Bind(R.id.settingSignOut) TextView signOutButton;
    @Bind(R.id.settingChangePassword) TextView changePWButton;
    @Bind(R.id.settingTutorial) TextView tutorialButton;
    @Bind(R.id.settingTerms) TextView terms;
    @Bind(R.id.settingPrivacyPolicy) TextView privacyPolicy;

    private SettingListener settingListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        ButterKnife.bind(this, view);

        initListener();

        return view;
    }

    private void initListener(){
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingListener != null){
                    settingListener.signOut();
                }
            }
        });

        changePWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingListener != null){
                    settingListener.changePassword();
                }
            }
        });

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingListener != null){
                    settingListener.tutorial();
                }
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sero.tv/eula/"));
                startActivity(intent);
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sero.tv/privacy/"));
                startActivity(intent);
            }
        });
    }

    public void setSettingListener(SettingListener settingListener) {
        this.settingListener = settingListener;
    }

    public interface SettingListener{
        void signOut();
        void changePassword();
        void tutorial();
    }
}
