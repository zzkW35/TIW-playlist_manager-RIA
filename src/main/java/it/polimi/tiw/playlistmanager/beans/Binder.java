package it.polimi.tiw.playlistmanager.beans;

public class Binder {
    private int id;
    private int playlistId;
    private int songId;

    private int songPosition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getPosition() {
        return songPosition;
    }

    public void setPosition(int position) {
        this.songPosition = position;
    }
}
