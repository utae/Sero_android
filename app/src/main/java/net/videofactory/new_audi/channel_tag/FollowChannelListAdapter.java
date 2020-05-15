package net.videofactory.new_audi.channel_tag;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
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
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-30.
 */
public class FollowChannelListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ItemOfFollowListChannel> channelList;

    public FollowChannelListAdapter(Context context, ArrayList<ItemOfFollowListChannel> channelList) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_follow_list_channel, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(channelList.get(position).getProfileUrl() == null || "".equals(channelList.get(position).getProfileUrl())){
            holder.profile.setImageResource(R.drawable.ic_profile_default);
        }else{
            if(Utilities.cancelPotentialTask(channelList.get(position).getProfileUrl(), holder.profile)){
                ImagePickerTask imagePickerTask = new ImagePickerTask(holder.profile);
                LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, imagePickerTask);
                holder.profile.setImageLoadingDrawable(loadingDrawable);
                imagePickerTask.execute(channelList.get(position).getProfileUrl());
            }
        }

        holder.nickName.setText(channelList.get(position).getNickName());
        holder.name.setText(channelList.get(position).getName());
        toggleFollowButton(holder.followButton, channelList.get(position).isFollow());

        if(channelList.get(position).getUserNum().equals(UserInfo.getUserNum())){
            holder.followButton.setVisibility(View.GONE);
        }else{
            if(holder.followButton.getVisibility() == View.GONE){
                holder.followButton.setVisibility(View.VISIBLE);
            }
            holder.followButtonContainer.setOnClickListener(new FollowButtonClickListener(holder.followButton) {
                @Override
                public void onFollowButtonCLick(View v, Button followButton) {
                    followChannel(channelList.get(position), followButton);
                }
            });
        }


        return convertView;
    }

    private void toggleFollowButton(Button followButton, boolean follow){
        if(follow){
            followButton.setTextColor(Color.parseColor("#ffffff"));
            followButton.setBackgroundColor(Color.parseColor("#00c558"));
        }else{
            followButton.setTextColor(Color.parseColor("#aaaaaa"));
            followButton.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    private void followChannel(final ItemOfFollowListChannel channel, final Button followButton){
        Network network = new Network(context, "txFollowCh") {
            @Override
            protected void processFinish(JsonNode result) {
                channel.setFollow(!channel.isFollow());
                toggleFollowButton(followButton, channel.isFollow());
            }
        };

        String url = "v001/follow";

        ServerCommunicator serverCommunicator = new ServerCommunicator(context, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("FOLLOWING", channel.getUserNum());

        serverCommunicator.communicate();
    }

    private abstract class FollowButtonClickListener implements View.OnClickListener {

        private Button followButton;

        FollowButtonClickListener(Button followButton) {
            this.followButton = followButton;
        }

        @Override
        public void onClick(View v) {
            onFollowButtonCLick(v, followButton);
        }

        public abstract void onFollowButtonCLick(View v, Button followButton);
    }

    static class ViewHolder {

        @BindView(R.id.followListProfile) CircleLoadingImageView profile;

        @BindView(R.id.followListNickName) TextView nickName;

        @BindView(R.id.followListName) TextView name;

        @BindView(R.id.followListChannelFollowButton) Button followButton;

        @BindView(R.id.followListChannelFollowButtonContainer) FrameLayout followButtonContainer;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
