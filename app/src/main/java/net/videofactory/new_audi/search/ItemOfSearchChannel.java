package net.videofactory.new_audi.search;


/**
 * Created by Utae on 2016-01-12.
 */
public class ItemOfSearchChannel {

    private String channelId;

    private String profileUrl;

    public ItemOfSearchChannel(String channelId, String profileUrl) {
        this.channelId = channelId;
        this.profileUrl = profileUrl;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
