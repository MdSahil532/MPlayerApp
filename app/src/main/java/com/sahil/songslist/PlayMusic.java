package com.sahil.songslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayMusic extends AppCompatActivity {

    ImageView shuffleAndRepeat,favourite;
    SeekBar proSeekBar, volumeSeekBar ;
    LinearLayout layout_L;
    ImageView playPause, nextSong,prevSong,songCoverImage;
    ImageView volumeOff;
    int position;
    TextView myWidget ;
    TextView elapsedTimeLabel, remainingTimeLabel;
    MediaPlayer mp;
    int totalTime;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        mp = new MediaPlayer();
        mp.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        myWidget = findViewById(R.id.widgetTextView);
        playPause = findViewById(R.id.play_pauseSong);
        volumeOff = findViewById(R.id.volume_down);
        nextSong = findViewById(R.id.nextSong);
        layout_L = findViewById(R.id.lLayout);
        songCoverImage = findViewById(R.id.songCoverImage);
        prevSong = findViewById(R.id.prevSong);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        proSeekBar = findViewById(R.id.positionBar);
        volumeSeekBar = findViewById(R.id.volumeBar);
        toolbar = findViewById(R.id.toolbarPlay);
        favourite = findViewById(R.id.favourite_Song);
        shuffleAndRepeat = findViewById(R.id.shuffleRepeatSong);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Now Playing");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic__keyboard_arrow_left));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        proSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.basicColor), PorterDuff.Mode.MULTIPLY);
        proSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.basicColor), PorterDuff.Mode.SRC_IN);
        volumeSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.basicColor), PorterDuff.Mode.MULTIPLY);
        volumeSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.basicColor), PorterDuff.Mode.SRC_IN);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            position = bundle.getInt("position");
            String title = bundle.getString("title");
            String data = bundle.getString("songData");
            myWidget.setText(title);
            myWidget.setSelected(true);
            Uri u = Uri.parse(data);
            Bitmap bp = getCoverImage(u);
            if (bp != null){
                songCoverImage.setImageBitmap(bp);
            }
            try {
                mp.setDataSource(this,u);
            } catch (Exception e) {
                Toast.makeText(this, "Cant play this song", Toast.LENGTH_SHORT).show();
            }
        }
        try {
            mp.prepare();
        }catch (Exception e){
            Toast.makeText(this, "Cant play this song", Toast.LENGTH_SHORT).show();
        }
        mp.start();
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setLooping(false);
        updateProcess();
        autoNextSong();



