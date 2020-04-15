package net.videofactory.new_audi.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import net.videofactory.new_audi.alarm.AlarmListFragment;
import net.videofactory.new_audi.camera_upload.CameraActivity;
import net.videofactory.new_audi.channel_tag.ChannelPageFragment;
import net.videofactory.new_audi.audi_fragment_manager.AudiFragmentListenerManager;
import net.videofactory.new_audi.audi_fragment_manager.AudiFragmentManager;
import net.videofactory.new_audi.channel_tag.FollowerFragment;
import net.videofactory.new_audi.channel_tag.FollowingFragment;
import net.videofactory.new_audi.channel_tag.ItemOfFollowListChannel;
import net.videofactory.new_audi.channel_tag.ItemOfFollowListTag;
import net.videofactory.new_audi.channel_tag.OnFollowListItemClickListener;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.footer.ShareBottomSheet;
import net.videofactory.new_audi.login.Splash;
import net.videofactory.new_audi.setting.ChangePasswordFragment;
import net.videofactory.new_audi.mypage.EditProfileFragment;
import net.videofactory.new_audi.mypage.MyPageFragment;
import net.videofactory.new_audi.common.BackPressCloseSystem;
import net.videofactory.new_audi.common.ItemOfCard;
import net.videofactory.new_audi.custom_view.audi_layout.AudiLayout;
import net.videofactory.new_audi.footer.FooterFragment;
import net.videofactory.new_audi.R;
import net.videofactory.new_audi.home.HomeCardVideoListFragment;
import net.videofactory.new_audi.home.HomePageFragment;
import net.videofactory.new_audi.home.OnCardClickListener;
import net.videofactory.new_audi.menu.MainMenuBarFragment;
import net.videofactory.new_audi.setting.SettingFragment;
import net.videofactory.new_audi.search.SearchPageFragment;
import net.videofactory.new_audi.search.SearchResultFragment;
import net.videofactory.new_audi.channel_tag.TagPageFragment;
import net.videofactory.new_audi.setting.TutorialActivity;
import net.videofactory.new_audi.video.VideoInfo;

