package net.videofactory.new_audi.custom_view.audi_layout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Utae on 2016-03-05.
 */
public class DragResizeView extends LinearLayout {

    // 드래그 속도와 방향을 판단하는 클래스
    private VelocityTracker mVelocityTracker = null;

    // 화면 전환을 위한 드래그 속도의 최소값 pixel/s (100 정도으로 속도로 이동하면 화면전환으로 인식)
    private static final int SNAP_VELOCITY = 100;

    /* 화면에 대한 터치이벤트가 화면전환을 위한 터치인가? 현 화면의 위젯동작을 위한
        터치인가? 구분하는 값 (누른상태에서 10px 이동하면 화면 이동으로 인식) */
    private int mTouchSlop = 10;

    private PointF mLastPoint = null; // 마지막 터치 지점을 저장하는 클래스

    private int mCurTouchState; // 현재 터치의 상태
    private static final int TOUCH_STATE_DRAGGING = 0; // 현재 스크롤 중이라는 상태
    private static final int TOUCH_STATE_NORMAL = 1; // 현재 스크롤 상태가 아님

    private int mCurFooterState = 0; // 현재 레이아웃의 상태
    private static final int FOOTER_STATE_HIDE = 0;
    private static final int FOOTER_STATE_SHOW = 1;

    private LayoutParams layoutParams;

    private ValueAnimator valueAnimator;

    private Toast mToast;

    public DragResizeView(Context context) {
        super(context);
        init();
    }

    public DragResizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragResizeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLastPoint = new PointF();
        valueAnimator = new ValueAnimator();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                layoutParams = (LayoutParams) getChildAt(0).getLayoutParams();
                layoutParams.height = getMeasuredHeight();
                getChildAt(0).setLayoutParams(layoutParams);
                return false;
            }
        });
    }

    public int getRealHeight(){
        int realHeight = 0;
        for(int i = 0; i < getChildCount(); i++){
            realHeight += getChildAt(i).getMeasuredHeight();
        }
        return realHeight;
    }

    public int getChildHeight(int index){
        return getChildAt(index).getMeasuredHeight();
    }

    public boolean isMaximumHeight(){
        return getChildHeight(0) == getMeasuredHeight();
    }

    public boolean isMinimumHeight(){
        return getRealHeight() == getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) (event.getY() - mLastPoint.y);

        if(!(y >= 0 && isMaximumHeight())){
            if (mVelocityTracker == null)
                mVelocityTracker = VelocityTracker.obtain();

            // 터치되는 모든 좌표들을 저장하여, 터치 드래그 속도를 판단하는 기초를 만듬
            mVelocityTracker.addMovement(event);

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    // 현재 화면이 자동 스크롤 중이라면 (ACTION_UP 의 mScroller 부분 참조)
                    if(valueAnimator.isRunning()){
                        valueAnimator.cancel();
                    }
                    mLastPoint.set(event.getX(), event.getY()); // 터치지점 저장
                    break;

                case MotionEvent.ACTION_MOVE:
                    // 이전 터치지점과 현재 터치지점의 차이를 구해서 화면 스크롤 하는데 이용
                    //overscroll을 막기위한 if문
                    if(!((y > 0 && isMaximumHeight()) || (y < 0 && isMinimumHeight()))){
                        if(getChildHeight(0) + y > getMeasuredHeight()){
                            layoutParams.height = getMeasuredHeight();
                        }else if(getRealHeight() + y < getMeasuredHeight()){
                            layoutParams.height = getMeasuredHeight() - getChildHeight(1);
                        }else{
                            layoutParams.height += y; // 차이만큼 높이 축소
                        }
                    }

                    getChildAt(0).setLayoutParams(layoutParams);

                    mLastPoint.set(event.getX(), event.getY());

                    break;

                case MotionEvent.ACTION_UP:

                    //이미 최대높이이거나 최소높이인 경우
                    if(isMaximumHeight() || isMinimumHeight()){
                        break;
                    }

                    // pixel/ms 단위로 드래그 속도를 구할것인가 지정 (1초로 지정)
                    // onInterceptTouchEvent 메서드에서 터치지점을 저장해둔 것을 토대로 한다.
                    mVelocityTracker.computeCurrentVelocity(1);
                    int v = (int) mVelocityTracker.getYVelocity(); // y 축 이동 속도를 구함

                    int showHeightOfFooter = getMeasuredHeight() - getChildHeight(0);
                    int hideHeightOfFooter = getChildHeight(1) - showHeightOfFooter; // 화면에 나오지 않은 Footer의 높이

                    int nextState = -1;

                    // 드래그 속도가 SNAP_VELOCITY 보다 높거니 화면 반이상 드래그 했으면
                    // 화면전환 할것이라고 nextPage 변수를 통해 저장.
                    if ((v > SNAP_VELOCITY && mCurFooterState == FOOTER_STATE_SHOW) || showHeightOfFooter <= hideHeightOfFooter ) {
                        valueAnimator = ValueAnimator.ofInt(getChildHeight(0), getMeasuredHeight());
                        nextState = FOOTER_STATE_HIDE;
                    } else if ((v < -SNAP_VELOCITY && mCurFooterState == FOOTER_STATE_HIDE) || showHeightOfFooter > hideHeightOfFooter ) {
                        valueAnimator = ValueAnimator.ofInt(getChildHeight(0), getMeasuredHeight() - getChildHeight(1));
                        nextState = FOOTER_STATE_SHOW;
                    }

                    // 애니메이션 시간계산

                    if(nextState != -1){
                        if (nextState == FOOTER_STATE_HIDE) {
                            valueAnimator.setDuration(showHeightOfFooter);
                        } else if(nextState == FOOTER_STATE_SHOW) {
                            valueAnimator.setDuration(hideHeightOfFooter);
                        }
                    }

                    //update listener를 통해 animation을 화면에 적용
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            layoutParams.height = (int) animation.getAnimatedValue();
                            getChildAt(0).setLayoutParams(layoutParams);
                        }
                    });

                    // 애니메이션실행
                    valueAnimator.start();

                    if (mToast != null) {
                        mToast.setText("state : " + nextState);
                    } else {
                        mToast = Toast.makeText(getContext(), "state : " + nextState, Toast.LENGTH_SHORT);
                    }
                    mToast.show();
