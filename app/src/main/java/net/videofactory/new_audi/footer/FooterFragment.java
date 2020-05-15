package net.videofactory.new_audi.footer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fasterxml.jackson.databind.JsonNode;
import com.nineoldandroids.animation.Animator;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.BackPressEditText;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.OnProfileImgClickListener;
import net.videofactory.new_audi.video.VideoInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static android.view.View.GONE;

/**
 * Created by Utae on 2015-10-29.
 */
public class FooterFragment extends Fragment {
    @BindView(R.id.footerEditText) BackPressEditText editText;
    @BindView(R.id.footerSendButton) ImageButton sendButton;
    @BindView(R.id.footerLikeCounter) TextView likeCounter;
    @BindView(R.id.footerLikeButton) ImageButton likeButton;
    @BindView(R.id.footerShareButton) ImageButton shareButton;
    @BindView(R.id.footerReportButton) ImageButton reportButton;
    @BindView(R.id.footerLikeInfoContainer) LinearLayout likeInfoContainer;
    @BindView(R.id.footerCommentListRefresher) PtrClassicFrameLayout commentListRefresher;
    @BindView(R.id.footerCommentList) ListView commentListView;
    @BindView(R.id.footerCommentListEmptyView) ImageView commentListEmptyView;
    @BindView(R.id.footerCommentListContainer) FrameLayout commentListContainer;
    @BindView(R.id.footerButtonsContainer) LinearLayout buttonsContainer;

    private ArrayList<ItemOfComment> commentList;

    private String videoNum;
    private String videoUrl;

    private String IDX = null;

    private OnFooterLikeButtonClickListener onFooterLikeButtonClickListener;

    private boolean isLike, isReady;

    private CommentListAdapter commentListAdapter;

    private OnProfileImgClickListener onProfileImgClickListener;
    private ShareBottomSheet.ShareBottomSheetListener shareBottomSheetListener;

