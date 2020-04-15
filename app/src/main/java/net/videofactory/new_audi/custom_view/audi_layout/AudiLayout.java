package net.videofactory.new_audi.custom_view.audi_layout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Scroller;

import net.videofactory.new_audi.common.Utilities;

/**
 * Created by Utae on 2016-03-12.
 */
public class AudiLayout extends LinearLayout {
    // 드래그 속도와 방향을 판단하는 클래스
    private VelocityTracker mVelocityTracker = null;

    // 화면 전환을 위한 드래그 속도의 최소값 pixel/s (100 정도으로 속도로 이동하면 화면전환으로 인식)
    private static final int SNAP_VELOCITY = 100;

    /* 화면에 대한 터치이벤트가 화면전환을 위한 터치인가? 현 화면의 위젯동작을 위한
        터치인가? 구분하는 값 (누른상태에서 10px 이동하면 화면 이동으로 인식) */
    private int mTouchSlop = 50;

    private PointF mLastPoint = null; // 마지막 터치 지점을 저장하는 클래스

    /* 화면 자동 전황을 위한 핵심 클래스 ( 화면 드래그후 손을 뗏을때
        화면 전환이나 원래 화면으로 자동으로 스크롤 되는 동작을 구현하는 클래스) */
    private Scroller mScroller = null;
    private int mCurPage = 0; // 현재 화면 페이지

    private int mCurTouchState = 0; // 현재 터치의 상태
    private static final int TOUCH_STATE_NORMAL = 0; // 정지상태
    private static final int TOUCH_STATE_TRANSLATING = 1; // 화면전환중
    private static final int TOUCH_STATE_RESIZING = 2; // 크기조정중


    private int mCurFooterState = 0; // 현재 레이아웃의 상태
    private static final int FOOTER_STATE_HIDE = 0;
    private static final int FOOTER_STATE_SHOW = 1;

    private boolean enableTranslating = true;
    private boolean enableResizing = true;

    private LayoutParams layoutParams;

    private ValueAnimator valueAnimator;

    private OnPageChangeListener onPageChangeListener;

    public AudiLayout(Context context) {
        super(context);
        init();
    }

