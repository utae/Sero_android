package net.videofactory.new_audi.footer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.videofactory.new_audi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-09-08.
 */

public class ShareBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.shareBSFacebook) TextView facebookButton;
    @BindView(R.id.shareBSInstagram) TextView instagramButton;
    @BindView(R.id.shareBSUrl) TextView urlButton;

    private String videoUrl;

    private ShareBottomSheetListener shareBottomSheetListener;

    public static ShareBottomSheet create(String videoUrl){
        ShareBottomSheet shareBottomSheet = new ShareBottomSheet();
        Bundle args = new Bundle();
        args.putString("videoUrl", videoUrl);
        shareBottomSheet.setArguments(args);
        return shareBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoUrl = getArguments().getString("videoUrl");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog)super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_share);

        ButterKnife.bind(this, bottomSheetDialog);

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareBottomSheetListener != null){
                    shareBottomSheetListener.shareFacebook(videoUrl);
                }
            }
        });
        return bottomSheetDialog;
    }

    public void setShareBottomSheetListener(ShareBottomSheetListener shareBottomSheetListener) {
        this.shareBottomSheetListener = shareBottomSheetListener;
    }

    public interface ShareBottomSheetListener{
        void shareFacebook(String videoUrl);
        void shareInstagram();
        void shareUrl();
    }
}
