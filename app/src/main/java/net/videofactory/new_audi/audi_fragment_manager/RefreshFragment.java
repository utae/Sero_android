package net.videofactory.new_audi.audi_fragment_manager;

import androidx.fragment.app.Fragment;

/**
 * Created by Utae on 2016-09-07.
 */

public abstract class RefreshFragment extends Fragment{

    private boolean isRefreshing = false;

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    public abstract void refreshFragment();

}
