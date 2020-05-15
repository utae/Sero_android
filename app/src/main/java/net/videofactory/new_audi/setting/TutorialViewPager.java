package net.videofactory.new_audi.setting;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Utae on 2016-08-31.
 */

public class TutorialViewPager extends ViewPager {

    private boolean canSwipe;

    public TutorialViewPager(Context context) {
        super(context);
        canSwipe = true;
    }

    public TutorialViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        canSwipe = true;
    }

    public void setCanSwipe(boolean canSwipe) {
        this.canSwipe = canSwipe;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canSwipe && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSwipe && super.onInterceptTouchEvent(ev);
    }
}
