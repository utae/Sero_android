package net.videofactory.new_audi.video;

/**
 * Created by Utae on 2016-06-08.
 */
public class VideoInfo {

    private String imgUrl;

    private String videoNum;

    private String videoUrl;

    private boolean isMine;

    public VideoInfo(String imgUrl, String videoNum, String videoUrl) {
        this.imgUrl = imgUrl;
        this.videoNum = videoNum;
        this.videoUrl = videoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getVideoNum() {
        return videoNum;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}
