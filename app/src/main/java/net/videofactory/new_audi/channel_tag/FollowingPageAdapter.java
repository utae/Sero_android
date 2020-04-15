package net.videofactory.new_audi.channel_tag;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.videofactory.new_audi.alarm.AlarmListFragment;

import java.util.ArrayList;

/**
 * Created by Utae on 2016-06-30.
 */
public class FollowingPageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentList;

    private OnFollowListItemClickListener onFollowListItemClickListener;

    public FollowingPageAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(fragmentList.size() > position){
            fragment = fragmentList.get(position);
        }else{
            switch (position){
                case 0 :
                    fragment = FollowChannelListFragment.create(1);
                    if(onFollowListItemClickListener != null){
                        ((FollowChannelListFragment)fragment).setOnFollowListItemClickListener(onFollowListItemClickListener);
                    }
                    break;

                case 1 :
                    fragment = new FollowTagListFragment();
                    if(onFollowListItemClickListener != null){
                        ((FollowTagListFragment)fragment).setOnFollowListItemClickListener(onFollowListItemClickListener);
                    }
                    break;
            }
            fragmentList.add(fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Channel";

            case 1 :
                return "Tag";
        }
        return super.getPageTitle(position);
    }

    public void setOnFollowListItemClickListener(OnFollowListItemClickListener onFollowListItemClickListener) {
        this.onFollowListItemClickListener = onFollowListItemClickListener;
    }
}
