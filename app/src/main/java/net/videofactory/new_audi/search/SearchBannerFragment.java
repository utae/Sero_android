package net.videofactory.new_audi.search;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-05-31.
 */
public class SearchBannerFragment extends Fragment {

    private String imgUrl;
    private String bannerLink;

    @Bind(R.id.searchBannerImageView) LoadingImageView bannerImageView;

    public static SearchBannerFragment create(String imgUrl, String bannerLink){
        SearchBannerFragment searchBannerFragment = new SearchBannerFragment();
        Bundle args = new Bundle();
        args.putString("imgUrl", imgUrl);
        args.putString("bannerLink", bannerLink);
        searchBannerFragment.setArguments(args);
        return searchBannerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgUrl = getArguments().getString("imgUrl");
        bannerLink = getArguments().getString("bannerLink");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_search_banner, container, false);

        ButterKnife.bind(this, view);

        if(Utilities.cancelPotentialTask(imgUrl, bannerImageView)){
            ImagePickerTask imagePickerTask = new ImagePickerTask(bannerImageView);
            bannerImageView.setImageLoadingDrawable(new LoadingDrawable(new ColorDrawable(Color.BLACK), imagePickerTask));
            imagePickerTask.execute(imgUrl);
        }

        bannerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bannerLink));
                startActivity(intent);
            }
        });

        return view;
    }
}
