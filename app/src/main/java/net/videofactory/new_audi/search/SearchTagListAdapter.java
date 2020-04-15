package net.videofactory.new_audi.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-01-12.
 */
public class SearchTagListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private Context context;

    private ArrayList<ItemOfSearchTag> taglList;

    public SearchTagListAdapter(Context context, ArrayList<ItemOfSearchTag> taglList) {
        this.context = context;
        this.taglList = taglList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return taglList.size();
    }

    @Override
    public Object getItem(int position) {
        return taglList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_search_tag, parent, false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(taglList.get(position).getThumbnailUrl() == null){
            holder.thumbnail.setImageResource(R.drawable.img_video_thumbnail_error);
        }else{
            if(Utilities.cancelPotentialTask(taglList.get(position).getThumbnailUrl(), holder.thumbnail)){
                ImagePickerTask imagePickerTask = new ImagePickerTask(holder.thumbnail);
                imagePickerTask.setErrorImgIdRes(R.drawable.img_video_thumbnail_error);
                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.img_video_thumbnail_loading, imagePickerTask);
                holder.thumbnail.setImageLoadingDrawable(loadingDrawable);
                imagePickerTask.execute(taglList.get(position).getThumbnailUrl());
            }
        }

        holder.name.setText(taglList.get(position).getTagName());

        return convertView;
    }

    static class ViewHolder {

        @Bind(R.id.searchTagListThumbnailView) LoadingImageView thumbnail;

        @Bind(R.id.searchTagListNameView) TextView name;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
