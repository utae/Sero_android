package net.videofactory.new_audi.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.custom_view.audi_layout.AudiLayout;
import net.videofactory.new_audi.main.MainActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-07-24.
 */

public class TutorialActivity extends AppCompatActivity {

    private int curPage;

    @Bind(R.id.tutorialAudiLayout) AudiLayout audiLayout;
    @Bind(R.id.tutorialTopImageView) ImageView topImageView;
    @Bind(R.id.tutorialViewPager) TutorialViewPager tutorialViewPager;
    @Bind(R.id.tutorialBottomImageView) ImageView bottomImageView;

    private ArrayList<Integer> resIdList = new ArrayList<>();
    private TutorialPagerAdapter pagerAdapter;

    private boolean isSignup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        ButterKnife.bind(this);

        isSignup = getIntent().getBooleanExtra("isSignup", false);

        resIdList.add(R.drawable.img_tutorial_01);

        resIdList.add(R.drawable.img_tutorial_02);

        resIdList.add(R.drawable.img_tutorial_03);

        resIdList.add(R.drawable.img_tutorial_04);

        resIdList.add(R.drawable.img_tutorial_05);

        resIdList.add(R.drawable.img_tutorial_06);

        resIdList.add(R.drawable.img_tutorial_07);

        init();
    }

    private void init(){
        pagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager(), this);

        tutorialViewPager.setAdapter(pagerAdapter);

        curPage = 1;

        topImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curPage < 6){
                    topImageView.setImageDrawable(null);
                    topImageView.setImageResource(resIdList.get(curPage));
                }else if(curPage == 6){
                    audiLayout.setPage(1);
                }else if(curPage == 7){
                    topImageView.setImageDrawable(null);
                    topImageView.setImageResource(R.drawable.img_tutorial_12);
                }else if(curPage == 8){
                    setResult(RESULT_OK);
                    if(isSignup){
                        Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        finish();
                    }
                }
                curPage++;
            }
        });

        tutorialViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    tutorialViewPager.setCanSwipe(false);
                    audiLayout.setEnableResizing(true);
                }
            }
        });

        audiLayout.setOnPageChangeListener(new AudiLayout.OnPageChangeListener() {
            @Override
            public void onPageChange(int pageIndex) {
                if(pageIndex == 0){
                    audiLayout.setEnableTranslating(false);
                }else if(pageIndex == 1){
                    if(pagerAdapter.getItemPosition(tutorialViewPager.getCurrentItem()) == 0){
                        audiLayout.setEnableTranslating(false);
                        audiLayout.setEnableResizing(false);
                        tutorialViewPager.setCanSwipe(true);
                    }else{
                        audiLayout.setEnableTranslating(true);
                    }
                }
            }

            @Override
            public void onFooterStateChange(boolean showing) {
                if(showing){
                    pagerAdapter.setImgViewInFragment(1, R.drawable.img_tutorial_09);
                    topImageView.setImageDrawable(null);
                    topImageView.setImageResource(R.drawable.img_tutorial_11);
                }else{
                    pagerAdapter.setImgViewInFragment(1, R.drawable.img_tutorial_10);
                    audiLayout.setEnableResizing(false);
                    audiLayout.setEnableTranslating(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isSignup){
            Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            super.onBackPressed();
        }
    }
}
