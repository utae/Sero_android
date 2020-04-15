package net.videofactory.new_audi.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.ItemOfCard;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.RatioLoadingImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-13.
 */

public class SearchResultListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ItemOfCard> cardList;

    public SearchResultListAdapter(Context context, ArrayList<ItemOfCard> cardList) {
        this.context = context;
        this.cardList = cardList;
        this.inflater = LayoutInflater.from(context);
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
        ItemOfCard cardData = cardList.get(position);

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
                        ImagePickerTask thumbnailPickerTask = new ImagePickerTask(holder.profile);
                        LoadingDrawable loadingDrawable = new LoadingDrawable(R.drawable.ic_profile_default, thumbnailPickerTask);
                        holder.profile.setImageLoadingDrawable(loadingDrawable);
                        thumbnailPickerTask.execute(cardData.getProfileUrl());
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

        return convertView;
    }

    static class ViewHolder {

        @Bind(R.id.cardProfile) CircleLoadingImageView profile;

        @Bind(R.id.cardTitle) TextView title;

        @Bind(R.id.cardDelBtn) ImageButton delBtn;

        @Bind(R.id.cardVideoThumbnail1) RatioLoadingImageView thumbnail1;

        @Bind(R.id.cardVideoThumbnail2) RatioLoadingImageView thumbnail2;

        @Bind(R.id.cardVideoThumbnail3) RatioLoadingImageView thumbnail3;

        @Bind(R.id.cardVideoThumbnail4) RatioLoadingImageView thumbnail4;

        @Bind(R.id.cardVideoThumbnailContainer) LinearLayout thumbnailContainer;

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