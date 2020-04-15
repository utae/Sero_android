package net.videofactory.new_audi.channel_tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.main.OnBackButtonClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-29.
 */
public class FollowerFragment extends RefreshFragment {

    @Bind(R.id.followerBackButton) ImageButton backButton;

    private OnBackButtonClickListener onBackButtonClickListener;
    private OnFollowListItemClickListener onFollowListItemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);

        ButterKnife.bind(this, view);

        initChannelListFragment();

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

    private void initChannelListFragment(){
        FollowChannelListFragment followChannelListFragment = FollowChannelListFragment.create(0);
        if(onFollowListItemClickListener != null){
            followChannelListFragment.setOnFollowListItemClickListener(onFollowListItemClickListener);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.followerListContainer, followChannelListFragment, "followChannelListFragment").commit();
    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.onBackButtonClickListener = onBackButtonClickListener;
    }

    public void setOnFollowListItemClickListener(OnFollowListItemClickListener onFollowListItemClickListener) {
        this.onFollowListItemClickListener = onFollowListItemClickListener;
    }


    @Override
    public void refreshFragment() {
        if(getChildFragmentManager().findFragmentByTag("followChannelListFragment") instanceof RefreshFragment){
            ((RefreshFragment) getChildFragmentManager().findFragmentByTag("followChannelListFragment")).refreshFragment();
        }
    }
}
