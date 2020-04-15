package net.videofactory.new_audi.camera_upload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.videofactory.new_audi.R;

/**
 * Created by Utae on 2015-12-03.
 */
public class FilterPanelFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_panel, container, false);
        return view;
    }
}
