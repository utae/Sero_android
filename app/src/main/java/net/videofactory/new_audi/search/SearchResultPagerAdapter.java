package net.videofactory.new_audi.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.videofactory.new_audi.alarm.AlarmListFragment;
import net.videofactory.new_audi.home.OnCardClickListener;

import java.util.ArrayList;

/**
 * Created by Utae on 2016-05-27.
 */
public class SearchResultPagerAdapter extends FragmentPagerAdapter {

    private String word;
    private ArrayList<SearchResultListFragment> searchResultListFragmentList;
    private OnCardClickListener onCardClickListener;

    public SearchResultPagerAdapter(FragmentManager fm, String word) {
        super(fm);
        this.word = word;
        this.searchResultListFragmentList = new ArrayList<>();
    }

    public void setWord(String word) {
        this.word = word;
        if(searchResultListFragmentList.size() == 2){
            for(SearchResultListFragment searchResultListFragment : searchResultListFragmentList){
                searchResultListFragment.setWord(word);
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        SearchResultListFragment searchResultListFragment;
        if(searchResultListFragmentList.size() > position){
            searchResultListFragment = searchResultListFragmentList.get(position);
        }else{
            searchResultListFragment = SearchResultListFragment.create(word, position);
            if(onCardClickListener != null){
                searchResultListFragment.setOnCardClickListener(onCardClickListener);
            }
            searchResultListFragmentList.add(searchResultListFragment);
        }

        return searchResultListFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Channels";

            case 1 :
                return "Tags";
        }
        return super.getPageTitle(position);
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }
}
