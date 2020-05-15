package net.videofactory.new_audi.camera_upload;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import net.videofactory.new_audi.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-06-30.
 */
public class GalleryActivity extends AppCompatActivity {

    @BindView(R.id.galleryBackButton) ImageButton backButton;
    @BindView(R.id.galleryThumbnailList) GridView thumbnailListView;

    private GalleryAdapter galleryAdapter;

    private final int AUDI_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        init();
    }

    private void init(){
        galleryAdapter = new GalleryAdapter(this, getVideoInfoList());

        thumbnailListView.setAdapter(galleryAdapter);

        thumbnailListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        thumbnailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getAdapter().getItem(position) instanceof ItemOfGallery){
                    ItemOfGallery itemOfGallery = (ItemOfGallery)parent.getAdapter().getItem(position);
                    Intent intent = new Intent(GalleryActivity.this, UploadFormActivity.class);
                    intent.putExtra("videoPath", itemOfGallery.getData());
                    intent.putExtra("duration", itemOfGallery.getDuration());
                    startActivity(intent);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private ArrayList<ItemOfGallery> getVideoInfoList(){
        ArrayList<ItemOfGallery> videoInfoList = new ArrayList<>();
        String data, height, width, rotation, duration;
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor videoCursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null, null);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        if(videoCursor != null){
            while (videoCursor.moveToNext()){
                data = videoCursor.getString(0);
                try{
                    mediaMetadataRetriever.setDataSource(data);
                }catch (IllegalArgumentException e){
                    continue;
                }

                height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                videoInfoList.add(0, new ItemOfGallery(data, height, width, rotation, duration));
            }
            videoCursor.close();
        }
        return videoInfoList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case AUDI_PERMISSIONS_REQUEST_EXTERNAL_STORAGE :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    //TODO PERMISSION DENIED
                    finish();
                }
                break;
        }
    }
}
