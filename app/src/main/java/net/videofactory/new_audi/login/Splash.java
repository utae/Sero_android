package net.videofactory.new_audi.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.JsonNode;

import io.fabric.sdk.android.Fabric;
import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.main.MainActivity;

/**
 * Created by Utae on 2016-07-19.
 */
public class Splash extends AppCompatActivity {

    private SharedPreferences prefs;

    private final int AUDI_PERMISSIONS_INTERNET = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        requestNetworkStatePermission();
    }

    private void requestNetworkStatePermission(){
        if(ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                    Manifest.permission.INTERNET) || ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                    Manifest.permission.ACCESS_NETWORK_STATE)){

                //TODO 권한요청 toast 띄우기

            }else{
                ActivityCompat.requestPermissions(Splash.this,
                        new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                        AUDI_PERMISSIONS_INTERNET);
            }
        }else{
            checkVer();
        }
    }

    private void checkAutoLogin(){
        prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);

        if(prefs != null){
            if(checkSessAuthKey()){
                autoLogin();
            }else{
                goToLoginHome();
            }
        }else{
            goToLoginHome();
        }
    }

    private boolean checkSessAuthKey(){
        return prefs.getString("sessAuthKey", null) != null;
    }

    private void autoLogin(){
        UserInfo.setSessAuthKey(prefs.getString("sessAuthKey", null));
        UserInfo.setUserNum(prefs.getString("userNum", null));
//        UserInfo.setUserName(prefs.getString("name", null));
//        UserInfo.setNickName(prefs.getString("nickName", null));
//        UserInfo.setGender(prefs.getInt("gender", -1));
        if(UserInfo.isNull()){
            goToLoginHome();
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void goToLoginHome(){
        Intent intent = new Intent(Splash.this, LoginHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    private void checkVer(){
        Network network = new Network(this, null) {
            @Override
            protected void processFinish(JsonNode result) {
                if (result.get("RTN_VAL").asText().equals("Y")){
                    checkAutoLogin();
                }else{
                    //최신버젼이 아닌경우
                    String marketUrl = result.get("VERSION_URL").asText();
                    //TODO google play에 app update 창으로 이동

                    Toast.makeText(Splash.this, "You have to update", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void connectFail() {
                Toast.makeText(Splash.this, "Cannot connect to server..", Toast.LENGTH_LONG).show();
            }
        };

        String url = "version";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("VERSION", "0.0.1");

        serverCommunicator.communicate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case AUDI_PERMISSIONS_INTERNET :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    checkVer();
                }else{
                    //TODO PERMISSION DENIED
                }
                break;
        }
    }
}