    private Handler handler;
    private static final int SELECT_COMMENTS = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentList = new ArrayList<>();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SELECT_COMMENTS :
                        if(videoNum != null){
                            selectComments(videoNum, true);
                        }
                        break;
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_footer, container, false);

        ButterKnife.bind(this, view);

        isReady = false;

        initRefresher();

        initViews();

        return view;
    }

    private void initViews(){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                onFocusChanged(hasFocus);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(editText.getText().toString().trim())) {
                    Toast.makeText(getContext(), "enter comment", Toast.LENGTH_SHORT).show();
                } else {
                    insertComment();
                }
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoNum != null){
                    ReportBottomSheet.create(videoNum).show(getActivity().getSupportFragmentManager(), "reportBottomSheet");
                }
            }
        });

        likeInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoNum != null){
                    LikeListBottomSheet likeListBottomSheet = LikeListBottomSheet.create(videoNum);
                    if(onProfileImgClickListener != null){
                        likeListBottomSheet.setOnProfileImgClickListener(onProfileImgClickListener);
                    }
                    likeListBottomSheet.show(getActivity().getSupportFragmentManager(), "likeListBottomSheet");
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoUrl != null && shareBottomSheetListener != null){
                    ShareBottomSheet shareBottomSheet = ShareBottomSheet.create(videoUrl);
                    shareBottomSheet.setShareBottomSheetListener(shareBottomSheetListener);
                    shareBottomSheet.show(getActivity().getSupportFragmentManager(), "ShareBottomSheet");
                }
            }
        });
    }

    public void setFooterStateLoading(){
        this.videoNum = null;
        commentList.clear();
        if(commentListAdapter != null){
            commentListAdapter.notifyDataSetChanged();
        }
    }

    public void setVideoInfo(VideoInfo videoInfo){
        this.videoNum = videoInfo.getVideoNum();
        this.videoUrl = videoInfo.getVideoUrl();
//        handler.removeMessages(SELECT_COMMENTS);
//        handler.sendEmptyMessageDelayed(SELECT_COMMENTS, 1000);
        Utilities.logD("videoNum", videoInfo.getVideoNum());
        selectComments(videoNum, true);
    }

    private void initLikeInfo(boolean isLike, String likeCount){
        this.isLike = isLike;

        if(isLike){
            likeButton.setImageResource(R.drawable.btn_footer_like_true);
        }else{
            likeButton.setImageResource(R.drawable.btn_footer_like_false);
        }

        likeCounter.setText(likeCount);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onFooterLikeButtonClickListener != null){
                    onFooterLikeButtonClickListener.onLikeButtonClick();
                }
            }
        });
    }

    private void initListView(){
        commentListView.setEmptyView(commentListEmptyView);

        commentListAdapter = new CommentListAdapter(getContext(), commentList);

        if(onProfileImgClickListener != null){
            commentListAdapter.setOnProfileImgClickListener(onProfileImgClickListener);
        }

        commentListView.setAdapter(commentListAdapter);

        commentListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        commentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });
    }

    private void setAutoListUpdate(boolean autoListUpdate){
        if(autoListUpdate){
            commentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private boolean lastItemVisibleFlag = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(scrollState == SCROLL_STATE_IDLE && lastItemVisibleFlag){
                        selectComments(videoNum, false);
                        setAutoListUpdate(false);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }
            });
        }else{
            commentListView.setOnScrollListener(null);
        }
    }

    private void initRefresher(){
        commentListRefresher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        commentListRefresher.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                selectComments(videoNum, true);
            }
        });
    }

    public void likeVideo(){
        if(isReady){
            isLike = !isLike;
            toggleLikeButton(isLike);
            modLikeCount(isLike);
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    likeVideo();
                }
            }, 1000);
        }
    }

    private void onFocusChanged(boolean focus){
        if (focus) {
            commentListContainer.setVisibility(GONE);
            likeInfoContainer.setVisibility(GONE);
            buttonsContainer.setVisibility(GONE);
        } else {
            commentListContainer.setVisibility(View.VISIBLE);
            likeInfoContainer.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
            Utilities.hideKeyboard(getContext(), editText);
        }
    }

    private void toggleLikeButton(boolean isLike){
        if(isLike){
            likeButton.setImageResource(R.drawable.btn_footer_like_true);
        }else{
            likeButton.setImageResource(R.drawable.btn_footer_like_false);
        }
        YoYo.with(Techniques.Flash).duration(1000).playOn(likeButton);
    }

    public void setOnProfileImgClickListener(OnProfileImgClickListener onProfileImgClickListener) {
        this.onProfileImgClickListener = onProfileImgClickListener;
        if(commentListAdapter != null){
            commentListAdapter.setOnProfileImgClickListener(onProfileImgClickListener);
        }
    }

    public void setShareBottomSheetListener(ShareBottomSheet.ShareBottomSheetListener shareBottomSheetListener) {
        this.shareBottomSheetListener = shareBottomSheetListener;
    }

    private void selectComments(final String videoNum, final boolean refresh){
        isReady = false;

        if(refresh){
            if(!commentList.isEmpty()){
                commentList.clear();
            }
            IDX = null;
        }

        Network network = new Network(getContext(), "getReplys") {
            @Override
            protected void processFinish(JsonNode result) {
                JsonNode data = Utilities.jsonParse(result.get("DATA").asText());

                if(data != null){
                    if(refresh){
                        initLikeInfo("Y".equals(data.get("LIKE_YN").asText()), data.get("LIKE_CNT").asText());
                    }

                    IDX = data.get("IDX").asText();

                    if(!IDX.equals("D")){
                        ItemOfComment itemOfComment;

                        for(JsonNode commentData : Utilities.jsonParse(data.get("DATA_LIST").asText())){
                            itemOfComment = new ItemOfComment();

                            itemOfComment.setUserNum(commentData.get("USER_NO").asText());
                            itemOfComment.setCommentNum(commentData.get("REPLY_NO").asText());
                            itemOfComment.setName(commentData.get("NAME").asText());
                            itemOfComment.setNickName(commentData.get("NICKNAME").asText());
                            itemOfComment.setImgUrl(commentData.get("IMG_URL").asText());
                            itemOfComment.setComment(commentData.get("REPLY_CONT").asText());
                            itemOfComment.setLikeCount(commentData.get("LIKE_CNT").asText());
                            itemOfComment.setIsLike("Y".equals(commentData.get("LIKE_YN").asText()));

                            String time = commentData.get("DIFF").asText();

                            switch (commentData.get("DIFF_FLAG").asText()){
                                case "D":
                                    time += " day ago";
                                    break;
                                case "H":
                                    time += " hour ago";
                                    break;
                                case "M":
                                    time += " minute ago";
                                    break;
                            }

                            itemOfComment.setTime(time);

                            commentList.add(itemOfComment);
                        }
                    }

                    if(commentListAdapter == null){
                        initListView();
                    }else{
                        commentListAdapter.notifyDataSetChanged();
                    }

                    commentListAdapter.setVideoNum(videoNum);

                    if(commentListRefresher.isRefreshing()){
                        commentListRefresher.refreshComplete();
                    }

                    isReady = true;

                    setAutoListUpdate(!IDX.equals("D"));
                }
            }

            @Override
            protected void onResultN(JsonNode result) {
                Utilities.logD("Footer", "select comment RTN_VAL is N");
            }
        };

        String url = "v001/video/reply";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getActivity(), network, url);

        serverCommunicator.addData("MEDIA_NO", videoNum);
        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("IDX", IDX);

        serverCommunicator.communicate();
    }

    private void insertComment(){
        Network network = new Network(getContext(), "txReplyUp") {
            @Override
            protected void processFinish(JsonNode result) {
                editText.clearFocus();
                editText.setText("");
                commentListRefresher.autoRefresh();
            }

            @Override
            protected void onResultN(JsonNode result) {
                editText.clearFocus();
                super.onResultN(result);
            }
        };

        String url = "v001/video/reply";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("FLG", "I");
        serverCommunicator.addData("MEDIA_NO", videoNum);
        serverCommunicator.addData("REPLY_NO", null);
        serverCommunicator.addData("REPLY_CONT", editText.getText().toString());
        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());

        serverCommunicator.communicate();
    }

    public void modLikeCount(final boolean increase){
        Runnable runnable = new Runnable() {
            Techniques first, second;
            @Override
            public void run() {
                if(increase){
                    first = Techniques.FadeOutDown;
                    second = Techniques.FadeInDown;
                }else{
                    first = Techniques.FadeOutUp;
                    second = Techniques.FadeInUp;
                }
                YoYo.with(first).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        int likeCount = Integer.parseInt(likeCounter.getText().toString());
                        if(increase){
                            likeCount++;
                        }else{
                            likeCount--;
                        }
                        likeCounter.setText(Integer.toString(likeCount));
                        YoYo.with(second).duration(500).playOn(likeCounter);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(likeCounter);
            }
        };

        if(isReady){
            likeCounter.post(runnable);
        }else{
            likeCounter.postDelayed(runnable,1000);
        }
    }

    public void commentEditTextClearFocus(){
        editText.clearFocus();
    }

    public void setOnFooterLikeButtonClickListener(OnFooterLikeButtonClickListener onFooterLikeButtonClickListener) {
        this.onFooterLikeButtonClickListener = onFooterLikeButtonClickListener;
    }

    public interface OnFooterLikeButtonClickListener{
        void onLikeButtonClick();
    }
}
