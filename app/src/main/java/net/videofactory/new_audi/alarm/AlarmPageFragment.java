package net.videofactory.new_audi.alarm;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-05-26.
 */
public class AlarmPageFragment extends RefreshFragment {

    private AlarmPagerAdapter alarmPagerAdapter;

    @BindView(R.id.alarmViewPager) ViewPager viewPager;
    @BindView(R.id.alarmViewPagerTab) SmartTabLayout viewPagerTab;

    private AlarmListFragment.AlarmListListener alarmListListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm_page, container, false);

        ButterKnife.bind(this, view);

        initPager();

        return view;
    }

    private void initPager(){
        alarmPagerAdapter = new AlarmPagerAdapter(getChildFragmentManager());

        if(alarmListListener != null){
            alarmPagerAdapter.setAlarmListListener(alarmListListener);
        }

        viewPager.setAdapter(alarmPagerAdapter);
        viewPagerTab.setViewPager(viewPager);
    }


    @Override
    public void refreshFragment() {
        if(alarmPagerAdapter != null){
            if(alarmPagerAdapter.getItem(viewPager.getCurrentItem()) instanceof RefreshFragment){
                ((RefreshFragment) alarmPagerAdapter.getItem(viewPager.getCurrentItem())).refreshFragment();
            }
        }
    }

    public void setAlarmListListener(AlarmListFragment.AlarmListListener alarmListListener) {
        this.alarmListListener = alarmListListener;
    }
}
