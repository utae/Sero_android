package net.videofactory.new_audi.common;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Utae on 2015-10-21.
 */
public abstract class AudiGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    @Override
    public abstract boolean onSingleTapConfirmed(MotionEvent e);

    @Override
    public abstract boolean onDoubleTap(MotionEvent e);

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
