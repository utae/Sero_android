package net.videofactory.new_audi.alarm;

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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-05-31.
 */
public class AlarmListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ItemOfAlarm> alarmList;

    public AlarmListAdapter(Context context, ArrayList<ItemOfAlarm> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_alarm, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(alarmList.get(position).getProfileUrl() == null || "".equals(alarmList.get(position).getProfileUrl())){
            holder.profile.setImageResource(R.drawable.ic_profile_default);
        }else{
            if(Utilities.cancelPotentialTask(alarmList.get(position).getProfileUrl(), holder.profile)){
                ImagePickerTask imagePickerTask = new ImagePickerTask(holder.profile);
                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, imagePickerTask);
                holder.profile.setImageLoadingDrawable(loadingDrawable);
                imagePickerTask.execute(alarmList.get(position).getProfileUrl());
            }
        }

        holder.name.setText(alarmList.get(position).getName());
        holder.firstContents.setText(alarmList.get(position).getFirstContents());
        if(alarmList.get(position).getSecondContents() != null && !"".equals(alarmList.get(position).getSecondContents())){
            holder.secondContents.setVisibility(View.VISIBLE);
            holder.secondContents.setText(alarmList.get(position).getSecondContents());
        }else{
            holder.secondContents.setVisibility(View.GONE);
        }
        holder.time.setText(alarmList.get(position).getTime());

        return convertView;
    }

    static class ViewHolder {

        @Bind(R.id.alarmProfile) CircleLoadingImageView profile;

        @Bind(R.id.alarmName) TextView name;

        @Bind(R.id.alarmFirstContents) TextView firstContents;

        @Bind(R.id.alarmSecondContents) TextView secondContents;

        @Bind(R.id.alarmTime) TextView time;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
