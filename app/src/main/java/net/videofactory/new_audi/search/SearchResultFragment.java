package net.videofactory.new_audi.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.audi_fragment_manager.RefreshFragment;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.home.OnCardClickListener;
import net.videofactory.new_audi.main.OnBackButtonClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-12.
 */
public class SearchResultFragment extends RefreshFragment {

    private String word;
    private SearchResultPagerAdapter searchResultPagerAdapter;
    private OnCardClickListener onCardClickListener;
    private OnBackButtonClickListener onBackButtonClickListener;

    @Bind(R.id.searchResultEditText) EditText searchEditText;
    @Bind(R.id.searchResultSearchButton) ImageButton searchButton;
    @Bind(R.id.searchResultViewPager) ViewPager resultViewPager;
    @Bind(R.id.searchResultViewPagerTab) SmartTabLayout resultViewPagerTab;
    @Bind(R.id.searchResultBackButton) ImageButton backButton;

    public static SearchResultFragment create(String word){
        SearchResultFragment searchResultFragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString("word", word);
        searchResultFragment.setArguments(args);
        return searchResultFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        word = getArguments().getString("word");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        ButterKnife.bind(this, view);

        searchEditText.setText(word);

        searchEditText.setOnEditorActionListener(new AudiOnSearchActionListener());

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
        searchResultPagerAdapter = new SearchResultPagerAdapter(getChildFragmentManager(), word);
        if(onCardClickListener != null){
            searchResultPagerAdapter.setOnCardClickListener(onCardClickListener);
        }
        resultViewPager.setAdapter(searchResultPagerAdapter);
        resultViewPagerTab.setViewPager(resultViewPager);

//        resultViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.onBackButtonClickListener = onBackButtonClickListener;
    }

    @Override
    public void refreshFragment() {
        if(searchResultPagerAdapter != null){
            if(searchResultPagerAdapter.getItem(resultViewPager.getCurrentItem()) instanceof RefreshFragment){
                ((RefreshFragment) searchResultPagerAdapter.getItem(resultViewPager.getCurrentItem())).refreshFragment();
            }
        }
    }

    private class AudiOnSearchActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    if(!"".equals(v.getText().toString().trim())) {
                        searchResultPagerAdapter.setWord(v.getText().toString().trim());
                        if(v instanceof EditText){
                            Utilities.hideKeyboard(getContext(), (EditText) v);
                        }
                        break;
                    }else{
                        return false;
                    }
                default:
                    return false;
            }
            return true;
        }
    }
}
