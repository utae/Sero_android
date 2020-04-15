package net.videofactory.new_audi.custom_view.loading_image_view;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;

import net.videofactory.new_audi.async.ImagePickerTask;

import java.lang.ref.WeakReference;

/**
 * Created by Utae on 2016-05-24.
 */
public class LoadingDrawable {

    @IdRes private int resId;
    private WeakReference<ImagePickerTask> imagePickerTaskWeakReference;
    private WeakReference<ColorDrawable> colorDrawableWeakReference;

    public LoadingDrawable(int resId, ImagePickerTask imagePickerTask) {
        this.resId = resId;
        this.imagePickerTaskWeakReference = new WeakReference<>(imagePickerTask);
        this.colorDrawableWeakReference = null;
    }

    public LoadingDrawable(ColorDrawable colorDrawable, ImagePickerTask imagePickerTask){
        this.colorDrawableWeakReference = new WeakReference<>(colorDrawable);
        this.imagePickerTaskWeakReference = new WeakReference<>(imagePickerTask);
    }

    public int getResId() {
        return resId;
    }

    public ImagePickerTask getImagePickerTask() {
        return imagePickerTaskWeakReference.get();
    }

    public boolean isColorDrawable(){
        return colorDrawableWeakReference != null;
    }

    public ColorDrawable getColorDrawable(){
        return colorDrawableWeakReference.get();
    }
}
