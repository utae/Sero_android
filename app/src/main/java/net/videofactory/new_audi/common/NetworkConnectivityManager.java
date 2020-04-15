package net.videofactory.new_audi.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Utae on 2015-11-06.
 */
public class NetworkConnectivityManager {

    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifiNetworkInfo != null && wifiNetworkInfo.isAvailable() && wifiNetworkInfo.isConnected()){
            return true;
        }else if(mobileNetworkInfo != null && mobileNetworkInfo.isAvailable() && mobileNetworkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }
}