import java.io.File;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements MainView {

    @Bind(R.id.videoViewPager) ViewPager videoViewPager;

    @Bind(R.id.videoViewPagerContainer) FrameLayout videoViewPagerContainer;

    @Bind(R.id.audiLayoutView) AudiLayout audiLayoutView;

    @Bind(R.id.menuPageContainer) LinearLayout menuPageContainer;

    @Bind(R.id.mainDragger) FrameLayout dragger;

    private MainPresenter mainPresenter;

    private GestureDetectorCompat gestureDetectorCompat;

    private FragmentManager fragmentManager;

    private FooterFragment footerFragment;

    private MainMenuBarFragment mainMenuBarFragment;

    private BackPressCloseSystem backPressCloseSystem;

    private AudiFragmentManager audiFragmentManager;

    private AudiFragmentListenerManager audiFragmentListenerManager;

    private static final int AUDI_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 0;
    private static final int AUDI_PICK_IMG_FROM_GALLERY = 0;
    private static final int AUDI_PERMISSIONS_REQUEST_CAMERA = 1;

    //Facebook
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private ShareVideo shareVideo;
    private ShareVideoContent shareVideoContent;

    @Override
    protected void onStop() {
        super.onStop();
        mainPresenter.onStopCalled();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if(audiLayoutView.getCurrentPage() == 1){
//            mainPresenter.onRestartCalled();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        init();
    }

    private void init(){
        mainPresenter = new MainPresenterImpl(this,this);

        mainPresenter.insertConnectLog();

        fragmentManager = getSupportFragmentManager();

        audiFragmentListenerManager = new AudiFragmentListenerManager();

        audiFragmentManager = new AudiFragmentManager(getSupportFragmentManager(), R.id.menuContentContainer, audiFragmentListenerManager);

        mainMenuBarFragment = new MainMenuBarFragment();

        backPressCloseSystem = new BackPressCloseSystem(this);

        initListener();

        audiFragmentManager.selectMenu(AudiFragmentManager.HOME_MENU);

        footerFragment = new FooterFragment();

        footerFragment.setOnFooterLikeButtonClickListener(mainPresenter.getOnFooterLikeButtonClickListener());

        footerFragment.setOnProfileImgClickListener(new AudiOnProfileImgClickListener());

        footerFragment.setShareBottomSheetListener(new AudiShareBottomSheetListener());

        fragmentManager.beginTransaction().replace(R.id.menuBarContainer, mainMenuBarFragment).replace(R.id.footerContainer, footerFragment).commit();
    }

    private void initListener(){
        mainMenuBarFragment.setMainMenuListener(new OnMenuClickListener());

        audiLayoutView.setOnPageChangeListener(mainPresenter.getAudiOnLayoutPageChangeListener());

        gestureDetectorCompat = new GestureDetectorCompat(this, mainPresenter.getAudiTapListener());

        videoViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetectorCompat.onTouchEvent(event);
                //TODO 뷰페이저 동작 이상없는지 확인 이상있는경우 return false 해줄것.
            }
        });

        audiFragmentListenerManager.setOnHeaderItemClickListener(new AudiOnHomeHeaderClickListener());

        audiFragmentListenerManager.setOnCardClickListener(new AudiOnCardClickListener());

        audiFragmentListenerManager.setOnSearchActionListener(new AudiOnSearchActionListener());

        audiFragmentListenerManager.setOnSearchPageItemClickListener(new AudiOnSearchPageItemClickListener());

        audiFragmentListenerManager.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());

        audiFragmentListenerManager.setMyPageListener(new AudiMyPageListener());

        audiFragmentListenerManager.setAlarmListListener(new AudiAlarmListListener());
    }

    @Override
    public void onBackPressed() {
        switch (audiLayoutView.getCurrentPage()){
            case 1 :
                if(audiLayoutView.isFooterShow()){
                    audiLayoutView.setFooterVisible(false);
                }else{
                    audiLayoutView.setPage(0);
                }
                break;

            default :
                if(audiFragmentManager.isLastFragment()){
                    backPressCloseSystem.onBackPressed();
                }else{
                    audiFragmentManager.onBackPress();
                }
                break;
        }
    }

    @Override
    public void setPagerAdapter(PagerAdapter pagerAdapter) {
        videoViewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void setVideoPage(final int position, final boolean smoothScroll) {
        try {
            videoViewPager.setCurrentItem(position, smoothScroll);
        }catch (Exception e){
            videoViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setVideoPage(position, smoothScroll);
                }
            }, 500);
        }
    }

    @Override
    public void setVideoInfoToFooter(VideoInfo videoInfo) {
        footerFragment.setVideoInfo(videoInfo);
        Utilities.logD("videoNum", videoInfo.getVideoNum());
    }

    @Override
    public void setFooterStateLoading() {
        footerFragment.setFooterStateLoading();
    }

    @Override
    public void setAudiLayoutPage(int pageIndex) {
        audiLayoutView.setPage(pageIndex);
    }

    @Override
    public void setDraggerAvailble() {
        dragger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    audiLayoutView.startScroll();
                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }
                return false;
            }
        });
    }

    @Override
    public boolean isFooterShow() {
        return audiLayoutView.isFooterShow();
    }

    @Override
    public void footerLikeVideo() {
        if(footerFragment != null){
            footerFragment.likeVideo();
        }
    }

    @Override
    public int getCurVideoPagePosition() {
        return videoViewPager.getCurrentItem();
    }

    @Override
    public void mainViewPagerAddOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        videoViewPager.clearOnPageChangeListeners();
        videoViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private void onUploadMenuClick(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                //TODO 권한요청 toast 띄우기

            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        AUDI_PERMISSIONS_REQUEST_CAMERA);
            }
        }else{
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        }
    }

    //Listeners

    private class OnMenuClickListener implements MainMenuBarFragment.OnMenuClickListener{

        @Override
        public void onMenuClick(@IdRes int id) {
            switch (id){
                case R.id.menuHome :
                    audiFragmentManager.selectMenu(AudiFragmentManager.HOME_MENU);
                    break;
                case R.id.menuSearch :
                    audiFragmentManager.selectMenu(AudiFragmentManager.SEARCH_MENU);
                    break;
                case R.id.menuAlarm :
                    audiFragmentManager.selectMenu(AudiFragmentManager.ALARM_MENU);
                    break;
                case R.id.menuMypage :
                    audiFragmentManager.selectMenu(AudiFragmentManager.MYPAGE_MENU);
                    break;
                case R.id.menuUpload :
                    onUploadMenuClick();
                    break;

            }
        }
    }

    private class AudiOnProfileImgClickListener implements OnProfileImgClickListener{

        @Override
        public void onProfileImgClick(String channelNum) {

            if(audiLayoutView.getCurrentPage() != 0){
                audiLayoutView.setPage(0);
            }

            if(channelNum.equals(UserInfo.getUserNum())){
                MyPageFragment myPageFragment = new MyPageFragment();
                myPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
                myPageFragment.setMyPageListener(new AudiMyPageListener());
                audiFragmentManager.addNewFragment(myPageFragment);
            }else{
                ChannelPageFragment channelPageFragment = ChannelPageFragment.create(channelNum);

                channelPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
                channelPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());

                audiFragmentManager.addNewFragment(channelPageFragment);
            }

        }
    }

    private class AudiOnHomeHeaderClickListener implements HomePageFragment.OnHeaderItemClickListener{

        @Override
        public void onHeaderItemClick(String type) {
            HomeCardVideoListFragment homeCardVideoListFragment = null;
            switch (type){
                case "020-001" :
                    homeCardVideoListFragment = HomeCardVideoListFragment.create("Following", type);
                    break;

                case "020-002" :
                    homeCardVideoListFragment = HomeCardVideoListFragment.create("Favorite", type);
                    break;

                case "020-003" :
                    homeCardVideoListFragment = HomeCardVideoListFragment.create("Trending", type);
                    break;
            }

            if(homeCardVideoListFragment != null){
                homeCardVideoListFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
                homeCardVideoListFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
                audiFragmentManager.addNewFragment(homeCardVideoListFragment);
            }
        }
    }

    private class AudiOnSearchActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    if(!"".equals(v.getText().toString().trim())) {
                        SearchResultFragment searchResultFragment = SearchResultFragment.create(v.getText().toString().trim());
                        searchResultFragment.setOnCardClickListener(new AudiOnCardClickListener());
                        searchResultFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
                        v.setText("");
                        if(v instanceof EditText){
                            Utilities.hideKeyboard(MainActivity.this, (EditText) v);
                        }
                        audiFragmentManager.addNewFragment(searchResultFragment);
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

    private class AudiOnSearchPageItemClickListener implements SearchPageFragment.OnSearchPageItemClickListener{

        @Override
        public void onChannelItemClick(String userNum) {
            ChannelPageFragment channelPageFragment = ChannelPageFragment.create(userNum);
            channelPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
            channelPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            audiFragmentManager.addNewFragment(channelPageFragment);
        }

        @Override
        public void onTagItemClick(String tag) {
            TagPageFragment tagPageFragment = TagPageFragment.create(tag);
            tagPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
            tagPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            audiFragmentManager.addNewFragment(tagPageFragment);
        }
    }

    private class AudiOnCardClickListener implements OnCardClickListener{

        @Override
        public void onCardClick(ItemOfCard card) {
            switch (card.getType()){
                case "020-004" :
                    ChannelPageFragment channelPageFragment = ChannelPageFragment.create(card.getUserNum());
                    channelPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
                    channelPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
                    audiFragmentManager.addNewFragment(channelPageFragment);
                    break;

                case "020-005" :
                    TagPageFragment tagPageFragment = TagPageFragment.create(card.getTitle());
                    tagPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
                    tagPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
                    audiFragmentManager.addNewFragment(tagPageFragment);
                    break;
            }
        }
    }

    private class AudiMyPageListener implements MyPageFragment.MyPageListener{

        @Override
        public void onFollowerCLick() {
            FollowerFragment followerFragment = new FollowerFragment();
            followerFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            followerFragment.setOnFollowListItemClickListener(new AudiOnFollowListItemClickListener());
            audiFragmentManager.addNewFragment(followerFragment);
        }

        @Override
        public void onFollowingClick() {
            FollowingFragment followingFragment = new FollowingFragment();
            followingFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            followingFragment.setOnFollowListItemClickListener(new AudiOnFollowListItemClickListener());
            audiFragmentManager.addNewFragment(followingFragment);
        }

        @Override
        public void onEditProfileClick() {
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            editProfileFragment.setEditProfileListener(new AudiEditProfileListener());
            audiFragmentManager.addNewFragment(editProfileFragment);
        }

        @Override
        public void onSettingClick() {
            SettingFragment settingFragment = new SettingFragment();
            settingFragment.setSettingListener(new AudiSettingListener());
            audiFragmentManager.addNewFragment(settingFragment);
        }
    }

    private class AudiOnFollowListItemClickListener implements OnFollowListItemClickListener{

        @Override
        public void onFollowListChannelItemClickListener(ItemOfFollowListChannel itemOfFollowListChannel) {
            ChannelPageFragment channelPageFragment = ChannelPageFragment.create(itemOfFollowListChannel.getUserNum());
            channelPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            channelPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
            audiFragmentManager.addNewFragment(channelPageFragment);
        }

        @Override
        public void onFollowListTagItemClickListener(ItemOfFollowListTag itemOfFollowListTag) {
            TagPageFragment tagPageFragment = TagPageFragment.create(itemOfFollowListTag.getTagName());
            tagPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            tagPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
            audiFragmentManager.addNewFragment(tagPageFragment);
        }
    }

    private class AudiOnBackButtonClickListener implements OnBackButtonClickListener{

        @Override
        public void onBackButtonClick() {
            onBackPressed();
        }
    }

    private class AudiSettingListener implements SettingFragment.SettingListener{

        @Override
        public void signOut() {
            SharedPreferences prefs = getSharedPreferences("loginInfo", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(MainActivity.this, Splash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void changePassword() {
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
            changePasswordFragment.setChangePasswordListener(new AudiChangePWListener());
            changePasswordFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            audiFragmentManager.addNewFragment(changePasswordFragment);
        }

        @Override
        public void tutorial() {
            Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(intent);
        }
    }

    private class AudiChangePWListener implements ChangePasswordFragment.ChangePasswordListener{

        @Override
        public void onChangePassword() {
            onBackPressed();
        }
    }

    private class AudiEditProfileListener implements EditProfileFragment.EditProfileListener{

        @Override
        public void onEditProfileImgClick() {
            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                    //TODO 권한요청 toast 띄우기

                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            AUDI_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                }
            }else{
                pickImgFromGallery();
            }
        }

        @Override
        public void onEditProfileFinish() {
            onBackPressed();
            if(audiFragmentManager.getCurFragment(false) instanceof MyPageFragment){
                MyPageFragment myPageFragment = (MyPageFragment) audiFragmentManager.getCurFragment(false);
                myPageFragment.refreshFragment();
            }
        }
    }

    private class AudiShareBottomSheetListener implements ShareBottomSheet.ShareBottomSheetListener{

        @Override
        public void shareFacebook(final String videoUrl) {
//            if(ShareDialog.canShow(ShareVideoContent.class)){
//                shareVideo = new ShareVideo.Builder().setLocalUrl(Uri.parse(videoUrl)).build();
//                shareVideoContent = new ShareVideoContent.Builder().setVideo(shareVideo).setContentTitle("Title").setContentDescription("Description").build();
//                shareDialog.show(shareVideoContent);
//            }else{
//                Utilities.logD("Fb", "can't show share dialog");
//            }
            callbackManager = CallbackManager.Factory.create();
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = null;
                    try {
//                        request = GraphRequest.newPostRequest(
//                                loginResult.getAccessToken(),
//                                "/me/videos",
//                                new JSONObject("{\"file_url\":\"https://d2w1dgw1e9awhj.cloudfront.net/video/main.mp4\"}"),
//                                new GraphRequest.Callback() {
//                                    @Override
//                                    public void onCompleted(GraphResponse response) {
//                                        Utilities.logD("Fb", "fb sharing succeed : " + response.toString());
//                                    }
//                                });
                        Bundle parm = new Bundle();
                        parm.putString("file_url", videoUrl);
                        request = new GraphRequest(
                                loginResult.getAccessToken(),
                                "/me/videos",
                                parm,
                                HttpMethod.POST,
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        Utilities.logD("Fb", "fb sharing succeed : " + response.toString());
                                    }
                                }
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (request != null) {
                        request.executeAsync();
                    }
                }

                @Override
                public void onCancel() {
                    Utilities.logD("Fb", "fb sharing is canceled");
                }

                @Override
                public void onError(FacebookException e) {
                    if(e instanceof FacebookAuthorizationException){
                        if(AccessToken.getCurrentAccessToken() != null){
                            LoginManager.getInstance().logOut();
                        }
                    }
                    Utilities.logD("Fb", "fb err : " + e.toString());
                }
            });

            loginManager.logInWithPublishPermissions(MainActivity.this, Arrays.asList("publish_actions"));
//            Bundle params = new Bundle();
//            params.putString("other", "http://samples.ogp.me/467235199955838");
//
//            GraphRequest request = new GraphRequest(
//                    AccessToken.getCurrentAccessToken(),
//                    "me/sero_app:post",
//                    params,
//                    HttpMethod.POST,
//                    new GraphRequest.Callback() {
//                        @Override
//                        public void onCompleted(GraphResponse response) {
//
//                        }
//                    }
//            );
//            request.executeAndWait();
        }

        @Override
        public void shareInstagram() {

        }

        @Override
        public void shareUrl() {

        }
    }

    private class AudiAlarmListListener implements AlarmListFragment.AlarmListListener{

        @Override
        public void goToChannel(String channelNum) {
            ChannelPageFragment channelPageFragment = ChannelPageFragment.create(channelNum);
            channelPageFragment.setOnBackButtonClickListener(new AudiOnBackButtonClickListener());
            channelPageFragment.setOnVideoThumbnailClickListener(mainPresenter.getAudiOnVideoThumbnailClickListener());
            audiFragmentManager.addNewFragment(channelPageFragment);
        }

        @Override
        public void goToVideo(VideoInfo videoInfo) {
            mainPresenter.goToVideo(videoInfo);
            audiLayoutView.setPage(1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case AUDI_PERMISSIONS_REQUEST_EXTERNAL_STORAGE :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImgFromGallery();
                }else{
                    //TODO PERMISSION DENIED
                }
                break;

            case AUDI_PERMISSIONS_REQUEST_CAMERA :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                }else{
                    //TODO PERMISSION DENIED
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(callbackManager != null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if(resultCode == RESULT_OK){
            if(requestCode == AUDI_PICK_IMG_FROM_GALLERY){
                //TODO imageview에 사진박기
                if(audiFragmentManager.getCurFragment(false) instanceof EditProfileFragment){
                    EditProfileFragment editProfileFragment = (EditProfileFragment)audiFragmentManager.getCurFragment(false);
                    editProfileFragment.setProfileImg(this.getExternalFilesDir(null) + "/profileImg/temp.jpg");
                }
            }
        }
    }

    private void pickImgFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, AUDI_PICK_IMG_FROM_GALLERY);
    }

    private Uri getTempUri(){
        return Uri.fromFile(getTempImgFile());
    }

    private File getTempImgFile(){
        File dir = new File(this.getExternalFilesDir(null) + "/" + "profileImg");
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir, "temp.jpg");
        try{
            file.createNewFile();
        }catch(Exception e){
            Utilities.logD("Test", "getTempImgFile Error : " + e.toString());
        }
        return file;
    }
}
