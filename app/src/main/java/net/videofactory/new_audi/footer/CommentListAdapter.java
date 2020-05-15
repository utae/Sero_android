package net.videofactory.new_audi.footer;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.main.OnProfileImgClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-10-29.
 */
public class CommentListAdapter  extends BaseAdapter{

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ItemOfComment> commentList;
    private OnProfileImgClickListener onProfileImgClickListener;
    private String videoNum;

    public CommentListAdapter(Context context, ArrayList<ItemOfComment> commentList) {
        this.context = context;
        this.commentList = commentList;
        inflater = LayoutInflater.from(context);
    }

    public void setVideoNum(String videoNum) {
        this.videoNum = videoNum;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_comment, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nickName.setText(commentList.get(position).getNickName());
        holder.comment.setText(commentList.get(position).getComment());
        holder.time.setText(commentList.get(position).getTime());

        switchLikeButton(holder.likeButton, commentList.get(position).isLike());

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeComment(position, (TextView)v, holder.likeCount);
            }
        });

        holder.likeCount.setText(commentList.get(position).getLikeCount());

        if(commentList.get(position).getUserNum().equals(UserInfo.getUserNum())){
            holder.delButton.setVisibility(View.VISIBLE);
            holder.delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDeleteDialog(position).show();
                }
            });
        }else{
            holder.delButton.setVisibility(View.GONE);
        }

        if(commentList.get(position).getImgUrl() == null){
            holder.profile.setImageResource(R.drawable.ic_profile_default);
        }else{
            if(Utilities.cancelPotentialTask(commentList.get(position).getImgUrl(), holder.profile)){
                ImagePickerTask imagePickerTask = new ImagePickerTask(holder.profile);
                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, imagePickerTask);
                holder.profile.setImageLoadingDrawable(loadingDrawable);
                imagePickerTask.execute(commentList.get(position).getImgUrl());
            }
        }

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if(onProfileImgClickListener != null){
                    onProfileImgClickListener.onProfileImgClick(commentList.get(position).getUserNum());
                }
            }
        });

        return convertView;
    }

    private AlertDialog getDeleteDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete this comment?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteComment(position);
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    public String getComment(int position){
        return commentList.get(position).getComment();
    }

    private void switchLikeButton(TextView likeButton, boolean isLike){
        if(isLike){
            likeButton.setText("UnLike");
        }else{
            likeButton.setText("Like");
        }
    }

    public void setOnProfileImgClickListener(OnProfileImgClickListener onProfileImgClickListener) {
        this.onProfileImgClickListener = onProfileImgClickListener;
    }

    private void deleteComment(final int position){
        Network network = new Network(context, "txReplyUp") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        commentList.remove(position);
                        notifyDataSetChanged();
                    }else{
                        Toast.makeText(context, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        String url = "v001/video/reply";

        ServerCommunicator serverCommunicator = new ServerCommunicator(context, network, url);

        serverCommunicator.addData("MEDIA_NO", videoNum);
        serverCommunicator.addData("USER_NO", commentList.get(position).getUserNum());
        serverCommunicator.addData("FLG", "D");
        serverCommunicator.addData("REPLY_NO", commentList.get(position).getCommentNum());

        serverCommunicator.communicate();
    }

    private void likeComment(final int position, final TextView likeButton, final TextView likeCount){
        Network network = new Network(context, "txLikeReply") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        commentList.get(position).setIsLike(!commentList.get(position).isLike());
                        commentList.get(position).setLikeCount(Integer.toString(Integer.parseInt(commentList.get(position).getLikeCount())+1));
                        likeCount.setText(Integer.toString(Integer.parseInt(commentList.get(position).getLikeCount())));
                        switchLikeButton(likeButton, commentList.get(position).isLike());
                    }else{
                        Toast.makeText(context, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        String url = "v001/video/reply";

        ServerCommunicator serverCommunicator = new ServerCommunicator(context, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("REPLY_NO", commentList.get(position).getCommentNum());

        serverCommunicator.communicate();
    }

    static class ViewHolder {

        @BindView(R.id.commentProfile) CircleLoadingImageView profile;

        @BindView(R.id.commentNickName) TextView nickName;

        @BindView(R.id.commentContent) TextView comment;

        @BindView(R.id.commentTime) TextView time;

        @BindView(R.id.commentLikeButton) TextView likeButton;

        @BindView(R.id.commentDelButton) TextView delButton;

        @BindView(R.id.commentLikeCount) TextView likeCount;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
