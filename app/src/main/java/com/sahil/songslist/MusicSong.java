package com.sahil.songslist;

public class MusicSong {

    private String songTitle;
    private String songArtist;
    private String songData;


    public MusicSong(){

    }

    public MusicSong(String songTitle, String songArtist, String songData){

        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songData = songData;


    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongData() {
        return songData;
    }

    public void setSongData(String songData) {
        this.songData = songData;
    }
}
