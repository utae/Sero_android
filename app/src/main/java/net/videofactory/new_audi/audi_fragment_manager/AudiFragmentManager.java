package net.videofactory.new_audi.audi_fragment_manager;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.videofactory.new_audi.alarm.AlarmPageFragment;
import net.videofactory.new_audi.common.ReorderedArraySet;
import net.videofactory.new_audi.home.HomePageFragment;
import net.videofactory.new_audi.mypage.MyPageFragment;
import net.videofactory.new_audi.search.SearchPageFragment;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Utae on 2016-03-22.
 */
public class AudiFragmentManager {

    private FragmentManager fragmentManager;

    private AudiFragmentListenerManager audiFragmentListenerManager;

    @IdRes private int containerId;

    private HashMap<Integer, Stack<String>> stackHashMap;

    private ReorderedArraySet<Integer> reorderedArraySet;

    public static final int HOME_MENU = 0;

    public static final int SEARCH_MENU = 1;

    public static final int ALARM_MENU = 2;

    public static final int MYPAGE_MENU = 3;

    public AudiFragmentManager(FragmentManager fragmentManager, @IdRes int containerId, AudiFragmentListenerManager audiFragmentListenerManager) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.audiFragmentListenerManager = audiFragmentListenerManager;
        stackHashMap = new HashMap<>();
        reorderedArraySet = new ReorderedArraySet<>();
    }

    public void selectMenu(int menu){
        if(reorderedArraySet.size() == 0 || menu != getCurMenu()){
            Fragment curFragment = null;
            if(reorderedArraySet.size() != 0){
                curFragment = getCurFragment(false);
            }

            reorderedArraySet.add(menu);

            if(!stackHashMap.containsKey(menu)){
                stackHashMap.put(menu, new Stack<String>());
                String tag = getNewTagName();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(curFragment != null){
                    fragmentTransaction.hide(curFragment);
                }
                switch (menu){
                    case HOME_MENU :
                        HomePageFragment homePageFragment = new HomePageFragment();
                        if(audiFragmentListenerManager.getOnHeaderItemClickListener() != null){
                            homePageFragment.setOnHeaderItemClickListener(audiFragmentListenerManager.getOnHeaderItemClickListener());
                        }
                        if(audiFragmentListenerManager.getOnCardClickListener() != null){
                            homePageFragment.setOnCardClickListener(audiFragmentListenerManager.getOnCardClickListener());
                        }
                        fragmentTransaction.add(containerId, homePageFragment, tag).commit();
                        break;

                    case SEARCH_MENU :
                        SearchPageFragment searchPageFragment = new SearchPageFragment();
                        if(audiFragmentListenerManager.getOnSearchActionListener() != null){
                            searchPageFragment.setOnSearchActionListener(audiFragmentListenerManager.getOnSearchActionListener());
                        }
                        if(audiFragmentListenerManager.getOnSearchPageItemClickListener() != null){
                            searchPageFragment.setOnSearchPageItemClickListener(audiFragmentListenerManager.getOnSearchPageItemClickListener());
                        }
                        fragmentTransaction.add(containerId, searchPageFragment, tag).commit();
                        break;

                    case ALARM_MENU :
                        AlarmPageFragment alarmPageFragment = new AlarmPageFragment();
                        if(audiFragmentListenerManager.getAlarmListListener() != null){
                            alarmPageFragment.setAlarmListListener(audiFragmentListenerManager.getAlarmListListener());
                        }
                        fragmentTransaction.add(containerId, alarmPageFragment, tag).commit();
                        break;

                    case MYPAGE_MENU :
                        MyPageFragment myPageFragment = new MyPageFragment();
                        if(audiFragmentListenerManager.getOnVideoThumbnailClickListener() != null){
                            myPageFragment.setOnVideoThumbnailClickListener(audiFragmentListenerManager.getOnVideoThumbnailClickListener());
                        }
                        if(audiFragmentListenerManager.getMyPageListener() != null){
                            myPageFragment.setMyPageListener(audiFragmentListenerManager.getMyPageListener());
                        }
                        fragmentTransaction.add(containerId, myPageFragment, tag).commit();
                        break;
                }
                stackHashMap.get(reorderedArraySet.getCurValue()).push(tag);
            }else{
                fragmentManager.beginTransaction().hide(curFragment).show(getCurFragmentByMenu(menu)).commit();
            }
        }else{
            if(getCurFragment(false) instanceof RefreshFragment){
                ((RefreshFragment)getCurFragment(false)).refreshFragment();
            }
        }
    }

    public void addNewFragment(Fragment newFragment){
        String tag = getNewTagName();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(getCurFragment(false)).add(containerId, newFragment, tag).commitAllowingStateLoss();
        stackHashMap.get(reorderedArraySet.getCurValue()).push(tag);
//        fragmentManager.executePendingTransactions();
    }

    private String getNewTagName(){
        return reorderedArraySet.getCurValue() + "-" + stackHashMap.get(reorderedArraySet.getCurValue()).size();
    }

    public boolean isLastFragment(){
        return reorderedArraySet.size() == 1 && stackHashMap.get(reorderedArraySet.getCurValue()).size() == 1;
    }

    public void onBackPress(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(stackHashMap.get(reorderedArraySet.getCurValue()).size() == 1){
            fragmentTransaction.hide(getCurFragment(false));
            reorderedArraySet.removeCurValue();
            //TODO selected menu 바꿔줘야함 눌린효과
        }else{
            fragmentTransaction.remove(getCurFragment(true));
        }
        fragmentTransaction.show(getCurFragment(false));
        fragmentTransaction.commit();
    }

    public Fragment getCurFragment(boolean pop){
        if(pop){
            return fragmentManager.findFragmentByTag(stackHashMap.get(reorderedArraySet.getCurValue()).pop());
        }else{
            return fragmentManager.findFragmentByTag(stackHashMap.get(reorderedArraySet.getCurValue()).peek());
        }
    }

    public Fragment getCurFragmentByMenu(int menu){
        return fragmentManager.findFragmentByTag(stackHashMap.get(menu).peek());
    }

    public int getCurMenu(){
        return reorderedArraySet.getCurValue();
    }
}
