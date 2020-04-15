package net.videofactory.new_audi.video;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import net.videofactory.new_audi.common.Utilities;

import java.util.ArrayList;

/**
 * Created by Utae on 2015-11-02.
 */
public class VideoPagerAdapter extends FragmentStatePagerAdapter{

    private ArrayList<VideoInfo> videoInfoList;

    private SparseArray<VideoViewFragment> videoViewFragmentList;

    private VideoViewFragment.VideoViewFragmentListener videoViewFragmentListener;

    public VideoPagerAdapter(FragmentManager fm, ArrayList<VideoInfo> videoInfoList) {
        super(fm);
        this.videoInfoList = videoInfoList;
        this.videoViewFragmentList = new SparseArray<>();
    }

    public void setVideoInfoList(ArrayList<VideoInfo> videoInfoList) {
        if(!videoInfoList.isEmpty()){
            videoInfoList.clear();
        }
        this.videoInfoList = videoInfoList;
        if(videoViewFragmentList.size() > 0){
            videoViewFragmentList.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        VideoViewFragment videoViewFragment;
        if(videoViewFragmentList.get(position) != null){
            videoViewFragment = videoViewFragmentList.get(position);
        }else {
            videoViewFragment = VideoViewFragment.create(position, videoInfoList.get(position).getVideoUrl(), videoInfoList.get(position).getVideoNum());
            if (videoViewFragmentListener != null) {
                videoViewFragment.setVideoViewFragmentListener(videoViewFragmentListener);
            }
            videoViewFragmentList.append(position, videoViewFragment);
        }
        return videoViewFragment;
    }

    @Override
    public int getCount() {
        return videoInfoList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        videoViewFragmentList.remove(position);
    }

    public void setAudiOnCompletionListener(VideoViewFragment.VideoViewFragmentListener videoViewFragmentListener) {
        this.videoViewFragmentListener = videoViewFragmentListener;
    }
}
