package net.videofactory.new_audi.camera_upload;

/**
 * Created by Utae on 2015-11-25.
 */
public class ItemOfGallery {

    private String data, height, width, rotation, duration;

    public ItemOfGallery(String data, String height, String width, String rotation, String duration) {
        this.data = data;
        this.height = height;
        this.width = width;
        this.rotation = rotation;
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public String getDuration() {
        return duration;
    }

    public float getRatio(){
        if(rotation == null){
            rotation = "0";
        }
        float ratio = 0;
        switch (rotation){
            case "0" :
            case "180" :
                ratio = Float.parseFloat(height) / Float.parseFloat(width);
                break;
            case "90" :
            case "270" :
                ratio = Float.parseFloat(width) / Float.parseFloat(height);
                break;
        }
        return ratio;
    }
}
