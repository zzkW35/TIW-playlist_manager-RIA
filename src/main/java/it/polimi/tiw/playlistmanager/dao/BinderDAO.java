package it.polimi.tiw.playlistmanager.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.playlistmanager.beans.Binder;
import it.polimi.tiw.playlistmanager.beans.Song;

public class BinderDAO {
private Connection connection;

    public BinderDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method finds a binder by its id
     * @param id the id of the binder to find
     * @return the binder with the given id
     * @throws SQLException if something goes wrong while searching for the binder
     */
    public Binder findBinderById(int id) throws SQLException {
        String query = "SELECT * FROM binder WHERE id = ?";
        Binder binder = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                binder = new Binder();
                binder.setId(resultSet.getInt("id"));
                binder.setPlaylistId(resultSet.getInt("playlist_id"));
                binder.setSongId(resultSet.getInt("song_id"));
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the binder: " + e.getMessage());
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing resultSet: " + e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
        return binder;
    }

    /**
     * This method finds a binder by its playlistId and songId
     * @param playlistId the playlistId of the binder to find
     * @param songId the songId of the binder to find
     * @return the binder with the given playlistId and songId
     * @throws SQLException if something goes wrong while searching for the binder
     */
    public Binder findBinderByPlaylistIdAndSongId(int playlistId, int songId) throws SQLException {
        String query = "SELECT * FROM binder WHERE playlist_id = ? AND song_id = ?";
        Binder binder = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, songId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                binder = new Binder();
                binder.setId(resultSet.getInt("id"));
                binder.setPlaylistId(resultSet.getInt("playlist_id"));
                binder.setSongId(resultSet.getInt("song_id"));
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the binder: " + e.getMessage());
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing resultSet: " + e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
        return binder;
    }

    /**
     * This method creates a new binder (playlist-song relation) if it doesn't already exist
     * @param playlistId the playlistId of the binder
     * @param songId the songId of the binder
     * @throws SQLException if something goes wrong while creating the binder
     */
    public void createBinder(int playlistId, int songId) throws SQLException {
        Binder binder = findBinderByPlaylistIdAndSongId(playlistId, songId);
        if (binder != null) {
            // TODO: maybe throw a custom exception?
            throw new SQLException("A binder with the same playlistId and songId already exists.");
        }
        String query = "INSERT INTO binder (playlist_id, song_id, song_position) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, songId);
            preparedStatement.setInt(3, getLastSongPosition(playlistId) + 1);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while creating the binder: " + e.getMessage());
        }
        finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
    }

    /**
     * This method finds all the songs of a playlist given its id
     * @param playlistId the id of the playlist
     * @return a list of songs of the playlist
     * @throws SQLException if something goes wrong while searching for the songs
     */
    public List<Song> findAllSongsByPlaylistId(int playlistId) throws SQLException {
//        String query = "SELECT * FROM song INNER JOIN binder ON song.id = binder.song_id WHERE binder.playlist_id = ?";
        String query = "SELECT song.*, binder.song_position FROM song INNER JOIN binder ON song.id = binder.song_id WHERE binder.playlist_id = ?";
        List<Song> songs;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, playlistId);
            resultSet = preparedStatement.executeQuery();
            songs = new ArrayList<>();

            while (resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setTitle(resultSet.getString("title"));
                song.setCoverPath(resultSet.getString("cover_path"));
                song.setAlbum(resultSet.getString("album"));
                song.setArtist(resultSet.getString("artist"));
                song.setAlbumYear(resultSet.getInt("album_year"));
                song.setGenre(resultSet.getString("genre"));
                song.setFilePath(resultSet.getString("file_path"));
                song.setUploaderId(resultSet.getInt("uploader_id"));
                song.setPosition(resultSet.getInt("song_position"));
                songs.add(song);
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the songs: " + e.getMessage());
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing resultSet: " + e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
        return songs;
    }

    /**
     * This method finds all the songs of a user that aren't in a playlist given its id
     * @param playlistId the id of the playlist
     * @param userId the id of the user who created the playlist
     * @return a list of songs that aren't in the playlist
     * @throws SQLException if something goes wrong while searching for the songs
     */
    public List<Song> findAllSongsOfUserNotInPlaylist(int playlistId, int userId) throws SQLException {
        String query = "SELECT * FROM song WHERE uploader_id = ? AND id NOT IN (SELECT song_id FROM binder WHERE playlist_id = ?)";
        List<Song> songs;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, playlistId);
            resultSet = preparedStatement.executeQuery();
            songs = new ArrayList<>();

            while (resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setTitle(resultSet.getString("title"));
                song.setCoverPath(resultSet.getString("cover_path"));
                song.setAlbum(resultSet.getString("album"));
                song.setArtist(resultSet.getString("artist"));
                song.setAlbumYear(resultSet.getInt("album_year"));
                song.setGenre(resultSet.getString("genre"));
                song.setFilePath(resultSet.getString("file_path"));
                song.setUploaderId(resultSet.getInt("uploader_id"));
                songs.add(song);
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the songs: " + e.getMessage());
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing resultSet: " + e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
        return songs;
    }

    /**
     * This method returns the last song position of a playlist given its id
     * @param playlistId the id of the playlist
     * @return the last song position of the playlist
     * @throws SQLException if something goes wrong while searching for the last song position
     */
    public int getLastSongPosition(int playlistId) throws SQLException {
        String query = "SELECT MAX(song_position) FROM binder WHERE playlist_id = ?";
        int lastSongPosition = 0;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, playlistId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                lastSongPosition = resultSet.getInt("MAX(song_position)");
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the last song position: " + e.getMessage());
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing resultSet: " + e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
        return lastSongPosition;
    }

    // Reorder the songs of a playlist from a JSON array
    public void reorderSongs(int playlistId, String[] songIds) throws SQLException {
        String query = "UPDATE binder SET song_position = ? WHERE playlist_id = ? AND song_id = ?";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < songIds.length; i++) {
                preparedStatement.setInt(1, i + 1);
                preparedStatement.setInt(2, playlistId);
                preparedStatement.setInt(3, Integer.parseInt(songIds[i]));
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while reordering the songs: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            throw new SQLException("Something went wrong while reordering the songs: " + e.getMessage());
        }
        finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
    }

    // Get the son_position

}