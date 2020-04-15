package net.videofactory.new_audi.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.widget.ImageView;

import net.videofactory.new_audi.common.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Utae on 2016-01-15.
 */
public class ImagePickerTask extends AsyncTask<String, Integer, Bitmap> {

    private final WeakReference<ImageView> imageViewWeakReference;

    private String imagePath;

    @IdRes private int errorImgIdRes = 0;

    public ImagePickerTask(ImageView imageView) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    public String getImageUrl(){
        return imagePath;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmap = null;
        imagePath = urls[0];
        try {
            URL imageUrl = new URL(imagePath);
            HttpURLConnection connection = (HttpURLConnection)imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            Utilities.logD("Test", "ImagePickerTask err : " + e.toString());
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()){
            bitmap = null;
        }

        ImageView imageView = imageViewWeakReference.get();
        ImagePickerTask imagePickerTask = Utilities.getImagePickerTask(imageView);
        if(this == imagePickerTask){
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }else if(errorImgIdRes != 0){
                imageView.setImageResource(errorImgIdRes);
            }
        }
    }

    public void setErrorImgIdRes(int errorImgIdRes) {
        this.errorImgIdRes = errorImgIdRes;
    }
}
