package net.videofactory.new_audi.camera_upload;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.videofactory.new_audi.R;

/**
 * Created by Utae on 2015-12-03.
 */
public class ClippingPanelFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clipping_panel, container, false);
        return view;
    }
}
