package net.videofactory.new_audi.setting;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.videofactory.new_audi.R;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-08-31.
 */

public class TutorialImgFragment extends Fragment {

    private int position;

    @BindView(R.id.tutorialPageImageView) ImageView imageView;

    private View.OnClickListener onClickListener;

    public static TutorialImgFragment create(int position){
        TutorialImgFragment tutorialImgFragment = new TutorialImgFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        tutorialImgFragment.setArguments(args);
        return tutorialImgFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_tutorial, container, false);

        ButterKnife.bind(this, view);

        switch (position){
            case 0 :
                imageView.setImageResource(R.drawable.img_tutorial_07);
                break;

            case 1 :
                imageView.setImageResource(R.drawable.img_tutorial_08);
                break;
        }

        if(onClickListener != null){
            imageView.setOnClickListener(onClickListener);
        }

        return view;
    }

    public void setImgView(int resId){
        imageView.setImageDrawable(null);
        imageView.setImageResource(resId);
    }

    public void setOnclickListenerToImgView(View.OnClickListener onclickListener){
        this.onClickListener = onclickListener;
    }
}
