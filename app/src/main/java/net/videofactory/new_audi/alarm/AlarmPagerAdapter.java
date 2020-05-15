package net.videofactory.new_audi.alarm;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Utae on 2016-05-27.
 */
public class AlarmPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<AlarmListFragment> alarmListFragmentList;
    private AlarmListFragment.AlarmListListener alarmListListener;

    public AlarmPagerAdapter(FragmentManager fm) {
        super(fm);
        this.alarmListFragmentList = new ArrayList<>();
    }

    public void setAlarmListListener(AlarmListFragment.AlarmListListener alarmListListener) {
        this.alarmListListener = alarmListListener;
    }

    @Override
    public Fragment getItem(int position) {
        AlarmListFragment alarmListFragment;
        if(alarmListFragmentList.size() > position){
            alarmListFragment = alarmListFragmentList.get(position);
        }else{
            alarmListFragment = AlarmListFragment.create(position);
            if(alarmListListener != null){
                alarmListFragment.setAlarmListListener(alarmListListener);
            }
            alarmListFragmentList.add(alarmListFragment);
        }
        return alarmListFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "You";

            case 1 :
                return "Following";
        }
        return super.getPageTitle(position);
    }
}
