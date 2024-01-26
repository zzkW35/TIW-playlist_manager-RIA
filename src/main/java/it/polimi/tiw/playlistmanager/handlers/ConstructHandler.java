package it.polimi.tiw.playlistmanager.handlers;

import it.polimi.tiw.playlistmanager.dao.BinderDAO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ConstructHandler {

    public static void songListPlaylistBinder(BinderDAO binderDAO, HttpServletResponse response, String[] songIds,
                                              int playlistID) throws IOException, SQLException {
        for (String songIdStr : songIds) {
            try {
                int songId = Integer.parseInt(songIdStr);
                binderDAO.createBinder(playlistID, songId);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid song ID: " + songIdStr);
                return;
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
                return;
            }
        }

    }
}
