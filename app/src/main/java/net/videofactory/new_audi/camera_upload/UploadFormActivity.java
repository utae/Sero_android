package net.videofactory.new_audi.camera_upload;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import net.videofactory.new_audi.R;
import net.videofactory.new_audi.common.Utilities;
import net.videofactory.new_audi.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Utae on 2016-07-01.
 */
public class UploadFormActivity extends AppCompatActivity {

    private String videoPath;
    private String duration;

    @Bind(R.id.uploadFormBackButton) ImageButton backButton;
    @Bind(R.id.uploadFormEditText) EditText editText;
    @Bind(R.id.uploadFormSaveToMyDevice) ImageButton saveToMyDevice;
    @Bind(R.id.uploadFormUpload) Button uploadButton;

    private ArrayList<String> hashtagList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_form);

        ButterKnife.bind(this);

        videoPath = getIntent().getStringExtra("videoPath");
        duration = getIntent().getStringExtra("duration");

        init();
    }

    private void init(){
        hashtagList = new ArrayList<>();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();
            }
        });
    }

    private void uploadVideo(){
        if(checkHashtag(editText.getText().toString().trim())){
            File input = new File(videoPath);
            Intent serviceIntent = new Intent(this, VideoEncodeUploadService.class);
            serviceIntent.putExtra("inputFile", videoPath);
            serviceIntent.putExtra("outputFile", getOutputFileName(input.getName()));
            serviceIntent.putExtra("startTime", "0");
            serviceIntent.putExtra("duration", duration);
            serviceIntent.putExtra("text", editText.getText().toString().trim());
            serviceIntent.putExtra("hashtagList", hashtagList);
            startService(serviceIntent);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            Toast.makeText(this, "You Should put at least one hashtag.", Toast.LENGTH_LONG).show();
        }
    }

    private String getOutputFileName(String fileName){
        File dir = new File(getExternalFilesDir(null) + "/" + Utilities.transTimeFormatFromTimeMillis(System.currentTimeMillis()));
        if(!dir.exists()){
            dir.mkdir();
        }
        return dir.getPath() + "/final_" + fileName;
    }

    private boolean checkHashtag(String text){
        hashtagList = Utilities.getSpanStrings(text, '#');
        return hashtagList.size() != 0;
    }
}
