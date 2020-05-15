package net.videofactory.new_audi.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
 * Created by Utae on 2016-06-16.
 */
public class HomeCardAddSearchDialogListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private Context context;

    private int type; // 0 : channel, 1 : tag

    private ArrayList<ItemOfFollowed> followedList;

    public HomeCardAddSearchDialogListAdapter(Context context, int type, ArrayList<ItemOfFollowed> followedList) {
        this.context = context;
        this.type = type;
        this.followedList = followedList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return followedList.size();
    }

    @Override
    public Object getItem(int position) {
        return followedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_home_card_search_dialog_list, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(followedList.get(position).getName());

        switch (type){
            case 0 :
                if(followedList.get(position).getProfileUrl() == null){
                    holder.profile.setImageResource(R.drawable.ic_profile_default);
                }else{
                    if(Utilities.cancelPotentialTask(followedList.get(position).getProfileUrl(), holder.profile)){
                        ImagePickerTask imagePickerTask = new ImagePickerTask(holder.profile);
                        LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, imagePickerTask);
                        holder.profile.setImageLoadingDrawable(loadingDrawable);
                        imagePickerTask.execute(followedList.get(position).getProfileUrl());
                    }
                }
                break;

            case 1 :
                holder.profile.setImageResource(R.drawable.ic_home_card_add_tag);
                break;
        }

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.homeCardAddDialogSearchListProfile) CircleLoadingImageView profile;
        @BindView(R.id.homeCardAddDialogSearchListName) TextView name;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
