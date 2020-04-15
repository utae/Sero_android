package net.videofactory.new_audi.custom_view.audi_video_view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.custom_view.audi_video_view.AudiSurfaceVideoView;

/**
 * Created by Utae on 2015-11-17.
 */
public class AudiVideoView extends AudiSurfaceVideoView {

    private Activity activity;
    private String mediaNum;
    private long startTime;
    private long endTime;

    private final long defaultTime = 5000;

    public AudiVideoView(Context context) {
        super(context);
    }

    public AudiVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudiVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setMediaNum(String mediaNum){
        this.mediaNum = mediaNum;
    }

    @Override
    public void start() {
        super.start();
        startTime = System.currentTimeMillis();
        endTime = 0;
    }

    @Override
    public void pause() {
        endTime = System.currentTimeMillis();
        if(endTime - startTime > defaultTime){
            insertViewLog(startTime, endTime);
        }
        super.pause();
    }

    private void insertViewLog(long startTime, long endTime){
        Network network = new Network(getContext(), "txViewTmSave") {
            @Override
            protected void processFinish(JsonNode result) {
                if("N".equals(result.get("RTN_VAL").asText())){
                    Toast.makeText(activity, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                }
            }
        };

        String url = "chart";

        ServerCommunicator serverCommunicator = new ServerCommunicator(activity, network, url);

        serverCommunicator.addData("MEDIA_NO", mediaNum);
        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("VIEW_TM", getWatchingTime(endTime - startTime));

        serverCommunicator.communicate();
    }

    private String getWatchingTime(long TimeMillis){
        return Long.toString(TimeMillis / 1000);
    }

}
