package it.polimi.tiw.playlistmanager.controllers;

import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.handlers.ConstructHandler;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.handler;
import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.forwardToErrorPage;

/**
 * Servlet implementation class AddSongToPlaylist
 */
@WebServlet("/AddSongToPlaylist")
public class AddSongToPlaylist extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private ConstructHandler constructHandler;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddSongToPlaylist() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] songIds;
        Playlist playlist = (Playlist) request.getSession().getAttribute("playlist");
        int playlistID = playlist.getId();

        try {
            songIds = request.getParameterValues("songSelection");
        } catch (Exception e) {
            String error = "Incorrect or missing parameters, error is: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        // Create the binder between the playlist and the songs
        BinderDAO binderDAO = new BinderDAO(connection);
        try {
            ConstructHandler.songListPlaylistBinder(binderDAO, response, songIds, playlistID);
        } catch (Exception e) {
            String error = "Error in creating the binder, error is: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }
//        String playlistIdString = Integer.toString(playlistID);
//        response.sendRedirect(getServletContext().getContextPath() + "/GoToPlaylistPage?playlistId=" + playlistIdString);

        // Send the updated song list to the client




    }
}
