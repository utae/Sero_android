package net.videofactory.new_audi.custom_view.loading_image_view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Utae on 2016-05-24.
 */
public class LoadingImageView extends ImageView {

    private LoadingDrawable loadingDrawable;

    public LoadingImageView(Context context) {
        super(context);
    }

    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImageLoadingDrawable(LoadingDrawable loadingDrawable){
        this.loadingDrawable = loadingDrawable;
        if(loadingDrawable.isColorDrawable()){
            setImageDrawable(loadingDrawable.getColorDrawable());
        }else{
            setImageResource(loadingDrawable.getResId());
        }
    }

    public LoadingDrawable getLoadingDrawable() {
        return loadingDrawable;
    }
}
