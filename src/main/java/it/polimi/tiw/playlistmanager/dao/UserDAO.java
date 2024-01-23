package it.polimi.tiw.playlistmanager.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import it.polimi.tiw.playlistmanager.beans.User;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method finds a user by its email and password
     * @param email the email of the user to find
     * @param password the password of the user to find
     * @return the user with the given email and password
     * @throws SQLException if something goes wrong while searching for the user
     */
    public User findUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        User user = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
            }
            if (user == null) {
                throw new SQLException("User not found");
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the user: " + e.getMessage());
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
        return user;
    }

    /**
     * This method finds a user by its id
     * @param id the id of the user to find
     * @return the user with the given id
     * @throws SQLException if something goes wrong while searching for the user
     */
    public User findUserById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        User user = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
            }
            if (user == null) {
                throw new SQLException("User not found");
            }
        }
        catch (SQLException e) {
            throw new SQLException("Something went wrong while searching for the user: " + e.getMessage());
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
        return user;
    }
}