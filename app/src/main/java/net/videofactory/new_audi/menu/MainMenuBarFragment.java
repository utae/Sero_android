package net.videofactory.new_audi.menu;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.videofactory.new_audi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-08.
 */
public class MainMenuBarFragment extends Fragment {

    private OnMenuClickListener onMenuClickListener;

    private ImageView selectedMenu;

    private OnClickListener onClickListener;

    @BindView(R.id.menuHome) ImageView homeMenu;
    @BindView(R.id.menuSearch) ImageView searchMenu;
    @BindView(R.id.menuUpload) ImageView uploadMenu;
    @BindView(R.id.menuAlarm) ImageView alarmMenu;
    @BindView(R.id.menuMypage) ImageView mypageMenu;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu_bar, container, false);

        ButterKnife.bind(this, view);

        onClickListener = new OnClickListener();

        homeMenu.setSelected(true);

        selectedMenu = homeMenu;

        homeMenu.setOnClickListener(onClickListener);

        searchMenu.setOnClickListener(onClickListener);

        uploadMenu.setOnClickListener(onClickListener);

        alarmMenu.setOnClickListener(onClickListener);

        mypageMenu.setOnClickListener(onClickListener);

        return view;
    }

    private void selectMenu(ImageView selectedMenu){
        if(this.selectedMenu != selectedMenu){
            if(this.selectedMenu != null){
                this.selectedMenu.setSelected(false);
            }
            selectedMenu.setSelected(true);
            this.selectedMenu = selectedMenu;
        }
    }

    public void setMainMenuListener(OnMenuClickListener onMenuClickListener){
        this.onMenuClickListener = onMenuClickListener;
    }

    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v instanceof ImageView){
                selectMenu((ImageView)v);
                if(onMenuClickListener != null){
                    onMenuClickListener.onMenuClick(v.getId());
                }
            }
        }
    }

    public interface OnMenuClickListener{
        void onMenuClick(@IdRes int id);
    }
}
