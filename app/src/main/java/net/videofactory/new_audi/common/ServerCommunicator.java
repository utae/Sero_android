package net.videofactory.new_audi.common;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Utae on 2015-11-06.
 */
public class ServerCommunicator {

    private Context context;

    private String timestamp;
    private Network network;
    private String url;
    private ArrayList<HashMap<String,Object>> dataList;

    public ServerCommunicator(Context context, Network network, String url) {
        this.context = context;
        this.network = network;
//        this.url = "https://app.sero.tv/" + url;
        this.url = "http://dev.sero.tv/" + url;

        dataList = new ArrayList<>();
        dataList.add(new HashMap<String, Object>());
    }

    public void setTimestamp(long currentTimeMillis){
        timestamp = Long.toString(currentTimeMillis);
    }

    public void addData(String key, Object value){
        addData(0, key, value);
    }

    public void addData(int dataSetNum, String key, Object value){
        dataList.get(dataSetNum).put(key, value);
    }

    public void addDataSet(){
        dataList.add(new HashMap<String, Object>());
    }

    public void communicate(){
        //네트워크 상태확인
        if(NetworkConnectivityManager.isOnline(context)){
            if(timestamp == null){
                timestamp = Long.toString(System.currentTimeMillis());
            }
            for(HashMap<String,Object> data : dataList){
                data.put("TIMESTAMP", timestamp);
                data.put("SESS_AUTH_KEY", UserInfo.getSessAuthKey());
                data.put("APP_ID", UserInfo.getUserAndroidId(context));
                data.put("SESS_NO", UserInfo.getUserNum());
                data.put("OS_TP", "010-001");
            }
            network.setTimeStamp(timestamp);
            network.setList(dataList);
            network.execute(url);
        }else{
            Intent intent = new Intent(context,OfflineActivity.class);
            context.startActivity(intent);
        }
    }

}
