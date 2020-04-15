package net.videofactory.new_audi.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-16.
 */
public class HomeCardAddSearchDialogAutoCompleteAdapter extends BaseAdapter implements Filterable{

    private LayoutInflater inflater;

    private Context context;

    private int type; // 0 : channel, 1 : tag

    private ArrayList<ItemOfFollowed> followedList;

    private ArrayList<ItemOfFollowed> filterResultList;

    private ArrayList<ItemOfFollowed> followedListAll;

    private AudiFilter audiFilter;

    public HomeCardAddSearchDialogAutoCompleteAdapter(Context context, int type, ArrayList<ItemOfFollowed> followedList) {
        this.context = context;
        this.type = type;
        this.followedList = followedList;
        this.inflater = LayoutInflater.from(context);
        this.followedListAll = new ArrayList<>(followedList);
        this.filterResultList = new ArrayList<>();
        this.audiFilter = new AudiFilter();
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

    @Override
    public Filter getFilter() {
        return audiFilter;
    }

    static class ViewHolder {

        @Bind(R.id.homeCardAddDialogSearchListProfile) CircleLoadingImageView profile;
        @Bind(R.id.homeCardAddDialogSearchListName) TextView name;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class AudiFilter extends Filter{

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ItemOfFollowed)resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null){
                filterResultList.clear();
                if(followedList != null && filterResultList != null){
                    for(ItemOfFollowed itemOfFollowed : followedList){
                        if(itemOfFollowed.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                            filterResultList.add(itemOfFollowed);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filterResultList;
                results.count = filterResultList.size();

                return results;
            }else{
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            followedList.clear();
            if(results != null && results.count > 0){
                List<?> result = (List<?>) results.values;
                for(Object object : result){
                    if(object instanceof ItemOfFollowed){
                        followedList.add((ItemOfFollowed)object);
                    }
                }
            }else if(constraint == null){
                followedList.addAll(followedListAll);
            }
            notifyDataSetChanged();
        }
    }
}
