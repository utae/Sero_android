package net.videofactory.new_audi.main;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.BaseAdapter;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sprylab.android.widget.TextureVideoView;

import net.videofactory.new_audi.video.VideoInfo;

import java.util.ArrayList;

/**
 * Created by Utae on 2015-10-24.
 */
public interface MainView {

    void setPagerAdapter(PagerAdapter pagerAdapter);

    void setVideoPage(int position, boolean smoothScroll);

    void setVideoInfoToFooter(VideoInfo videoInfo);

    void setAudiLayoutPage(int pageIndex);

    void setDraggerAvailble();

    boolean isFooterShow();

    void footerLikeVideo();

    int getCurVideoPagePosition();

    void setFooterStateLoading();

    void mainViewPagerAddOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);
}
