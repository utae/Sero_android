package net.videofactory.new_audi.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.videofactory.new_audi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-07.
 */
public class OfflineActivity extends AppCompatActivity {

    @Bind(R.id.reconnectButton)
    Button reconnectButton;
    BackPressCloseSystem backPressCloseSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offline);

        ButterKnife.bind(this);

        backPressCloseSystem = new BackPressCloseSystem(this);

        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkConnectivityManager.isOnline(getApplicationContext())){
                    finish();
                }else{
                    Toast.makeText(OfflineActivity.this, "Please check your network.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }
}
