package net.videofactory.new_audi.channel_tag;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-07-20.
 */

public class ChannelShowMoreDialog extends BottomSheetDialogFragment {

    private String channelNum;
    private boolean block;

    @Bind(R.id.channelPageBlockButton) TextView blockButton;
    @Bind(R.id.channelPageReportButton) TextView reportButton;

    private AlertDialog.Builder alertBuilder;
    private ChannelShowMoreListener channelShowMoreListener;

    public static ChannelShowMoreDialog create(String channelNum, boolean block){
        ChannelShowMoreDialog channelShowMoreDialog = new ChannelShowMoreDialog();
        Bundle args = new Bundle();
        args.putString("channelNum", channelNum);
        args.putBoolean("block", block);
        channelShowMoreDialog.setArguments(args);
        return channelShowMoreDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        channelNum = getArguments().getString("channelNum");
        block = getArguments().getBoolean("block");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog)super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetDialog.setContentView(R.layout.dialog_channel_show_more);

        ButterKnife.bind(this, bottomSheetDialog);

        init();

        return bottomSheetDialog;
    }

    private void init(){
        if(block){
            blockButton.setText("Unblock User");
        }else{
            blockButton.setText("Block User");
        }

        blockButton.setSelected(block);

        alertBuilder = new AlertDialog.Builder(getContext());

        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(block){
                    alertBuilder.setTitle("Unblock User")
                            .setMessage("Are you sure you want to unblock this user?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    unblockChannel();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                }else{
                    alertBuilder.setTitle("Block User")
                            .setMessage("Are you sure you want to block this user?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    blockChannel();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                }

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBuilder.setTitle("Report Inappropriate User")
                        .setMessage("Are you sure you want to report this user?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reportChannel();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void blockChannel(){
        Network network = new Network(getContext(), "txBlockUser") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        if(channelShowMoreListener != null){
                            channelShowMoreListener.onBlockUser(true);
                        }
                    }else{
                        Toast.makeText(getContext(), result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }
                    dismiss();
                }
            }
        };

        String url = "v001/block";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("BLOCK_NO", channelNum);

        serverCommunicator.communicate();
    }

    private void unblockChannel(){
        Network network = new Network(getContext(), "txUnBlockUser") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        if(channelShowMoreListener != null){
                            channelShowMoreListener.onBlockUser(false);
                        }
                    }else{
                        Toast.makeText(getContext(), result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }
                    dismiss();
                }
            }
        };

        String url = "v001/block";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("BLOCK_NO", channelNum);

        serverCommunicator.communicate();
    }

    private void reportChannel(){
        Network network = new Network(getContext(), "txReportUser") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        Toast.makeText(getContext(), "report succeed", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(), result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }
                    dismiss();
                }
            }
        };

        String url = "v001/block";

        ServerCommunicator serverCommunicator = new ServerCommunicator(getContext(), network, url);

        serverCommunicator.addData("REPORT_NO", channelNum);

        serverCommunicator.communicate();
    }

    public void setChannelShowMoreListener(ChannelShowMoreListener channelShowMoreListener) {
        this.channelShowMoreListener = channelShowMoreListener;
    }

    public interface ChannelShowMoreListener{
        void onBlockUser(boolean block);
    }
}
