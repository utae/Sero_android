package net.videofactory.new_audi.footer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Utae on 2015-10-29.
 */
public class ItemOfComment implements Parcelable{

    private String commentNum, userNum, nickName, name, time, comment, likeCount, imgUrl;

    private boolean isLike;

    public ItemOfComment() {
    }

    public ItemOfComment(Parcel in){
        readFromParcel(in);
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentNum);
        dest.writeString(userNum);
        dest.writeString(nickName);
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(comment);
        dest.writeString(likeCount);
        if(isLike()){
            dest.writeString("Y");
        }else{
            dest.writeString("N");
        }
        dest.writeString(imgUrl);
    }

    private void readFromParcel(Parcel in){
        commentNum = in.readString();
        userNum = in.readString();
        nickName = in.readString();
        name = in.readString();
        time = in.readString();
        comment = in.readString();
        likeCount = in.readString();
        isLike = "Y".equals(in.readString());
        imgUrl = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new ItemOfComment(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new ItemOfComment[size];
        }
    };
}
