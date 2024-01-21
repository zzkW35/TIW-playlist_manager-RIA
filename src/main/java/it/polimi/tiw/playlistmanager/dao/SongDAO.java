package it.polimi.tiw.playlistmanager.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import it.polimi.tiw.playlistmanager.beans.Song;

public class SongDAO {

    private Connection connection;

    public SongDAO(Connection connection) {
        this.connection = connection;
    }

    public Song findSongById(int id) throws SQLException {
        String query = "SELECT * FROM song WHERE id = ?";
        Song song = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setTitle(resultSet.getString("title"));
                song.setCoverPath(resultSet.getString("cover_path"));
                song.setAlbum(resultSet.getString("album"));
                song.setArtist(resultSet.getString("artist"));
                song.setAlbumYear(resultSet.getInt("album_year"));
                song.setGenre(resultSet.getString("genre"));
                song.setFilePath(resultSet.getString("file_path"));
                song.setUploaderId(resultSet.getInt("uploader_id"));
            }
        } catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the song: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                throw new SQLException("Something went wrong while closing resultSet: " + e.getMessage());
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
        }
        return song;
    }

    public void addSong(String title, String coverPath, String album, String artist, int albumYear, String genre,
                        String filePath, int uploaderId) throws SQLException {
        String query = "INSERT INTO song (title, cover_path, album, artist, album_year, genre, file_path, uploader_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            // Disable auto-commit
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, coverPath);
            preparedStatement.setString(3, album);
            preparedStatement.setString(4, artist);
            preparedStatement.setInt(5, albumYear);
            preparedStatement.setString(6, genre);
            preparedStatement.setString(7, filePath);
            preparedStatement.setInt(8, uploaderId);

            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Something went wrong while adding the song: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                throw new SQLException("Something went wrong while closing preparedStatement: " + e.getMessage());
            }
            // Enable auto-commit back
            connection.setAutoCommit(true);
        }
    }
}
