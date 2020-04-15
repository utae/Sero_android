package net.videofactory.new_audi.home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import net.videofactory.new_audi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-15.
 */
public class HomeCardAddDialog extends DialogFragment {

    @Bind(R.id.homeCardAddDialogChannel) TextView channelAddButton;
    @Bind(R.id.homeCardAddDialogTag) TextView tagAddButton;
    @Bind(R.id.homeCardAddDialogClose) ImageButton closeButton;

    private OnDialogButtonCLickListener onDialogButtonCLickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_home_card_add, container, false);

        ButterKnife.bind(this, view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        channelAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogButtonCLickListener.onTypeButtonClick(0);
            }
        });

        tagAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogButtonCLickListener.onTypeButtonClick(1);
            }
        });

        return view;
    }

    public void setOnDialogCloseButtonCLickListener(OnDialogButtonCLickListener onDialogButtonCLickListener) {
        this.onDialogButtonCLickListener = onDialogButtonCLickListener;
    }

    public interface OnDialogButtonCLickListener{
        void onTypeButtonClick(int type);
    }
}
