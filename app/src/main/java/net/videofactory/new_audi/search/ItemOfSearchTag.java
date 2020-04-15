package net.videofactory.new_audi.search;


/**
 * Created by Utae on 2016-01-12.
 */
public class ItemOfSearchTag {

    private String tagName;

    private String thumbnailUrl;

    public ItemOfSearchTag(String tagName, String thumbnailUrl) {
        this.tagName = tagName;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTagName() {
        return tagName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
