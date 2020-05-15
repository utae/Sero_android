package net.videofactory.new_audi.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-01-12.
 */
public class SearchChannelListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private Context context;

    private ArrayList<ItemOfSearchChannel> channelList;

    public SearchChannelListAdapter(Context context, ArrayList<ItemOfSearchChannel> channelList) {
        this.context = context;
        this.channelList = channelList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return channelList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_search_channel, parent, false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(channelList.get(position).getProfileUrl() == null){
            holder.profile.setImageResource(R.drawable.ic_profile_default);
        }else{
            if(Utilities.cancelPotentialTask(channelList.get(position).getProfileUrl(), holder.profile)){
                ImagePickerTask imagePickerTask = new ImagePickerTask(holder.profile);
                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, imagePickerTask);
                holder.profile.setImageLoadingDrawable(loadingDrawable);
                imagePickerTask.execute(channelList.get(position).getProfileUrl());
            }
        }

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.searchChannelListProfileView) CircleLoadingImageView profile;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
