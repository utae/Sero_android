package net.videofactory.new_audi.setting;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import net.videofactory.new_audi.R;

import java.util.ArrayList;

/**
 * Created by Utae on 2016-08-31.
 */

public class TutorialPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private ArrayList<TutorialImgFragment> imgFragmentList = new ArrayList<>();

    public TutorialPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        TutorialImgFragment tutorialImgFragment;

        if(imgFragmentList.size() > position){
            tutorialImgFragment = imgFragmentList.get(position);
        }else{
            tutorialImgFragment = TutorialImgFragment.create(position);
            imgFragmentList.add(tutorialImgFragment);
        }
        return tutorialImgFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void setImgViewInFragment(int position, int resId){
        imgFragmentList.get(position).setImgView(resId);
    }
}
