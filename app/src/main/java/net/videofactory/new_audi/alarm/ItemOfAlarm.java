package net.videofactory.new_audi.alarm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Utae on 2016-05-26.
 */
public class ItemOfAlarm{

    private String type; //200-001 : channel follow, 200-003 : media upload, 200-004 : like media, 200-006 : write comment

    private String profileUrl = null;

    private String name;

    private String time;

    private String firstContents;

    private String secondContents;

    private String channelNum;

    private String videoNum;

    private String videoUrl;

    public ItemOfAlarm(String type, String name, String time) {
        this.type = type;
        this.name = name;
        this.time = time;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getFirstContents() {
        return firstContents;
    }

    public void setFirstContents(String firstContents) {
        this.firstContents = firstContents;
    }

    public String getSecondContents() {
        return secondContents;
    }

    public void setSecondContents(String secondContents) {
        this.secondContents = secondContents;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum;
    }

    public String getVideoNum() {
        return videoNum;
    }

    public void setVideoNum(String videoNum) {
        this.videoNum = videoNum;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
