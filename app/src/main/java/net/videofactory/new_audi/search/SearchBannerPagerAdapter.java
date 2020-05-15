package net.videofactory.new_audi.search;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
