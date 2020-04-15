package net.videofactory.new_audi.common;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Utae on 2016-04-18.
 */
public class ItemOfCard implements Parcelable{

    private String type; //020-004 : Channel / 020-005 : Hashtag

    private String title;

    private ArrayList<String> imgUrls;

    private String profileUrl;

    private String userNum;

    public ItemOfCard(Parcel in){
        readFromParcel(in);
    }

    public ItemOfCard(String type, String title, ArrayList<String> imgUrls) {
        this.type = type;
        this.title = title;
        this.imgUrls = imgUrls;
        this.profileUrl = null;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public ArrayList<String> getImgUrls() {
        return imgUrls;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(title);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imgUrls", imgUrls);
        bundle.putString("profileUrl", profileUrl);
        bundle.putString("userNum", userNum);
        dest.writeBundle(bundle);
    }

    private void readFromParcel(Parcel in){
        type = in.readString();
        title = in.readString();
        Bundle bundle = new Bundle();
        bundle = in.readBundle();
        imgUrls = bundle.getStringArrayList("imgUrls");
        if(type.equals("020-004")){
            profileUrl = bundle.getString("profileUrl");
            userNum = bundle.getString("userNum");
        }
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new ItemOfCard(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new ItemOfCard[size];
        }
    };
}