    public AudiLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudiLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLastPoint = new PointF();
        valueAnimator = new ValueAnimator();
        mScroller = new Scroller(getContext()); // 스크롤러 클래스 생성
        mScroller.setFriction(0);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                layoutParams = (LayoutParams) getChildAt(1).getLayoutParams();
                layoutParams.height = getMeasuredHeight();
                getChildAt(1).setLayoutParams(layoutParams);
                return false;
            }
        });
    }

    public int getCurrentPage() {
        return mCurPage;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public int getChildHeightSum(int startIndex, int endIndex){
        if(startIndex < 0 || endIndex >= getChildCount()){
            return 0;
        }
        int heightSum = 0;
        for(int i = startIndex; i <= endIndex; i++){
            heightSum += getChildAt(i).getMeasuredHeight();
        }
        return heightSum;
    }

    public int getChildHeight(int index){
        return getChildAt(index).getMeasuredHeight();
    }

    public boolean isFooterHide(){
        return getChildHeight(1) == getMeasuredHeight();
    }

    public boolean isFooterShow(){
        return getChildHeightSum(1, 2) == getMeasuredHeight();
    }

    public void setFooterVisible(boolean visible){
        if(visible){
            valueAnimator = ValueAnimator.ofInt(getChildHeight(1), getMeasuredHeight() - getChildHeight(2));
        }else{
            valueAnimator = ValueAnimator.ofInt(getChildHeight(1), getMeasuredHeight());
        }

        valueAnimator.setDuration(getChildHeight(2)/2);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.height = (int) animation.getAnimatedValue();
                getChildAt(1).setLayoutParams(layoutParams);
                if (isFooterHide() || isFooterShow()) {
                    mCurFooterState = isFooterHide() ? FOOTER_STATE_HIDE : FOOTER_STATE_SHOW;
                    mCurTouchState = TOUCH_STATE_NORMAL; // 터치상태는 일반으로 변경
                }
            }
        });

        valueAnimator.start();
    }

    public void setEnableTranslating(boolean enableTranslating) {
        this.enableTranslating = enableTranslating;
    }

    public void setEnableResizing(boolean enableResizing) {
        this.enableResizing = enableResizing;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();

        mVelocityTracker.addMovement(event);

        int y = (int) (event.getY() - mLastPoint.y);

        if(mCurTouchState == TOUCH_STATE_NORMAL){
            if(Math.abs(y) > mTouchSlop){
                if((mCurPage == 0 && y < 0) || (mCurPage == 1 && y > 0 && isFooterHide())){
                    mCurTouchState = TOUCH_STATE_TRANSLATING;
                }else if((mCurPage == 1 && y < 0 && isFooterHide()) || isFooterShow()){
                    mCurTouchState = TOUCH_STATE_RESIZING;
                }else if(!(isFooterHide() || isFooterShow())){
                    mCurTouchState = TOUCH_STATE_RESIZING;
                }
            }
        }

        if(enableTranslating && mCurTouchState == TOUCH_STATE_TRANSLATING){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 현재 화면이 자동 스크롤 중이라면 (ACTION_UP 의 mScroller 부분 참조)
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation(); // 자동스크롤 중지하고 터치 지점에 멈춰서잇을것
                    }
                    mLastPoint.set(event.getX(), event.getY()); // 터치지점 저장
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    // 이전 터치지점과 현재 터치지점의 차이를 구해서 화면 스크롤 하는데 이용

                    //overscroll을 막기위한 if문
                    if(getScrollY() - y < 0){
                        scrollTo(0, 0); // 첫페이지라면 더이상 위로 올라가지 않음
                    }else if(getScrollY() - y > getChildHeight(0)){
                        scrollTo(0, getChildHeight(0)); // 마지막페이지라면 더이상 아래로 내려가지 않음
                    }else{
                        scrollBy(0, -y); // 차이만큼 화면 스크롤
                    }

                    invalidate(); // 다시 그리기
                    mLastPoint.set(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_UP:
                    // pixel/ms 단위로 드래그 속도를 구할것인가 지정 (1밀리초로 지정)
                    // onInterceptTouchEvent 메서드에서 터치지점을 저장해둔 것을 토대로 한다.
                    mVelocityTracker.computeCurrentVelocity(100);
                    int v = (int) mVelocityTracker.getYVelocity(); // y 축 이동 속도를 구함

                    int gap = getScrollY() - mCurPage * getMeasuredHeight(); // 드래그 이동 거리 체크
                    int nextPage = mCurPage;

                    // 드래그 속도가 SNAP_VELOCITY 보다 높거니 화면 반이상 드래그 했으면
                    // 화면전환 할것이라고 nextPage 변수를 통해 저장.
                    if ((v > SNAP_VELOCITY || -gap > getMeasuredHeight() / 2) && mCurPage > 0) {
                        nextPage--;
                    } else if ((v < -SNAP_VELOCITY || gap > getMeasuredHeight() / 2) && mCurPage < 1) {
                        nextPage++;
                    }

                    int move;
                    if (mCurPage != nextPage) { // 화면 전환 스크롤 계산
                        // 현재 스크롤 지점에서 화면전환을 위해 이동해야하는 지점과의 거리 계산
                        move = getMeasuredHeight() * nextPage - getScrollY();
                    } else { // 원래 화면 복귀 스크롤 계산
                        // 화면 전환 하지 않을것이며 원래 페이지로 돌아가기 위한 이동해야하는 거리 계산
                        move = getHeight() * mCurPage - getScrollY();
                    }

                    // 핵심!! 현재 스크롤 지점과 이동하고자 하는 최종 목표 스크롤 지점을 설정하는 메서드
                    // 현재 지점에서 목표 지점까지 스크롤로 이동하기 위한 중간값들을 자동으로 구해준다.
                    // 마지막 인자는 목표 지점까지 스크롤 되는 시간을 지정하는 것. 밀리세컨드 단위이다.
                    // 마지막 인자의 시간동안 중간 스크롤 값들을 얻어 화면에 계속 스크롤을 해준다.
                    // 그러면 스크롤 애니메이션이 되는것처럼 보인다. (computeScroll() 참조)
                    mScroller.startScroll(0, getScrollY(), 0, move, Math.abs(move)/2);

                    invalidate();

                    // 터치가 끝났으니 velocityTracker 삭제
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                    break;
            }
        }else if(enableResizing && mCurTouchState == TOUCH_STATE_RESIZING){
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN :
                    // 현재 화면이 자동 스크롤 중이라면 (ACTION_UP 의 mScroller 부분 참조)
                    if(valueAnimator.isRunning()){
                        valueAnimator.cancel();
                    }
                    mLastPoint.set(event.getX(), event.getY()); // 터치지점 저장
                    break;

                case MotionEvent.ACTION_MOVE :
                    // 이전 터치지점과 현재 터치지점의 차이를 구해서 화면 스크롤 하는데 이용
                    //overscroll을 막기위한 if문
                    if(getChildHeight(1) + y > getMeasuredHeight()){ //최대높이인경우
                        layoutParams.height = getMeasuredHeight();
                    }else if(getChildHeightSum(1,2) + y < getMeasuredHeight()){ //최소높이인경우
                        layoutParams.height = getMeasuredHeight() - getChildHeight(2);
                    }else{
                        layoutParams.height += y; // 차이만큼 높이 조절
                    }

                    getChildAt(1).setLayoutParams(layoutParams);

                    mLastPoint.set(event.getX(), event.getY());

                    break;

                case MotionEvent.ACTION_UP:

                    //이미 최대높이이거나 최소높이인 경우
                    if(isFooterShow() || isFooterHide()){
                        mCurFooterState = isFooterHide() ? FOOTER_STATE_HIDE : FOOTER_STATE_SHOW;
                        mCurTouchState = TOUCH_STATE_NORMAL; // 터치상태는 일반으로 변경
                        if(onPageChangeListener != null){
                            onPageChangeListener.onFooterStateChange(mCurFooterState == FOOTER_STATE_SHOW);
                        }
                        break;
                    }

                    // pixel/ms 단위로 드래그 속도를 구할것인가 지정 (1밀리초로 지정)
                    // onInterceptTouchEvent 메서드에서 터치지점을 저장해둔 것을 토대로 한다.
                    mVelocityTracker.computeCurrentVelocity(100);
                    int v = (int) mVelocityTracker.getYVelocity(); // y 축 이동 속도를 구함

                    int showHeightOfFooter = getMeasuredHeight() - getChildHeight(1);
                    int hideHeightOfFooter = getChildHeight(2) - showHeightOfFooter; // 화면에 나오지 않은 Footer의 높이

                    int nextState = -1;

                    // 드래그 속도가 SNAP_VELOCITY 보다 높거니 화면 반이상 드래그 했으면
                    // 화면전환 할것이라고 nextPage 변수를 통해 저장.
                    if ((v > SNAP_VELOCITY && mCurFooterState == FOOTER_STATE_SHOW) || showHeightOfFooter <= hideHeightOfFooter ) {
                        valueAnimator = ValueAnimator.ofInt(getChildHeight(1), getMeasuredHeight());
                        nextState = FOOTER_STATE_HIDE;
                    } else if ((v < -SNAP_VELOCITY && mCurFooterState == FOOTER_STATE_HIDE) || showHeightOfFooter > hideHeightOfFooter ) {
                        valueAnimator = ValueAnimator.ofInt(getChildHeight(1), getMeasuredHeight() - getChildHeight(2));
                        nextState = FOOTER_STATE_SHOW;
                    }

                    // 애니메이션 시간계산

                    if(nextState != -1){
                        if (nextState == FOOTER_STATE_HIDE) {
                            valueAnimator.setDuration(showHeightOfFooter);
                        }else if(nextState == FOOTER_STATE_SHOW) {
                            valueAnimator.setDuration(hideHeightOfFooter);
                        }
                    }

                    //update listener를 통해 animation을 화면에 적용
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            layoutParams.height = (int) animation.getAnimatedValue();
                            getChildAt(1).setLayoutParams(layoutParams);
                        }
                    });

                    valueAnimator.addListener(new AudiLayoutValueAnimatorListener(nextState){
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            switch (nextState){
                                case FOOTER_STATE_HIDE :
                                    layoutParams.height = getMeasuredHeight();
                                    break;

                                case FOOTER_STATE_SHOW :
                                    layoutParams.height = getMeasuredHeight() - getChildHeight(2);
                                    break;
                            }
                            getChildAt(1).setLayoutParams(layoutParams);
                            mCurFooterState = nextState;
                            mCurTouchState = TOUCH_STATE_NORMAL; // 터치상태는 일반으로 변경
                            if(onPageChangeListener != null){
                                onPageChangeListener.onFooterStateChange(mCurFooterState == FOOTER_STATE_SHOW);
                            }
                        }
                    });

                    // 애니메이션실행
                    valueAnimator.start();

                    // 터치가 끝났으니 저장해두었던 터치 정보들 삭제하고
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                    break;
            }
        }

        return true;
    }

    // ViewGroup 의 childview 들에게 터치이벤트를 줄것인지 아니면 본인에게 터치이벤트를 줄것인지
    // 판단하는 콜백 메서드 ( 터치 이빈트 발생시 가장먼저 실행 됨 )
    // 리턴값으로 true 를 주게 되면 viewgroup의 onTouchEvent 메서드가 실행되고
    // false 를 주면 ViewGroup 의 onTouchEvent은 실행되지 않고 childview 에게
    // 터치 이벤틀르 넘겨주게 된다. 따라서, 화면 전환 할것인가? 차일드뷰의 버튼이나 여타 위젯을 컨트롤
    // 하는 동작인가? 를 구분하는 로직이 여기서 필요하다.
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mCurPage != 0 || mCurTouchState != TOUCH_STATE_NORMAL){
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Scroller가 현재 목표지점까지 스크롤 되었지는 판단하는 isFinished() 를 통해
                    // 화면이 자동 스크롤 되는 도중에 터치를 한것인지 아닌지를 확인하여,
                    // 자식에게 이벤트를 전달해 줄건지를 판단한다.
                    if (!mScroller.isFinished()) {
                        mCurTouchState = TOUCH_STATE_TRANSLATING;
                    } else if (valueAnimator.isRunning()) {
                        mCurTouchState = TOUCH_STATE_RESIZING;
                    } else {
                        mCurTouchState = TOUCH_STATE_NORMAL;
                    }
                    mLastPoint.set(ev.getX(), ev.getY()); // 터치 지점 저장
                    break;

                case MotionEvent.ACTION_MOVE:
                    // 자식뷰의 이벤트인가 아니면 화면전환 동작 이벤트를 판단하는 기준의 기본이 되는
                    // 드래그 이동 거리를 체크 계산한다.
                    int y = (int) (ev.getY() - mLastPoint.y);

                    // 만약 처음 터치지점에서 mTouchSlop 만큼 이동되면 화면전환을 위한 동작으로 판단
                    if (mCurTouchState == TOUCH_STATE_NORMAL) {
                        if (Math.abs(y) > mTouchSlop) {
                            if (enableTranslating && ((mCurPage == 0 && y < 0) || (mCurPage == 1 && y > 0 && isFooterHide()))) {
                                mCurTouchState = TOUCH_STATE_TRANSLATING;
                                invalidate();
                            } else if (enableResizing && ((mCurPage == 1 && y < 0 && isFooterHide()) || isFooterShow())) {
                                mCurTouchState = TOUCH_STATE_RESIZING;
                            }
                            mLastPoint.set(ev.getX(), ev.getY());
                        }
                    }
                    break;
            }
        }

        // 현재 상태가 드래그 중이라면 true를 리턴하여 onTouchEvent 가 실행됨
        return mCurTouchState != TOUCH_STATE_NORMAL;
    }

    // 완전 핵심!! 인 콜백 메서드. 스크롤 될때마다 무조건 계속 실행됨.
    @Override
    public void computeScroll() {
        super.computeScroll();
        // onTouchEvent 에서 지정된 mScroller 의 목표 스크롤 지점으로 스크롤하는데, 필요한 중간 좌표 값들을
        // 얻기 위한 메서드로서, 중간 좌표값을 얻을수 있으면 true 를 리턴
        if (mScroller.computeScrollOffset()) {
            // 값을 얻을수 있다면. getCurrX,getCurrY 을 통해 전달되는데,
            // 이는 목표 지점으로 스크롤하기 위한 중간 좌표값들을 Scroller 클래스가 자동으로 계산한 값이다.
            // scrollTo() 를 통해 화면을 중간 지점으로 스크롤 하고,
            // 앞서 말했듯 스크롤이 되면 자동으로 computeScroll() 메서드가 호출되기 때문에
            // 목표 스크롤 지점에 도착할떄까지 computeScroll() 메서드가 호출되고 스크롤 되고 호출되고 반복.
            // 따라서 화면에 스크롤 애니메이션을 구현된것처럼 보이게 됨.
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
            if(mScroller.getCurrY() == 0 || mScroller.getCurrY() == getChildHeight(0)){ //스크롤완료
                if(mScroller.getCurrY() == 0){
                    mCurPage = 0;
                }else{
                    mCurPage = 1;
                }
                mCurTouchState = TOUCH_STATE_NORMAL; // 터치상태는 일반으로 변경
                if(onPageChangeListener != null){
                    onPageChangeListener.onPageChange(mCurPage);
                }
            }
        }
    }

    public void setPage(int index){
//        mScroller.startScroll(0, getScrollY(), 0, getMeasuredHeight()*index, Math.abs(getMeasuredHeight()));

        if (mCurPage != index) {

            int move = getMeasuredHeight() * index - getScrollY();

            Utilities.logD("Test", "move : " + move);
            Utilities.logD("Test", "getScrollY : " + getScrollY());

            mScroller.startScroll(0, getScrollY(), 0, move, Math.abs(move)/2);

            invalidate();

            mCurPage = index;
        }

        // 핵심!! 현재 스크롤 지점과 이동하고자 하는 최종 목표 스크롤 지점을 설정하는 메서드
        // 현재 지점에서 목표 지점까지 스크롤로 이동하기 위한 중간값들을 자동으로 구해준다.
        // 마지막 인자는 목표 지점까지 스크롤 되는 시간을 지정하는 것. 밀리세컨드 단위이다.
        // 마지막 인자의 시간동안 중간 스크롤 값들을 얻어 화면에 계속 스크롤을 해준다.
        // 그러면 스크롤 애니메이션이 되는것처럼 보인다. (computeScroll() 참조)

    }

    public void startScroll(){
        mCurTouchState = TOUCH_STATE_TRANSLATING;
        mScroller.startScroll(0, getScrollY(), 0, 100, 100);
        invalidate();
    }

    public interface OnPageChangeListener{
//        void prePageChange(int nextPage);
        void onPageChange(int pageIndex);
        void onFooterStateChange(boolean showing);
    }

    public class AudiLayoutValueAnimatorListener implements Animator.AnimatorListener{

        public int nextState;

        public AudiLayoutValueAnimatorListener(int nextState) {
            this.nextState = nextState;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
