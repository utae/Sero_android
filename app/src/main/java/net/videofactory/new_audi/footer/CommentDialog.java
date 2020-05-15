package net.videofactory.new_audi.footer;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import net.videofactory.new_audi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-22.
 */
public class CommentDialog extends DialogFragment {

    private CommentListAdapter commentListAdapter;

    @BindView(R.id.copyButton) Button copyButton;

    private int position;

    private String userNum = null;

    private OnItemClickListener onItemClickListener;

    public static CommentDialog create(int position, String userNum){
        CommentDialog commentDialog = new CommentDialog();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("userNum", userNum);
        commentDialog.setArguments(args);
        return commentDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position", -1);
        userNum = getArguments().getString("userNum", null);
        if(position == -1){
            dismiss();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_comment, container, false);

        ButterKnife.bind(this, view);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onCopyButtonClick(position);
                }
                dismiss();
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    public interface OnItemClickListener{
        void onCopyButtonClick(int position);
    }
}
