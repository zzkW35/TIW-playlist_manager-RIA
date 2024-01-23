package it.polimi.tiw.playlistmanager.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import it.polimi.tiw.playlistmanager.beans.Binder;

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
            if (binder == null) {
                throw new SQLException("Binder not found");
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
            if (binder == null) {
                throw new SQLException("Binder not found");
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
        String query = "INSERT INTO binder (playlist_id, song_id) VALUES (?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, songId);
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
}