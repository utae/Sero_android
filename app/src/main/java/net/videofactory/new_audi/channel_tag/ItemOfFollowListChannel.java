package net.videofactory.new_audi.channel_tag;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Utae on 2016-06-29.
 */
public class ItemOfFollowListChannel{

    private String userNum, profileUrl, nickName, name;
    private boolean isFollow;

    public ItemOfFollowListChannel() {
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        this.isFollow = follow;
    }
}
