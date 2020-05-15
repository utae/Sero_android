package net.videofactory.new_audi.alarm;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.video.VideoInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Utae on 2016-05-27.
 */
public class AlarmListFragment extends RefreshFragment {

    private int type; // 0 : You, 1 : Following

    private ArrayList<ItemOfAlarm> alarmDataList;
//    private String IDX = null;
//    private String MAX_IDX = null;

    private AlarmListAdapter alarmListAdapter;

    @BindView(R.id.alarmList) ListView alarmListView;
    @BindView(R.id.alarmListEmptyView) ImageView alarmListEmptyView;
    @BindView(R.id.alarmListRefresher) PtrClassicFrameLayout alarmListRefresher;

    private AlarmListListener alarmListListener;

    public static AlarmListFragment create(int type){
        AlarmListFragment alarmListFragment = new AlarmListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        alarmListFragment.setArguments(args);
        return alarmListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        alarmDataList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        ButterKnife.bind(this, view);

        alarmListView.setEmptyView(alarmListEmptyView);

        selectListData(true);

        return view;
    }

    private void selectListData(boolean refresh){

        setRefreshing(refresh);

        switch (type){
            case 0 :
                selectAlarmOfYou(refresh);
                break;

            case 1 :
                selectAlarmOfFollowing(refresh);
                break;
        }
    }

