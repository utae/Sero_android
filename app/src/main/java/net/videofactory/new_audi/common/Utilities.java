package net.videofactory.new_audi.common;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.videofactory.new_audi.BuildConfig;
import net.videofactory.new_audi.async.ImagePickerTask;
import net.videofactory.new_audi.custom_view.loading_image_view.CircleLoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingDrawable;
import net.videofactory.new_audi.custom_view.loading_image_view.LoadingImageView;
import net.videofactory.new_audi.custom_view.loading_image_view.RatioLoadingImageView;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 옥지수 on 2015-11-07.
 */

public class Utilities {

    public static void logD (String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static long getCurrentTimeMinutes(){
        return System.currentTimeMillis()/60000;
    }

    public static long transMinutesToTimeMillis(long minutes){
        return TimeUnit.MINUTES.toMillis(minutes);
    }

    public static String transTimeFormatFromTimeMillis(long timeMillis){
        return transTimeFormatFromTimeMillis(timeMillis, null);
    }

    public static String transTimeFormatFromTimeMillis(long timeMillis, String dateFormat){
        SimpleDateFormat simpleDateFormat;
        if(dateFormat == null){
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        }else{
            simpleDateFormat = new SimpleDateFormat(dateFormat);
        }
        String newTimeFormat = simpleDateFormat.format(new Date(timeMillis));
        return newTimeFormat;
    }

    public static float getDip(Context context, int value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPassword(CharSequence password){
        String Passwrod_PATTERN = "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{6,20}$";
        Pattern pattern = Pattern.compile(Passwrod_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static JsonNode jsonParse(String data){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jsonFactory = mapper.getFactory();
            return mapper.readTree(jsonFactory.createParser(data));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String SHA256(String msg){
        StringBuffer sb = new StringBuffer();

        String data = "";
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(msg.getBytes());
            byte[] mdBytes = md.digest();

            data = byteAsString(mdBytes);

            return data;
        }catch (Exception localNoSuchAlgorithmException) {
            localNoSuchAlgorithmException.printStackTrace();
        }
        return data;
    }

    public static String byteAsString(byte[] dataBytes) throws Exception{
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < dataBytes.length; i++) {
            String hex = Integer.toHexString(0xFF & dataBytes[i]);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    public static void hideKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public static ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }

        return  spans;
    }

    public static ArrayList<String> getSpanStrings(String body, char prefix) {
        ArrayList<String> spans = new ArrayList<>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            spans.add(matcher.group());
        }

        return spans;
    }

    static public boolean cancelPotentialTask(String imagePath, ImageView imageView){
        ImagePickerTask imagePickerTask = getImagePickerTask(imageView);
        if(imagePickerTask != null){
            String taskImageUrl = imagePickerTask.getImageUrl();
            if( (taskImageUrl == null) || (!taskImageUrl.equals(imagePath))){
                imagePickerTask.cancel(true);
            }else{
                return false;
            }
        }
        return true;
    }

    static public ImagePickerTask getImagePickerTask(ImageView imageView){
        if(imageView != null){
            LoadingDrawable loadingDrawable = null;
            if(imageView instanceof LoadingImageView){
                LoadingImageView loadingImageView = (LoadingImageView) imageView;
                loadingDrawable = loadingImageView.getLoadingDrawable();
            }else if(imageView instanceof RatioLoadingImageView){
                RatioLoadingImageView ratioLoadingImageView = (RatioLoadingImageView) imageView;
                loadingDrawable = ratioLoadingImageView.getLoadingDrawable();
            }else if(imageView instanceof CircleLoadingImageView){
                CircleLoadingImageView circleLoadingImageView = (CircleLoadingImageView) imageView;
                loadingDrawable = circleLoadingImageView.getLoadingDrawable();
            }
            if(loadingDrawable != null){
                return loadingDrawable.getImagePickerTask();
            }
        }
        return null;
    }

    static public BitmapFactory.Options getBitmapSize(File imageFile){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        return options;
    }
}
