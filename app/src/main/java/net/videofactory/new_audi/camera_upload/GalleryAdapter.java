package net.videofactory.new_audi.camera_upload;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.etsy.android.grid.util.DynamicHeightImageView;

import net.videofactory.new_audi.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2015-11-24.
 */
public class GalleryAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ItemOfGallery> videoInfoList;


    public GalleryAdapter(Context context, ArrayList<ItemOfGallery> videoInfoList) {
        this.context = context;
        this.videoInfoList = videoInfoList;
        inflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_gallery, parent, false);
            holder = new ViewHolder(convertView);
            holder.videoThumbnail.setHeightRatio(1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(cancelPotentialTask(videoInfoList.get(position).getData(), holder.videoThumbnail)){
            DeviceVideoThumbnailPickerTask thumbnailPickerTask = new DeviceVideoThumbnailPickerTask(holder.videoThumbnail);
            LoadingDrawable loadingDrawable = new LoadingDrawable(thumbnailPickerTask);
            holder.videoThumbnail.setImageDrawable(loadingDrawable);
            thumbnailPickerTask.execute(videoInfoList.get(position).getData());
        }

        return convertView;
    }

    private boolean cancelPotentialTask(String videoPath, DynamicHeightImageView thumbnailView){
        DeviceVideoThumbnailPickerTask thumbnailPickerTask = getThumbnailPickerTask(thumbnailView);
        if(thumbnailPickerTask != null){
            String thumbnailPath = thumbnailPickerTask.getVideoPath();
            if( (thumbnailPath == null) || (!thumbnailPath.equals(videoPath))){
                thumbnailPickerTask.cancel(true);
            }else{
                return false;
            }
        }
        return true;
    }

    static DeviceVideoThumbnailPickerTask getThumbnailPickerTask(DynamicHeightImageView thumbnailView){
        if(thumbnailView != null){
            Drawable drawable = thumbnailView.getDrawable();
            if(drawable instanceof LoadingDrawable){
                LoadingDrawable loadingDrawable = (LoadingDrawable) drawable;
                return loadingDrawable.getThumnailPickerTask();
            }
        }
        return null;
    }

    static class ViewHolder {

        @Bind(R.id.videoThumbnail) DynamicHeightImageView videoThumbnail;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class LoadingDrawable extends ColorDrawable {
        private final WeakReference<DeviceVideoThumbnailPickerTask> thumbnailPickerTaskWeakReference;

        public LoadingDrawable(DeviceVideoThumbnailPickerTask thumbnailPickerTask) {
            super(Color.BLACK);
            thumbnailPickerTaskWeakReference = new WeakReference<>(thumbnailPickerTask);
        }

        public DeviceVideoThumbnailPickerTask getThumnailPickerTask(){
            return thumbnailPickerTaskWeakReference.get();
        }
    }
}