    private void initRefresher(){
        alarmListRefresher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        alarmListRefresher.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                selectListData(true);
            }
        });
    }

    private void initListView(){
        alarmListAdapter = new AlarmListAdapter(getContext(), alarmDataList);
        alarmListView.setAdapter(alarmListAdapter);

        alarmListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(alarmListListener != null){
                    switch (alarmDataList.get(position).getType()){
                        case "200-001" :
                            if(alarmDataList.get(position).getChannelNum() != null){
                                alarmListListener.goToChannel(alarmDataList.get(position).getChannelNum());
                            }
                            break;

                        case "200-003" :
                        case "200-004" :
                        case "200-006" :
                            if(alarmDataList.get(position).getVideoNum() != null && alarmDataList.get(position).getVideoUrl() != null){
                                alarmListListener.goToVideo(new VideoInfo(null, alarmDataList.get(position).getVideoNum(), alarmDataList.get(position).getVideoUrl()));
                            }
                            break;
                    }
                    Utilities.logD("Alarm", "AlarmList item click : " + position);
                }
            }
        });
    }

    private void setAutoListUpdate(boolean autoListUpdate){
        if(autoListUpdate){
            alarmListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private boolean lastItemVisibleFlag = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag){
                        selectListData(false);
                        setAutoListUpdate(false);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });
        }else{
            alarmListView.setOnScrollListener(null);
        }
    }

    public void setAlarmListListener(AlarmListListener alarmListListener) {
        this.alarmListListener = alarmListListener;
    }

    private void selectAlarmOfYou(final boolean refresh){
//        if(refresh){
//            IDX = null;
//            MAX_IDX = null;
//        }

        Network network = new Network(getContext(), "getYour") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null) {

                    if(refresh){
                        if(!alarmDataList.isEmpty()){
                            alarmDataList.clear();
                        }

//                        MAX_IDX = data.get("MAX_IDX").asText();
                    }

//                    IDX = data.get("IDX").asText();

                    ItemOfAlarm itemOfAlarm;
                    for (JsonNode alarmData : Utilities.jsonParse(data.get("DATA_LIST").asText())) {
                        String type = alarmData.get("RQST_TP").asText();
                        String name = alarmData.get("RQST_NM").asText();
                        String time = alarmData.get("DIFF").asText();

                        switch (alarmData.get("DIFF_FLAG").asText()) {
                            case "D":
                                time += " day ago";
                                break;
                            case "H":
                                time += " hour ago";
                                break;
                            case "M":
                                time += " minute ago";
                                break;
                        }

                        itemOfAlarm = new ItemOfAlarm(type, name, time);

                        switch (type){
                            case "200-001" :
                                itemOfAlarm.setFirstContents("started following you");
                                itemOfAlarm.setChannelNum(alarmData.get("RQST_NO").asText());
                                break;

                            case "200-003" :
                                itemOfAlarm.setFirstContents("uploaded a video");
                                itemOfAlarm.setVideoNum(alarmData.get("REF_NO").asText());
                                itemOfAlarm.setVideoUrl(alarmData.get("MEDIA_URL").asText());
                                break;

                            case "200-004" :
                                itemOfAlarm.setFirstContents("like your video");
                                itemOfAlarm.setVideoNum(alarmData.get("REF_NO").asText());
                                itemOfAlarm.setVideoUrl(alarmData.get("MEDIA_URL").asText());
                                break;

                            case "200-006" :
                                itemOfAlarm.setFirstContents("left a comment on your video");
                                itemOfAlarm.setSecondContents(alarmData.get("REF_CONT").asText());
                                itemOfAlarm.setVideoNum(alarmData.get("MEDIA_NO").asText());
                                itemOfAlarm.setVideoUrl(alarmData.get("MEDIA_URL").asText());
                                break;
                        }

                        if(alarmData.get("RQST_IMG_URL") != null && !"".equals(alarmData.get("RQST_IMG_URL").asText())){
                            itemOfAlarm.setProfileUrl(alarmData.get("RQST_IMG_URL").asText());
                        }

                        alarmDataList.add(itemOfAlarm);

                    }

                    if(alarmListAdapter == null){
                        initListView();
                        initRefresher();
                    }else{
                        alarmListAdapter.notifyDataSetChanged();
                    }

//                    setAutoListUpdate(!IDX.equals(MAX_IDX));

                    if(refresh){
                        setRefreshing(false);
                    }

                    if(alarmListRefresher.isRefreshing()){
                        alarmListRefresher.refreshComplete();
                    }
                }
            }
        };

        String url = "v001/activity";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.communicate();
    }

    private void selectAlarmOfFollowing(final boolean refresh){
        if(refresh){
//            IDX = null;
//            MAX_IDX = null;
        }

        Network network = new Network(getContext(), "getFollowing") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){

                    if(refresh){
                        if(!alarmDataList.isEmpty()){
                            alarmDataList.clear();
                        }
//                        MAX_IDX = data.get("MAX_IDX").asText();
                    }

//                    IDX = data.get("IDX").asText();

                    ItemOfAlarm itemOfAlarm;
                    for (JsonNode alarmData : Utilities.jsonParse(data.get("DATA_LIST").asText())) {
                        String type = alarmData.get("RQST_TP").asText();
                        String name = alarmData.get("RQST_NM").asText();
                        String time = alarmData.get("DIFF").asText();

                        switch (alarmData.get("DIFF_FLAG").asText()) {
                            case "D":
                                time += " day ago";
                                break;
                            case "H":
                                time += " hour ago";
                                break;
                            case "M":
                                time += " minute ago";
                                break;
                        }

                        itemOfAlarm = new ItemOfAlarm(type, name, time);

                        switch (type){
                            case "200-001" :
                                itemOfAlarm.setFirstContents("started following " + alarmData.get("TO_NM").asText());
                                itemOfAlarm.setChannelNum(alarmData.get("TO_NO").asText());
                                break;

                            case "200-003" :
                                itemOfAlarm.setFirstContents("uploaded a video");
                                itemOfAlarm.setVideoNum(alarmData.get("REF_NO").asText());
                                itemOfAlarm.setVideoUrl(alarmData.get("MEDIA_URL").asText());
                                break;

                            case "200-004" :
                                itemOfAlarm.setFirstContents("like your video");
                                itemOfAlarm.setVideoNum(alarmData.get("REF_NO").asText());
                                itemOfAlarm.setVideoUrl(alarmData.get("MEDIA_URL").asText());
                                break;

                            case "200-006" :
                                itemOfAlarm.setFirstContents("left a comment on your video");
                                itemOfAlarm.setSecondContents(alarmData.get("REF_CONT").asText());
                                itemOfAlarm.setVideoNum(alarmData.get("MEDIA_NO").asText());
                                itemOfAlarm.setVideoUrl(alarmData.get("MEDIA_URL").asText());
                                break;
                        }

                        if(alarmData.get("RQST_IMG_URL") != null && !"".equals(alarmData.get("RQST_IMG_URL").asText())){
                            itemOfAlarm.setProfileUrl(alarmData.get("RQST_IMG_URL").asText());
                        }

                        alarmDataList.add(itemOfAlarm);

                    }

                    if(alarmListAdapter == null){
                        initListView();
                        initRefresher();
                    }else{
                        alarmListAdapter.notifyDataSetChanged();
                    }

//                    setAutoListUpdate(!IDX.equals(MAX_IDX));

                    if(refresh){
                        setRefreshing(false);
                    }

                    if(alarmListRefresher.isRefreshing()){
                        alarmListRefresher.refreshComplete();
                    }
                }
            }
        };

        String url = "v001/activity";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.communicate();
    }

    @Override
    public void refreshFragment() {
        if(!alarmListView.canScrollVertically(-1)){
            if(!isRefreshing()){
                selectListData(true);
            }
        }else{
            alarmListView.smoothScrollToPosition(0);
        }
    }

    public interface AlarmListListener{
        void goToChannel(String channelNum);
        void goToVideo(VideoInfo videoInfo);
    }
}