// Thread (Update positionBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                }
            }
        }).start();

    }


    public void autoNextSong(){
        if (!mp.isLooping()){
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp != null) {
                        mp.stop();
                        mp.reset();
                    }
                        position =((position+1)%MainActivity.songsList.size());
                        Uri uri = Uri.parse(MainActivity.songsList.get(position).getSongData());
                        try {
                            mp.setDataSource(PlayMusic.this, uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "cant play this song", Toast.LENGTH_SHORT).show();
                        }
                        myWidget.setText(MainActivity.songsList.get(position).getSongTitle());
                        Bitmap bp = getCoverImage(uri);
                        if (bp != null) {
                            songCoverImage.setImageBitmap(bp);
                        } else {
                            songCoverImage.setImageResource(R.drawable.mimage);
                        }
                        try {
                            mp.prepare();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "cant play this song", Toast.LENGTH_SHORT).show();
                        }
                        mp.start();
                        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                        mp.setLooping(false);
                        if (mp.isPlaying()) {
                            playPause.setImageResource(R.drawable.ic_pause_song);
                        }
                        updateProcess();
                        autoNextSong();
                    }
            });
        }
    }



    public void updateProcess(){
        mp.seekTo(0);
        mp.setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();
        proSeekBar.setMax(totalTime);
        proSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(progress);
                    proSeekBar.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumeNum = progress / 100f;
                mp.setVolume(volumeNum, volumeNum);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });


    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            proSeekBar.setProgress(currentPosition);
            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);
            String remainingTime = "- " + createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText(remainingTime);
            return true;
        }
    });

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }


    public void playPauseSong(View view){
        if (mp.isPlaying()){
            playPause.setImageResource(R.drawable.ic_play_song);
            mp.pause();
        }else{
            playPause.setImageResource(R.drawable.ic_pause_song);
            mp.start();
        }
    }

    public void repeatShuffle(View v){

        if (mp != null){
            if (!mp.isLooping()){
                shuffleAndRepeat.setImageResource(R.drawable.ic__repeat);
                mp.setLooping(true);
                shuffleAndRepeat.setTag(2);
            }else{
                shuffleAndRepeat.setImageResource(R.drawable.ic__shuffle);
                mp.setLooping(false);
                shuffleAndRepeat.setTag(1);
            }
        }
    }

    public void volumeOFF(View v){
        volumeSeekBar.setProgress(0);
    }
    public void volumeFull(View v){
        volumeSeekBar.setProgress(100);
    }


    public void  nextSong(View view){
        if (mp != null) {
            mp.stop();
            mp.reset();
        }
            position = ((position+1)%MainActivity.songsList.size());
            Uri uri = Uri.parse(MainActivity.songsList.get(position).getSongData());
            try {
                mp.setDataSource(PlayMusic.this, uri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "cant play this song", Toast.LENGTH_SHORT).show();
            }
            try {
                mp.prepare();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "cant play this song", Toast.LENGTH_SHORT).show();
            }
            mp.start();
            myWidget.setText(MainActivity.songsList.get(position).getSongTitle());
            Bitmap bp = getCoverImage(uri);
            if (bp != null) {
                songCoverImage.setImageBitmap(bp);
            } else {
                songCoverImage.setImageResource(R.drawable.mimage);
            }
            String tag = shuffleAndRepeat.getTag().toString();
            if (tag.equals("1")) {
                mp.setLooping(false);
            } else {
                mp.setLooping(true);
            }
            if (mp.isPlaying()) {
                playPause.setImageResource(R.drawable.ic_pause_song);
            }
            mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            updateProcess();
            autoNextSong();
    }



    public void prevSong(View v){
        if (mp != null) {
            mp.stop();
            mp.reset();
        }
            position = ((position - 1) < 0) ? (MainActivity.songsList.size() - 1) : (position - 1);
            Uri u = Uri.parse(MainActivity.songsList.get(position).getSongData());
            try {
                mp.setDataSource(PlayMusic.this, u);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Cant play this song", Toast.LENGTH_SHORT).show();
            }
            try {
                mp.prepare();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Cant play this song", Toast.LENGTH_SHORT).show();
            }
            mp.start();
            Bitmap bpm = getCoverImage(u);
            if (bpm != null) {
                songCoverImage.setImageBitmap(bpm);
            } else {
                songCoverImage.setImageResource(R.drawable.mimage);
            }
            myWidget.setText(MainActivity.songsList.get(position).getSongTitle());
            String tag = shuffleAndRepeat.getTag().toString();
            if (tag.equals("1")) {
                mp.setLooping(false);
            } else {
                mp.setLooping(true);
            }
            if (mp.isPlaying()) {
                playPause.setImageResource(R.drawable.ic_pause_song);
            }
            mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            updateProcess();
            autoNextSong();
    }

    public  void favSong(View v){
        String  tag = favourite.getTag().toString();
        if (tag.equals("1")){
            favourite.setImageResource(R.drawable.ic_favorite_cyan);
            favourite.setTag(2);
            Snackbar snackbar = Snackbar
                    .make(layout_L, "Added To Favourite List", Snackbar.LENGTH_SHORT);
            snackbar.setTextColor(getResources().getColor(R.color.basicColor));
            snackbar.setBackgroundTint(getResources().getColor(R.color.seekColor));
            snackbar.show();
        }else if (tag.equals("2")){
            favourite.setImageResource(R.drawable.ic_fav);
            favourite.setTag(1);
            Snackbar snackbar = Snackbar
                    .make(layout_L, "Remove From Favourite List", Snackbar.LENGTH_SHORT);
            snackbar.setTextColor(getResources().getColor(R.color.basicColor));
            snackbar.setBackgroundTint(getResources().getColor(R.color.seekColor));
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mp != null){
            if (mp.isPlaying()) {
                mp.stop();
            }
        }
    }
    public Bitmap getCoverImage(Uri uri){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art = null;
        BitmapFactory.Options bfo=new BitmapFactory.Options();
        try {
            mmr.setDataSource(String.valueOf(uri));
        } catch (RuntimeException ex) {
            return  null;
        }
        rawArt = mmr.getEmbeddedPicture();
        mmr.release();
        if (null != rawArt) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        }
        return art;
    }

}