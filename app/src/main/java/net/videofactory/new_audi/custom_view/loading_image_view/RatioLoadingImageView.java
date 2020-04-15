package net.videofactory.new_audi.custom_view.loading_image_view;

import android.content.Context;
import android.util.AttributeSet;

import com.etsy.android.grid.util.DynamicHeightImageView;

/**
 * Created by Utae on 2016-06-12.
 */
public class RatioLoadingImageView extends DynamicHeightImageView {

    private LoadingDrawable loadingDrawable;

    public RatioLoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioLoadingImageView(Context context) {
        super(context);
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
