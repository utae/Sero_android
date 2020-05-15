package net.videofactory.new_audi.channel_tag;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.main.OnBackButtonClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-30.
 */
public class FollowingFragment extends RefreshFragment {

    private FollowingPageAdapter followingPageAdapter;

    @BindView(R.id.followingViewPager) ViewPager viewPager;
    @BindView(R.id.followingViewPagerTab) SmartTabLayout viewPagerTab;
    @BindView(R.id.followingBackButton) ImageButton backButton;

    private OnBackButtonClickListener onBackButtonClickListener;
    private OnFollowListItemClickListener onFollowListItemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        ButterKnife.bind(this, view);

        initPager();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackButtonClickListener != null) {
                    onBackButtonClickListener.onBackButtonClick();
                }
            }
        });

        return view;
    }

    private void initPager(){
        followingPageAdapter = new FollowingPageAdapter(getChildFragmentManager());

        if(onFollowListItemClickListener != null){
            followingPageAdapter.setOnFollowListItemClickListener(onFollowListItemClickListener);
        }

        viewPager.setAdapter(followingPageAdapter);
        viewPagerTab.setViewPager(viewPager);
    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.onBackButtonClickListener = onBackButtonClickListener;
    }

    public void setOnFollowListItemClickListener(OnFollowListItemClickListener onFollowListItemClickListener) {
        this.onFollowListItemClickListener = onFollowListItemClickListener;
    }

    @Override
    public void refreshFragment() {
        if(followingPageAdapter != null){
            if(followingPageAdapter.getItem(viewPager.getCurrentItem()) instanceof RefreshFragment){
                ((RefreshFragment) followingPageAdapter.getItem(viewPager.getCurrentItem())).refreshFragment();
            }
        }
    }
}
