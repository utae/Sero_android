package net.videofactory.new_audi.video;

/**
 * Created by Utae on 2016-06-21.
 */
public class VideoDetailInfo {

    private String videoNum;
    private String videoIntro;
    private String likeCount;
    private String uploaderNum;
    private String uploaderNickName;
    private String uploaderName;
    private String uploaderProfileUrl;
    private String commentCount;
    private String uploadTime;
    private String viewCount;
    private boolean isLike;
    private boolean isReport;

    public String getVideoNum() {
        return videoNum;
    }

    public String getUploaderNum() {
        return uploaderNum;
    }

    public void setUploaderNum(String uploaderNum) {
        this.uploaderNum = uploaderNum;
    }

    public void setVideoNum(String videoNum) {
        this.videoNum = videoNum;
    }

    public String getVideoIntro() {
        return videoIntro;
    }

    public void setVideoIntro(String videoIntro) {
        this.videoIntro = videoIntro;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getUploaderNickName() {
        return uploaderNickName;
    }

    public void setUploaderNickName(String uploaderNickName) {
        this.uploaderNickName = uploaderNickName;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderProfileUrl() {
        return uploaderProfileUrl;
    }

    public void setUploaderProfileUrl(String uploaderProfileUrl) {
        this.uploaderProfileUrl = uploaderProfileUrl;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public void switchIsLike(){
        this.isLike = !this.isLike;
    }

    public boolean isReport() {
        return isReport;
    }

    public void setIsReport(boolean isReport) {
        this.isReport = isReport;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }
}
