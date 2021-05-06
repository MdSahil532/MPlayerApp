package com.sahil.songslist;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {

    LayoutInflater inflater;
    private Context context;
    ArrayList<MusicSong> songsList ;


    public ShowAdapter(Context context, ArrayList<MusicSong> songsList){

        this.context = context;
        this.songsList = songsList;
        this.inflater = LayoutInflater.from(context);

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_songlist,parent,false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        MusicSong songMusic = songsList.get(position);
        final String titleSong = songMusic.getSongTitle();
        final String dataOfSong = songMusic.getSongData();
        holder.titleTextView.setText(titleSong);
        holder.titleTextView.setSelected(true);
        String artist = "Artist : "+ songMusic.getSongArtist();
        Uri uri = Uri.parse(dataOfSong);
        Bitmap bp = getCoverImage(uri);
        if (bp != null) {
            holder.coverImage.setImageBitmap(bp);
        }else{
            holder.coverImage.setImageResource(R.drawable.mimage);
        }

        holder.artistTextView.setText(artist);
        holder.playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PlayMusic.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position",position);
                bundle.putString("title",titleSong);
                bundle.putString("songData",dataOfSong);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

    }



    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView coverImage, playImage;
        TextView titleTextView , artistTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            coverImage = (ImageView)itemView.findViewById(R.id.cover_image);
            playImage = itemView.findViewById(R.id.playSong);
            titleTextView = itemView.findViewById(R.id.album_titleText);
            artistTextView = itemView.findViewById(R.id.album_artistText);
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
            return null;
        }
        rawArt = mmr.getEmbeddedPicture();
        mmr.release();
        if (null != rawArt) {

            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        }
        return art;
    }


}
