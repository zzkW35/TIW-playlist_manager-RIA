package it.polimi.tiw.playlistmanager.beans;

import java.util.List;

public class PlaylistData {
    private int playlistId;
    private String playlistTitle;
    private List<Song> songs;
    private List<Song> songsNotInPlaylist;

    // Constructor and getters
    public PlaylistData(Playlist playlist, List<Song> songs, List<Song> songsNotInPlaylist) {
        this.playlistId = playlist.getId();
        this.playlistTitle = playlist.getTitle();
        this.songs = songs;
        this.songsNotInPlaylist = songsNotInPlaylist;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Song> getSongsNotInPlaylist() {
        return songsNotInPlaylist;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setSongsNotInPlaylist(List<Song> songsNotInPlaylist) {
        this.songsNotInPlaylist = songsNotInPlaylist;
    }

}
