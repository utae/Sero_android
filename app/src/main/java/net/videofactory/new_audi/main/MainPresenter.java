package net.videofactory.new_audi.main;

import android.support.v4.view.ViewPager;

import net.videofactory.new_audi.footer.FooterFragment;
import net.videofactory.new_audi.video.OnVideoThumbnailClickListener;
import net.videofactory.new_audi.common.AudiGestureListener;
import net.videofactory.new_audi.custom_view.audi_layout.AudiLayout;
import net.videofactory.new_audi.video.VideoInfo;

/**
 * Created by Utae on 2015-10-24.
 */
public interface MainPresenter {

    void onRestartCalled();

    void onStopCalled();

    AudiLayout.OnPageChangeListener getAudiOnLayoutPageChangeListener();

    ViewPager.OnPageChangeListener getAudiOnVideoChangeListener();

    AudiGestureListener getAudiTapListener();

    void insertConnectLog();

    OnVideoThumbnailClickListener getAudiOnVideoThumbnailClickListener();

    FooterFragment.OnFooterLikeButtonClickListener getOnFooterLikeButtonClickListener();

    void goToVideo(VideoInfo videoInfo);

}
