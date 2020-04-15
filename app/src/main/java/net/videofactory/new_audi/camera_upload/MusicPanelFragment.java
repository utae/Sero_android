package net.videofactory.new_audi.camera_upload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.videofactory.new_audi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-12-03.
 */
public class MusicPanelFragment extends Fragment{

    @Bind(R.id.musicSelectTextView) TextView musicSelectTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_panel, container, false);
        ButterKnife.bind(this, view);
        musicSelectTextView.setSelected(true);
        return view;
    }
}
