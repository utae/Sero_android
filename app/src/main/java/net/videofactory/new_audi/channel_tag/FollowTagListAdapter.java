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
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-30.
 */
public class FollowTagListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ItemOfFollowListTag> tagList;

    public FollowTagListAdapter(Context context, ArrayList<ItemOfFollowListTag> tagList) {
        this.context = context;
        this.tagList = tagList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public Object getItem(int position) {
        return tagList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_follow_list_tag, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tagName.setText(tagList.get(position).getTagName());

        holder.followButtonContainer.setOnClickListener(new FollowButtonClickListener(holder.followButton) {
            @Override
            public void onFollowButtonCLick(View v, Button followButton) {
                followTag(tagList.get(position), followButton);
            }
        });

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

    private void followTag(final ItemOfFollowListTag tag, final Button followButton){
        Network network = new Network(context, "txFollowHt") {
            @Override
            protected void processFinish(JsonNode result) {
                tag.setFollow(!tag.isFollow());
                toggleFollowButton(followButton, tag.isFollow());
            }
        };

        String url = "v001/follow";

        ServerCommunicator serverCommunicator = new ServerCommunicator(context, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("HASHTAG", tag.getTagName());

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

        @BindView(R.id.followListTagName) TextView tagName;

        @BindView(R.id.followListTagFollowButton) Button followButton;

        @BindView(R.id.followListTagFollowButtonContainer) FrameLayout followButtonContainer;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
