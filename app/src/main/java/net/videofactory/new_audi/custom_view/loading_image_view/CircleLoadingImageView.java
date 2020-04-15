package net.videofactory.new_audi.custom_view.loading_image_view;

import android.content.Context;
import android.util.AttributeSet;

import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by Utae on 2016-07-25.
 */

public class CircleLoadingImageView extends CircularImageView {

    private LoadingDrawable loadingDrawable;

    public CircleLoadingImageView(Context context) {
        super(context);
    }

    public CircleLoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleLoadingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
