package com.sahil.songslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicSplasScreen extends AppCompatActivity {

    ImageView musicLogo;
    TextView musicBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_splas_screen);
        musicLogo = findViewById(R.id.musicPlayerImage);
        musicBrand = findViewById(R.id.musicPlayerName);
        musicBrand.animate().alpha(1).setDuration(2000);
        musicLogo.animate().alpha(1).setDuration(2500);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MusicSplasScreen.this,MainActivity.class));
                finish();

            }
        },   3500);

    }
}