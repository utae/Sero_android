package net.videofactory.new_audi.search;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Utae on 2016-05-24.
 */
public class SearchBannerPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<ItemOfBanner> bannerUrlList;

    public SearchBannerPagerAdapter(FragmentManager fm, ArrayList<ItemOfBanner> bannerUrlList) {
        super(fm);
        this.bannerUrlList = bannerUrlList;
    }

    @Override
    public Fragment getItem(int position) {
        return SearchBannerFragment.create(bannerUrlList.get(position).getImgUrl(), bannerUrlList.get(position).getBannerLink());
    }

    @Override
    public int getCount() {
        return bannerUrlList.size();
    }
}
