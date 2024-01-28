package it.polimi.tiw.playlistmanager.beans;

import java.util.List;

public class UserData {
    private int userId;
    private String userName;
    private List<Playlist> playlists;
    private List<Song> songs;

    // constructor, getters and setters
    public UserData(User user, List<Playlist> playlists, List<Song> songs) {
        this.userId = user.getId();
        this.userName = user.getName();
        this.playlists = playlists;
        this.songs = songs;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public List<Song> getSongs() {
        return songs;
    }
}