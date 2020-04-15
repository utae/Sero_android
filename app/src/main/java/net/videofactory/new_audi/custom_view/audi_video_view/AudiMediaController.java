package net.videofactory.new_audi.custom_view.audi_video_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.video.VideoDetailInfo;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-12-04.
 */
public class AudiMediaController extends FrameLayout {

    private static final String TAG = "AudiMediaController";

    private MediaPlayerControl mPlayer;
    private Context mContext;
    private ViewGroup mAnchor;
    private View mRoot;
    private ViewHolder viewHolder;
    private boolean mShowing;
    private boolean mDragging;
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private Handler mHandler = new MessageHandler(this);

    public AudiMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = null;
        mContext = context;
    }

    public AudiMediaController(Context context) {
        super(context);
        mContext = context;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.audi_mediacontroller, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void initControllerView(View v) {
        viewHolder = new ViewHolder(v);

        if(viewHolder.startPauseButton != null) {
            viewHolder.startPauseButton.requestFocus();
            viewHolder.startPauseButton.setOnClickListener(mPauseListener);
        }

        viewHolder.seekBar.setOnSeekBarChangeListener(mSeekListener);
        viewHolder.seekBar.setMax(1000);

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    public void setVideoDetailInfo(VideoDetailInfo videoDetailInfo) {

        if(videoDetailInfo.getUploaderProfileUrl() == null || "".equals(videoDetailInfo.getUploaderProfileUrl())){
            viewHolder.profile.setImageResource(R.drawable.ic_profile_default);
        }else{
            if(Utilities.cancelPotentialTask(videoDetailInfo.getUploaderProfileUrl(), viewHolder.profile)){
                ImagePickerTask profileImgPickerTask = new ImagePickerTask(viewHolder.profile);
                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, profileImgPickerTask);
                viewHolder.profile.setImageLoadingDrawable(loadingDrawable);
                profileImgPickerTask.execute(videoDetailInfo.getUploaderProfileUrl());
            }
        }

        viewHolder.nickName.setText(videoDetailInfo.getUploaderNickName());

        if(videoDetailInfo.getVideoIntro() != null){
            viewHolder.intro.setText(videoDetailInfo.getVideoIntro());
        }

        viewHolder.date.setText(videoDetailInfo.getUploadTime());
        viewHolder.likeCount.setText(videoDetailInfo.getLikeCount());
        viewHolder.commentCount.setText(videoDetailInfo.getCommentCount());
        viewHolder.viewCount.setText(videoDetailInfo.getViewCount());

        show();
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
        if (mPlayer == null) {
            return;
        }

        try {
            if (viewHolder.startPauseButton != null && !mPlayer.canPause()) {
                viewHolder.startPauseButton.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();
            if (viewHolder.startPauseButton != null) {
                viewHolder.startPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );

            try {
                if(mAnchor.indexOfChild(this) == -1){
                    mAnchor.addView(this, tlp);
                }else{
                    if(getVisibility() != View.VISIBLE){
                        setVisibility(View.VISIBLE);
                    }
                }
            }catch (IllegalStateException e) {
                e.printStackTrace();
//                ViewGroup parent = (ViewGroup)this.getParent();
//                parent.removeView(this);
            }

            mShowing = true;
        }
        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            Message msg = mHandler.obtainMessage(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchor == null) {
            return;
        }
        if(mShowing){
            try {
                setVisibility(View.GONE);
//                mAnchor.removeView(this);
                mHandler.removeMessages(SHOW_PROGRESS);
            } catch (IllegalArgumentException ex) {
                Log.w("MediaController", "already removed");
            }
            mShowing = false;
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }

        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (viewHolder.seekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                viewHolder.seekBar.setProgress( (int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            viewHolder.seekBar.setSecondaryProgress(percent * 10);
        }

        if (viewHolder.curTime != null){
            viewHolder.curTime.setText(stringForTime(position));
        }

        if(viewHolder.endTime != null){
            viewHolder.endTime.setText(stringForTime(duration));
        }

        return position;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlayer == null) {
            return true;
        }

        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode ==  KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(sDefaultTimeout);
                if (viewHolder.startPauseButton != null) {
                    viewHolder.startPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    public void updatePausePlay() {
        if (mRoot == null || viewHolder.startPauseButton == null || mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            viewHolder.startPauseButton.setImageResource(R.drawable.btn_media_controller_pause);
        } else {
            viewHolder.startPauseButton.setImageResource(R.drawable.btn_media_controller_play);
        }
    }


    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }


    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (mPlayer == null) {
                return;
            }

            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo( (int) newposition);
            if (viewHolder.curTime != null)
                viewHolder.curTime.setText(stringForTime( (int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (viewHolder.startPauseButton != null) {
            viewHolder.startPauseButton.setEnabled(enabled);
        }
        if (viewHolder.seekBar != null) {
            viewHolder.seekBar.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AudiMediaController.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AudiMediaController.class.getName());
    }

    public interface MediaPlayerControl {
        void    start();
        void    pause();
        int     getDuration();
        int     getCurrentPosition();
        void    seekTo(int pos);
        boolean isPlaying();
        int     getBufferPercentage();
        boolean canPause();
        boolean canSeekBackward();
        boolean canSeekForward();
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<AudiMediaController> mView;

        MessageHandler(AudiMediaController view) {
            mView = new WeakReference<>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            AudiMediaController view = mView.get();
            if (view == null || view.mPlayer == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = view.setProgress();
                    if (!view.mDragging && view.mShowing && view.mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }

    static class ViewHolder{
        @Bind(R.id.mediaControllerProfile) CircleLoadingImageView profile;
        @Bind(R.id.mediaControllerIntro) TextView intro;
        @Bind(R.id.mediaControllerNickName) TextView nickName;
        @Bind(R.id.mediaControllerDate) TextView date;
        @Bind(R.id.mediaControllerLikeCount) TextView likeCount;
        @Bind(R.id.mediaControllerCommentCount) TextView commentCount;
        @Bind(R.id.mediaControllerStartPauseButton) ImageButton startPauseButton;
        @Bind(R.id.mediaControllerSeekBar) SeekBar seekBar;
        @Bind(R.id.mediaControllerListButton) ImageButton listButton;
        @Bind(R.id.mediaControllerCurTime) TextView curTime;
        @Bind(R.id.mediaControllerEndTime) TextView endTime;
        @Bind(R.id.mediaControllerViewCount) TextView viewCount;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
