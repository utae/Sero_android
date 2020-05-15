package net.videofactory.new_audi.main;


import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.videofactory.new_audi.video.VideoInfo;

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
