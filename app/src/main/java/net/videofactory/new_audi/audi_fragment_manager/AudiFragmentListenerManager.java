package net.videofactory.new_audi.audi_fragment_manager;

import android.widget.TextView;

import net.videofactory.new_audi.alarm.AlarmListFragment;
import net.videofactory.new_audi.mypage.MyPageFragment;
import net.videofactory.new_audi.home.HomePageFragment;
import net.videofactory.new_audi.home.OnCardClickListener;
import net.videofactory.new_audi.search.SearchPageFragment;
import net.videofactory.new_audi.video.OnVideoThumbnailClickListener;

/**
 * Created by Utae on 2016-06-09.
 */
public class AudiFragmentListenerManager {

    private HomePageFragment.OnHeaderItemClickListener onHeaderItemClickListener;
    private TextView.OnEditorActionListener onSearchActionListener;
    private SearchPageFragment.OnSearchPageItemClickListener onSearchPageItemClickListener;
    private OnCardClickListener onCardClickListener;
    private OnVideoThumbnailClickListener onVideoThumbnailClickListener;
    private MyPageFragment.MyPageListener myPageListener;
    private AlarmListFragment.AlarmListListener alarmListListener;

    public TextView.OnEditorActionListener getOnSearchActionListener() {
        return onSearchActionListener;
    }

    public void setOnSearchActionListener(TextView.OnEditorActionListener onSearchActionListener) {
        this.onSearchActionListener = onSearchActionListener;
    }

    public HomePageFragment.OnHeaderItemClickListener getOnHeaderItemClickListener() {
        return onHeaderItemClickListener;
    }

    public void setOnHeaderItemClickListener(HomePageFragment.OnHeaderItemClickListener onHeaderItemClickListener) {
        this.onHeaderItemClickListener = onHeaderItemClickListener;
    }

    public SearchPageFragment.OnSearchPageItemClickListener getOnSearchPageItemClickListener() {
        return onSearchPageItemClickListener;
    }

    public void setOnSearchPageItemClickListener(SearchPageFragment.OnSearchPageItemClickListener onSearchPageItemClickListener) {
        this.onSearchPageItemClickListener = onSearchPageItemClickListener;
    }

    public OnCardClickListener getOnCardClickListener() {
        return onCardClickListener;
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public OnVideoThumbnailClickListener getOnVideoThumbnailClickListener() {
        return onVideoThumbnailClickListener;
    }

    public void setOnVideoThumbnailClickListener(OnVideoThumbnailClickListener onVideoThumbnailClickListener) {
        this.onVideoThumbnailClickListener = onVideoThumbnailClickListener;
    }

    public MyPageFragment.MyPageListener getMyPageListener() {
        return myPageListener;
    }

    public void setMyPageListener(MyPageFragment.MyPageListener myPageListener) {
        this.myPageListener = myPageListener;
    }

    public AlarmListFragment.AlarmListListener getAlarmListListener() {
        return alarmListListener;
    }

    public void setAlarmListListener(AlarmListFragment.AlarmListListener alarmListListener) {
        this.alarmListListener = alarmListListener;
    }
}
