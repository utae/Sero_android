package net.videofactory.new_audi.channel_tag;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Utae on 2016-06-30.
 */
public class ItemOfFollowListTag{

    private String tagName;
    private boolean isFollow;

    public ItemOfFollowListTag() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        this.isFollow = follow;
    }
}
