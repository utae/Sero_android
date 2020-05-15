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
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-01-20.
 */
public class ReportBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.reportBSInappropriate) TextView inappropriateButton;
    @BindView(R.id.reportBSCopyright) TextView copyrightButton;

    private String mediaNum;

    public static ReportBottomSheet create(String mediaNum){
        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
        Bundle args = new Bundle();
        args.putString("mediaNum", mediaNum);
        reportBottomSheet.setArguments(args);
        return reportBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaNum = getArguments().getString("mediaNum");
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

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_report);

        ButterKnife.bind(this, bottomSheetDialog);

        inappropriateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMedia("050-001");
            }
        });

        copyrightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMedia("050-002");
            }
        });

        return bottomSheetDialog;
    }

    private void reportMedia(String reportType){
        Network network = new Network(getContext(), "txReportMedia") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        Toast.makeText(getContext(), "Your report has been received.", Toast.LENGTH_LONG).show();
                        dismiss();
                    }else{
                        Toast.makeText(getContext(), result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                }
            }
        };

        String url = "v001/home/media";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("MEDIA_NO", mediaNum);
        serverCommunicator.addData("REPORT_TP", reportType);

        serverCommunicator.communicate();
    }
}
