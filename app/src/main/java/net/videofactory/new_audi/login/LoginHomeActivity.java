package net.videofactory.new_audi.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.BackPressCloseSystem;
import net.videofactory.new_audi.encryption.CrytAudi;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-12-17.
 */
public class LoginHomeActivity extends AppCompatActivity {

    @Bind(R.id.loginBtnContainer) LinearLayout loginBtnContainer;
    @Bind(R.id.loginFbButton) LinearLayout loginFbButton;
    @Bind(R.id.loginEmailButton) LinearLayout loginEmailButton;
    @Bind(R.id.signupEmailButton) Button signupEmailButton;
    @Bind(R.id.loginBackgroundVideoView) VideoView backgroundVideoView;
    @Bind(R.id.logoView) ImageView logoView;
    @Bind(R.id.loginTerms) TextView terms;
    @Bind(R.id.loginPrivacyPolicy) TextView privacyPolicy;

    private CallbackManager callbackManager;

    private String name, email, gender, birth;

    private SharedPreferences prefs;

    private LoginManager loginManager;

    private BackPressCloseSystem backPressCloseSystem;

    private final String[] FB_PERMISSIONS = {"public_profile", "email", "user_birthday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login_home);

        ButterKnife.bind(this);

        test();

        callbackManager = CallbackManager.Factory.create();

        loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFbInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Utilities.logD("Fb", "fb login is canceled");
            }

            @Override
            public void onError(FacebookException e) {
                if(e instanceof FacebookAuthorizationException){
                    if(AccessToken.getCurrentAccessToken() != null){
                        LoginManager.getInstance().logOut();
                    }
                }
                Utilities.logD("Fb", "fb err : " + e.toString());
            }
        });

        loginFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermissions = false;
                for (String permission : FB_PERMISSIONS){
                    hasPermissions = checkFbPermission(permission);
                }
                if(hasPermissions){
                    requestFbInfo(AccessToken.getCurrentAccessToken());
                }else{
                    loginManager.logInWithReadPermissions(LoginHomeActivity.this, Arrays.asList("public_profile", "email", "user_birthday"));
                }
            }
        });

        backgroundVideoView.setVideoURI(Uri.parse("https://d2w1dgw1e9awhj.cloudfront.net/video/main.mp4"));

        backgroundVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                backgroundVideoView.start();
                logoView.setVisibility(View.VISIBLE);
                showLoginBtn();
            }
        });

        backgroundVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                backgroundVideoView.start();
            }
        });

        loginEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginHomeActivity.this, LoginFormActivity.class);
                startActivity(intent);
            }
        });

        signupEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginHomeActivity.this, SignupFormActivity.class);
                startActivity(intent);
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

        backPressCloseSystem = new BackPressCloseSystem(this);

        backPressCloseSystem.setOnBackPressCloseListener(new BackPressCloseSystem.OnBackPressCloseListener() {
            @Override
            public void onBackPressClose() {
                UserInfo.clearUserInfo();
            }
        });
    }

    private boolean checkFbPermission(String permission){
        return AccessToken.getCurrentAccessToken().getPermissions().contains(permission);
    }

    private void requestFbInfo(final AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                Utilities.logD("Fb", "fb login succeed : " + graphResponse.toString());
                fbLogin(accessToken, jsonObject);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, email, gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private boolean checkSessAuthKey(){
        prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return prefs != null && prefs.getString("sessAuthKey", null) != null;
    }

    private void autoLogin(){
        UserInfo.setSessAuthKey(prefs.getString("sessAuthKey", null));
        UserInfo.setUserNum(prefs.getString("userNum", null));
        if(UserInfo.isNull()){
            showLoginBtn();
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void showLoginBtn(){
        loginBtnContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void fbSignup(AccessToken accessToken, boolean useFbEmail){
        Intent intent = new Intent(LoginHomeActivity.this, FbSignupFormActivity.class);
        if(useFbEmail){
            intent.putExtra("email", email);
        }
        intent.putExtra("name", name);
        intent.putExtra("gender", gender);
        intent.putExtra("birth", birth);
        intent.putExtra("fbId", accessToken.getUserId());
        intent.putExtra("fbToken", accessToken.getToken());
        startActivity(intent);
    }

    private void fbLogin(final AccessToken accessToken, final JSONObject jsonObject){
        Network network = new Network(this, "txChkFbookEmail") {
            @Override
            protected void processFinish(JsonNode result) {
                prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());
                if(data != null){
                    if(checkSessAuthKey()){
                        editor.clear();
                    }
                    editor.putString("sessAuthKey", data.get("SESS_AUTH_KEY").asText());
                    editor.putString("userNum", data.get("USER_NO").asText());
                    editor.apply();

                    autoLogin();
                }else{
                    return;
                }
            }

            @Override
            protected void onResultN(JsonNode result) {
                try {
                    name = jsonObject.get("name").toString();
                    email = jsonObject.get("email").toString();
                    gender = jsonObject.get("gender").toString();
                    birth = jsonObject.get("birthday").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                if(result.get("MSG") == null || "".equals(result.get("MSG").asText())){
                    fbSignup(accessToken, true);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginHomeActivity.this);
                    builder.setTitle("Block User")
                            .setMessage(result.get("MSG").asText())
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    fbSignup(accessToken, false);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(LoginHomeActivity.this, LoginFormActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.create().show();
                }
            }
        };

        String url = "base";

        ServerCommunicator serverCommunicator = new ServerCommunicator(LoginHomeActivity.this, network, url);

        serverCommunicator.addData("SIGN_TP", "100-002");
        try {
            serverCommunicator.addData("FB_EMAIL", jsonObject.get("email").toString());
            serverCommunicator.addData("FB_ID", accessToken.getUserId());
            serverCommunicator.addData("REF_KEY1", accessToken.getToken());
            serverCommunicator.communicate();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginHomeActivity.this, "Facebook login is failed", Toast.LENGTH_LONG).show();
        }
    }

    private void test(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "net.videofactory.new_audi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Utilities.logD("Test", "HashKey : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {

        }
    }
}
