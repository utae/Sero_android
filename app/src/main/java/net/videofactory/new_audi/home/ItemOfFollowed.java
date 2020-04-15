package net.videofactory.new_audi.home;

/**
 * Created by Utae on 2016-06-16.
 */
public class ItemOfFollowed {

    private String name;

    private String profileUrl = null;

    private String userNum = null;

    public ItemOfFollowed(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }
}
