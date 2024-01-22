package it.polimi.tiw.playlistmanager.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.playlistmanager.beans.Playlist;

public class PlaylistDAO {
    private Connection connection;

    public PlaylistDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method finds a playlist by its id
     * @param id the id of the playlist to find
     * @return the playlist with the given id
     * @throws SQLException if something goes wrong while searching for the playlist
     */
    public Playlist findPlaylistById(int id) throws SQLException {
        String query = "SELECT * FROM playlist WHERE id = ?";
        Playlist playlist = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                playlist = new Playlist();
                playlist.setId(resultSet.getInt("id"));
                playlist.setTitle(resultSet.getString("title"));
                playlist.setCreationDate(resultSet.getDate("creation_date"));
                playlist.setOwnerId(resultSet.getInt("owner_id"));
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the playlist: " + e.getMessage());
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
        return playlist;
    }

    /**
     * This method creates a new playlist
     * @param title the title of the playlist
     * @param ownerId the id of the user who owns the playlist
     * @throws SQLException if something goes wrong while creating the playlist
     */
    public void createPlaylist(String title, int ownerId) throws SQLException {
        String query = "INSERT INTO playlist (title, owner_id, creation_date) VALUES (?, ?, NOW())";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, ownerId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while creating the playlist: " + e.getMessage());
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
     * This method finds all the playlists owned by a user
     * @param userId the id of the user who owns the playlists
     * @return a list of all the playlists owned by the user
     * @throws SQLException if something goes wrong while searching for the playlists
     */
    public List<Playlist> findPlaylistsByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM playlist WHERE owner_id = ?";
        List<Playlist> playlists = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Playlist playlist = new Playlist();
                playlist.setId(resultSet.getInt("id"));
                playlist.setTitle(resultSet.getString("title"));
                playlist.setCreationDate(resultSet.getDate("creation_date"));
                playlist.setOwnerId(resultSet.getInt("owner_id"));
                playlists.add(playlist);
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the playlists: " + e.getMessage());
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
        return playlists;
    }
}
