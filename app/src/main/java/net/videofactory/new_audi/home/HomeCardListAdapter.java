package net.videofactory.new_audi.home;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.ItemOfCard;
import net.videofactory.new_audi.common.Network;
import net.videofactory.new_audi.common.ServerCommunicator;
import net.videofactory.new_audi.common.UserInfo;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.RatioLoadingImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-04-18.
 */
public class HomeCardListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private Context context;

    private ArrayList<ItemOfCard> cardList;

    private AlertDialog.Builder alertBuilder;

    public HomeCardListAdapter(Context context, ArrayList<ItemOfCard> cardList) {
        this.context = context;
        this.cardList = cardList;
        this.inflater = LayoutInflater.from(context);
        this.alertBuilder = new AlertDialog.Builder(context);
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final ItemOfCard cardData = cardList.get(position);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_card, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(cardData.getTitle());

        switch (cardData.getType()){

            case "020-005" :
                holder.profile.setImageResource(R.drawable.ic_profile_hashtag);
                break;

            case "020-004" :
                if(cardData.getProfileUrl() == null){
                    holder.profile.setImageResource(R.drawable.ic_profile_default);
                }else{
                    if(Utilities.cancelPotentialTask(cardData.getProfileUrl(), holder.profile)){
                        ImagePickerTask imagePickerTask = new ImagePickerTask(holder.profile);
                        LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, imagePickerTask);
                        holder.profile.setImageLoadingDrawable(loadingDrawable);
                        imagePickerTask.execute(cardData.getProfileUrl());
                    }
                }
                break;
        }

        for(int i = 0; i < 4; i++){
            if(cardData.getImgUrls().get(i) == null) {
                holder.getThumbnailViewList().get(i).setImageResource(R.drawable.img_video_thumbnail_error);
            }else{
                if(Utilities.cancelPotentialTask(cardData.getImgUrls().get(i), holder.getThumbnailViewList().get(i))){
                    ImagePickerTask thumbnailPickerTask = new ImagePickerTask(holder.getThumbnailViewList().get(i));
                    thumbnailPickerTask.setErrorImgIdRes(R.drawable.img_video_thumbnail_error);
                    LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.img_video_thumbnail_loading, thumbnailPickerTask);
                    holder.getThumbnailViewList().get(i).setImageLoadingDrawable(loadingDrawable);
                    thumbnailPickerTask.execute(cardData.getImgUrls().get(i));
                }
            }
        }

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                alertBuilder.setTitle("Delete card")
                        .setMessage("Are you sure you want to delete your card?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delCard(cardData);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        });

        return convertView;
    }

    private void delCard(final ItemOfCard itemOfCard){
        Network network = new Network(context, "txDelCard") {
            @Override
            protected void processFinish(JsonNode result) {
                cardList.remove(itemOfCard);
                notifyDataSetChanged();
            }
        };

        String url = "v001/home/card";

        ServerCommunicator serverCommunicator = new ServerCommunicator(context, network, url);

        serverCommunicator.addData("USER_NO", UserInfo.getUserNum());
        serverCommunicator.addData("CARD_TP", itemOfCard.getType());
        switch (itemOfCard.getType()){
            case "020-004" :
                serverCommunicator.addData("REF_NO", itemOfCard.getUserNum());
                break;

            case "020-005" :
                serverCommunicator.addData("REF_NO", itemOfCard.getTitle());
                break;
        }

        serverCommunicator.communicate();
    }

    static class ViewHolder {

        @BindView(R.id.cardProfile) CircleLoadingImageView profile;

        @BindView(R.id.cardTitle) TextView title;

        @BindView(R.id.cardDelBtn) ImageButton delBtn;

        @BindView(R.id.cardVideoThumbnail1) RatioLoadingImageView thumbnail1;

        @BindView(R.id.cardVideoThumbnail2) RatioLoadingImageView thumbnail2;

        @BindView(R.id.cardVideoThumbnail3) RatioLoadingImageView thumbnail3;

        @BindView(R.id.cardVideoThumbnail4) RatioLoadingImageView thumbnail4;

        @BindView(R.id.cardVideoThumbnailContainer) LinearLayout thumbnailContainer;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            thumbnail1.setHeightRatio(1.75);
            thumbnail2.setHeightRatio(1.75);
            thumbnail3.setHeightRatio(1.75);
            thumbnail4.setHeightRatio(1.75);
        }

        public ArrayList<RatioLoadingImageView> getThumbnailViewList(){
            ArrayList<RatioLoadingImageView> thumbnailViewList = new ArrayList<>();
            thumbnailViewList.add(thumbnail1);
            thumbnailViewList.add(thumbnail2);
            thumbnailViewList.add(thumbnail3);
            thumbnailViewList.add(thumbnail4);
            return thumbnailViewList;
        }
    }
}
