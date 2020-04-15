package net.videofactory.new_audi.camera_upload;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.etsy.android.grid.util.DynamicHeightImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Utae on 2015-11-25.
 */
public class DeviceVideoThumbnailPickerTask extends AsyncTask<String, Void, Bitmap> {

    private String videoPath;
    private final WeakReference<DynamicHeightImageView> thumbnailViewReference;

    public DeviceVideoThumbnailPickerTask(DynamicHeightImageView thumbnailView) {
        thumbnailViewReference = new WeakReference<>(thumbnailView);
    }

    public String getVideoPath() {
        return videoPath;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        videoPath = params[0];
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        return thumbnail;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()){
            bitmap = null;
        }

        if(bitmap != null){
            DynamicHeightImageView thumbnailView = thumbnailViewReference.get();
            DeviceVideoThumbnailPickerTask thumbnailPickerTask = GalleryAdapter.getThumbnailPickerTask(thumbnailView);
            if(this == thumbnailPickerTask){
                thumbnailView.setImageBitmap(bitmap);
            }
        }
    }
}
