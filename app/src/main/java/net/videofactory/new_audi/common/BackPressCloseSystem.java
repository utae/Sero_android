package net.videofactory.new_audi.common;

import android.app.Activity;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Utae on 2015-11-09.
 */
public class BackPressCloseSystem {

    private long backKeyPressedTime = 0L;

    private Toast toast;

    private Activity activity;

    private OnBackPressCloseListener onBackPressCloseListener = null;

    public BackPressCloseSystem(Activity activity) {
        this.activity = activity;
    }

    public void setOnBackPressCloseListener(OnBackPressCloseListener onBackPressCloseListener) {
        this.onBackPressCloseListener = onBackPressCloseListener;
    }

    public void onBackPressed() {

        if(backKeyPressedTime != 0 && SystemClock.uptimeMillis() - backKeyPressedTime < 3000){
            if(onBackPressCloseListener != null){
                onBackPressCloseListener.onBackPressClose();
            }
            insertConnectLog();
            UserInfo.clearUserInfo();
            AppEventsLogger.deactivateApp(activity);
            activity.finishAffinity();
            toast.cancel();
        }else{
            toast = Toast.makeText(activity, "Press the back button once again to close the application.", Toast.LENGTH_SHORT);
            toast.show();
            backKeyPressedTime = SystemClock.uptimeMillis();
        }
    }

    //권장되는 방법이 아니라서 사용은 안하지만 혹시 몰라서 주석처리
//    private void programShutdown() {
//        activity.moveTaskToBack(true);
//        activity.finish();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
//    }

    public void insertConnectLog() {

//        long currentTimeMillis = System.currentTimeMillis();
//
//        String connectTime = Utilities.transTimeFormatFromTimeMillis(currentTimeMillis);
//
//        String connectKey = UserInfo.getConnectKey();
//
//        if(connectKey == null){
//            connectKey = connectTime + UserInfo.getUserId(activity);
//            UserInfo.setConnectKey(connectKey);
//        }
//
//        Network network = new Network("txLConn") {
//            @Override
//            protected void processFinish(JsonNode result) {
//                UserInfo.clearUserInfo();
//                activity.finishAffinity();
//            }
//
//            @Override
//            protected void connectFail() {
//                activity.finishAffinity();
//            }
//        };
//
//        String url = "lConn";
//
//        ServerCommunicator serverCommunicator = new ServerCommunicator(activity, network, url);
//
//        serverCommunicator.setTimestamp(currentTimeMillis);
//
//        serverCommunicator.addData("CONKEY", connectKey);
//        serverCommunicator.addData("CONN_TIME", connectTime);
//        serverCommunicator.addData("FRM_TO", "T");
//        serverCommunicator.addData("OS_TYPE", "200-001");
//
//        serverCommunicator.communicate();
    }

    public interface OnBackPressCloseListener{
        void onBackPressClose();
    }
}