//                invalidate();
                    mCurFooterState = nextState;

                    // 터치가 끝났으니 저장해두었던 터치 정보들 삭제하고
                    // 터치상태는 일반으로 변경
                    mCurTouchState = TOUCH_STATE_NORMAL;
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                    break;
            }

            return true;
        }

        return false;
    }

    // ViewGroup 의 childview 들에게 터치이벤트를 줄것인지 아니면 본인에게 터치이벤트를 줄것인지
    // 판단하는 콜백 메서드 ( 터치 이빈트 발생시 가장먼저 실행 됨 )
    // 리턴값으로 true 를 주게 되면 viewgroup의 onTouchEvent 메서드가 실행되고
    // false 를 주면 ViewGroup 의 onTouchEvent은 실행되지 않고 childview 에게
    // 터치 이벤틀르 넘겨주게 된다. 따라서, 화면 전환 할것인가? 차일드뷰의 버튼이나 여타 위젯을 컨트롤
    // 하는 동작인가? 를 구분하는 로직이 여기서 필요하다.
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getY() < getChildHeight(0)){
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Scroller가 현재 목표지점까지 스크롤 되었지는 판단하는 isFinished() 를 통해
                    // 화면이 자동 스크롤 되는 도중에 터치를 한것인지 아닌지를 확인하여,
                    // 자식에게 이벤트를 전달해 줄건지를 판단한다.
                    mCurTouchState = isMaximumHeight() || isMinimumHeight() ? TOUCH_STATE_NORMAL : TOUCH_STATE_DRAGGING;
                    mLastPoint.set(ev.getX(), ev.getY()); // 터치 지점 저장
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 자식뷰의 이벤트인가 아니면 화면전환 동작 이벤트를 판단하는 기준의 기본이 되는
                    // 드래그 이동 거리를 체크 계산한다.
                    int y = (int) (ev.getY() - mLastPoint.y);
                    // 만약 처음 터치지점에서 mTouchSlop 만큼 이동되면 화면전환을 위한 동작으로 판단

                    if(!(y > 0 && isMaximumHeight())){
                        if (Math.abs(y) > mTouchSlop) {
                            mCurTouchState = TOUCH_STATE_DRAGGING; // 현재 상태 스크롤 상태로 전환
                            mLastPoint.set(ev.getX(), ev.getY());
                        }
                    }
                    break;
            }
        }

        // 현재 상태가 스크롤 중이라면 true를 리턴하여 viewgroup의 onTouchEvent 가 실행됨
        return mCurTouchState == TOUCH_STATE_DRAGGING;
    }
}
