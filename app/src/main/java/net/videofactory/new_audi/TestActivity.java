package net.videofactory.new_audi;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Utae on 2015-10-29.
 */
public class TestActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void onButtonClicked(View view){
        Log.d("Test","Button Clicked");

        Network network = new Network(this, "getMedia") {
            @Override
            protected void processFinish(JsonNode result) {
                Utilities.logD("Test", "communication success");
                Utilities.logD("Test", "result : " + result.asText());
            }
        };

        String url = "media";

        ServerCommunicator serverCommunicator = new ServerCommunicator(this, network, url);

        serverCommunicator.addData("USER_NO", "U000013");
        serverCommunicator.addData("MAX_NUM", null);
        serverCommunicator.addData("MIN_NUM", null);
        serverCommunicator.addData("FLG", "I");

        serverCommunicator.communicate();


    }


}
