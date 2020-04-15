package net.videofactory.new_audi.video;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.RatioLoadingImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-01-21.
 */
public class VideoThumbnailListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private Context context;

    private ArrayList<VideoInfo> videoInfoList;

    private AlertDialog.Builder alertBuilder;

    public VideoThumbnailListAdapter(Context context, ArrayList<VideoInfo> videoInfoList) {
        this.context = context;
        this.videoInfoList = videoInfoList;
        this.inflater = LayoutInflater.from(context);
        this.alertBuilder = new AlertDialog.Builder(context);
    }

    @Override
    public int getCount() {
        return videoInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_video_thumbnail, parent, false);
            holder = new ViewHolder(convertView);
            holder.thumbnail.setHeightRatio(1.7);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String imgUrl = videoInfoList.get(position).getImgUrl();

        if(imgUrl == null || "".equals(imgUrl)){
            holder.thumbnail.setImageResource(R.drawable.img_video_thumbnail_error);
        }else{
            if(Utilities.cancelPotentialTask(imgUrl, holder.thumbnail)){
                ImagePickerTask thumbnailPickerTask = new ImagePickerTask(holder.thumbnail);
                thumbnailPickerTask.setErrorImgIdRes(R.drawable.img_video_thumbnail_error);
                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.img_video_thumbnail_loading, thumbnailPickerTask);
                holder.thumbnail.setImageLoadingDrawable(loadingDrawable);
                thumbnailPickerTask.execute(imgUrl);
            }
        }

        if(videoInfoList.get(position).isMine()){
            holder.delButton.setVisibility(View.VISIBLE);

            holder.delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertBuilder.setTitle("Delete video")
                            .setMessage("Are you sure you want to delete your video?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    delVideo(position);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
                }
            });
        }

        return convertView;
    }

    private void delVideo(final int position){
        Network network = new Network(context, "txMedia") {
            @Override
            protected void processFinish(JsonNode result) {
                if(result != null){
                    if("Y".equals(result.get("RTN_VAL").asText())){
                        videoInfoList.remove(position);
                        notifyDataSetChanged();
                    }else{
                        Toast.makeText(context, result.get("MSG").asText(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        String url = "v001/home/media";

        ServerCommunicator serverCommunicator = new ServerCommunicator(context, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("FLG", "D");
        serverCommunicator.addData("MEDIA_NO", videoInfoList.get(position).getVideoNum());

        serverCommunicator.communicate();
    }

    static class ViewHolder {

        @Bind(R.id.personVideoListThumbnail) RatioLoadingImageView thumbnail;

        @Bind(R.id.personVideoListDel) ImageButton delButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
