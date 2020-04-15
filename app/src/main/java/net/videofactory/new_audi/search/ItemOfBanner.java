package net.videofactory.new_audi.search;

/**
 * Created by Utae on 2016-09-02.
 */

public class ItemOfBanner {

    private String imgUrl;
    private String bannerLink;

    public ItemOfBanner(String imgUrl, String bannerLink) {
        this.imgUrl = imgUrl;
        this.bannerLink = bannerLink;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBannerLink() {
        return bannerLink;
    }

    public void setBannerLink(String bannerLink) {
        this.bannerLink = bannerLink;
    }
}
