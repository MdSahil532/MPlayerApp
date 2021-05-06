package com.sahil.songslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public  static  final  int REQUEST_CODE = 1;

    Toolbar toolbar ;
    TextView textViewR;

    static ArrayList<MusicSong> songsList ;
    private RecyclerView newRecyclerView;
    private  ShowAdapter showAdapter;
    ImageButton imageButton ;
    LinearLayout linear_Layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_show);
        imageButton = findViewById(R.id.exit_app);
        linear_Layout = findViewById(R.id.layout_linear);
        textViewR = findViewById(R.id.textViewResult);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("PlayList");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_music));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(linear_Layout, "Listen Great Musics!", Snackbar.LENGTH_SHORT);
                snackbar.setTextColor(getResources().getColor(R.color.basicColor));
                snackbar.setBackgroundTint(getResources().getColor(R.color.seekColor));
                snackbar.show();

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        songsList = new ArrayList<MusicSong>();
        newRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_songs);
        newRecyclerView.setHasFixedSize(true);
        newRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);

        }else {

          showSongList();

        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                Snackbar snackbar = Snackbar
                        .make(linear_Layout, "Permission Granted! Play and Enjoy Great Musics!", Snackbar.LENGTH_SHORT);
                snackbar.setTextColor(getResources().getColor(R.color.basicColor));
                snackbar.setBackgroundTint(getResources().getColor(R.color.seekColor));
                snackbar.show();
                showSongList();

            }else {
                Snackbar snackbar = Snackbar
                        .make(linear_Layout, "Permission Require! Pls Allow The Permission", Snackbar.LENGTH_SHORT);
                snackbar.setTextColor(getResources().getColor(R.color.basicColor));
                snackbar.setBackgroundTint(getResources().getColor(R.color.seekColor));
                snackbar.show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }

    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri,null,null,null,null);
        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {

                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentSongData = songCursor.getString(songData);
                if (!currentSongData.isEmpty() && !currentTitle.isEmpty()) {
                    MusicSong musicSong = new MusicSong(currentTitle, currentArtist, currentSongData);
                    songsList.add(musicSong);
                }

            }while (songCursor.moveToNext());

            songCursor.close();
            showAdapter = new ShowAdapter(MainActivity.this, songsList);
            newRecyclerView.setAdapter(showAdapter);
        }else {
            newRecyclerView.setVisibility(View.GONE);
            textViewR.setVisibility(View.VISIBLE);
        }

    }

    public void showSongList(){
        getMusic();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}