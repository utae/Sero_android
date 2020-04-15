package net.videofactory.new_audi.camera_upload;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utae on 2015-11-29.
 */
public class VideoMerger extends AsyncTask<File, Void, Boolean>{

    private Activity activity;
    private File mergeVideo;
    private ProgressBar progressBar;
    private String duration;

    public VideoMerger(Activity activity, ProgressBar progressBar, String duration) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.duration = duration;
    }

    @Override
    protected Boolean doInBackground(File... params) {

        File dir = params[0];

        File[] files = dir.listFiles();

        ArrayList<Movie> movies = new ArrayList<>();

        List<Track> videoTracks = new LinkedList<>();
        List<Track> audioTracks = new LinkedList<>();

        try{
            for(File file : files){
                movies.add(MovieCreator.build(file.getPath()));
            }

            for(Movie movie : movies){
                for(Track track : movie.getTracks()){
                    if(track.getHandler().equals("vide")){
                        videoTracks.add(track);
                    }
                    if(track.getHandler().equals("soun")){
                        audioTracks.add(track);
                    }
                }
            }

            Movie merge = new Movie();

            if(audioTracks.size() > 0){
                merge.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            if(videoTracks.size() > 0){
                merge.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }

            Container container = new DefaultMp4Builder().build(merge);

            mergeVideo = new File(dir, dir.getName() + "_merge.mp4");

            FileChannel fileChannel = new FileOutputStream(mergeVideo).getChannel();

            container.writeContainer(fileChannel);

            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean sucess) {
        if(sucess){
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(activity, UploadFormActivity.class);
            intent.putExtra("videoPath", mergeVideo.getPath());
            intent.putExtra("duration", duration);
            activity.startActivity(intent);
        }else{
            //TODO merge실패 처리
        }
    }
}
