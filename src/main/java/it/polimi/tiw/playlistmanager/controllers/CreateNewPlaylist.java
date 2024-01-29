package it.polimi.tiw.playlistmanager.controllers;

import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.handlers.ConstructHandler;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.*;

/**
 * Servlet implementation class CreateNewPlaylist
 */
@WebServlet("/CreateNewPlaylist")
public class CreateNewPlaylist extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateNewPlaylist() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        this.templateEngine = handler(servletContext);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playlistTitle;
        String[] songIds;
        User songUploader = (User) request.getSession().getAttribute("currentUser");
        int songUploaderId = songUploader.getId();
        int playlistId;

        try {
            playlistTitle = request.getParameter("playlistTitle");
            songIds = request.getParameterValues("songSelection");
        } catch (Exception e) {
            String error = "Incorrect or missing parameters, error is: " + e.getMessage();
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Insert the playlist into the database
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        try {
            playlistId = playlistDAO.createPlaylist(playlistTitle, songUploaderId);
        } catch (Exception e) {
            String error = "Could not create playlist: " + e.getMessage();
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Create the binder between the playlist and the songs
        BinderDAO binderDAO = new BinderDAO(connection);
        try {
            ConstructHandler.songListPlaylistBinder(binderDAO, response, songIds, playlistId);
        } catch (SQLException e) {
            forwardToErrorPage(request, response, e.getMessage(), getServletContext(), templateEngine);
            throw new RuntimeException(e);
        }

        // Get user's ordered playlists to refresh the homepage
        List<Playlist> orderedUserPlaylists;
        try {
            orderedUserPlaylists = playlistDAO.findPlaylistsByUserIdOrderByCreationDateDesc(songUploaderId);
        } catch (Exception e) {
            String error = "Could not retrieve playlists: " + e.getMessage();
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }
        HttpSession session = request.getSession();
        session.setAttribute("orderedUserPlaylists", orderedUserPlaylists);
        String homePath = "/WEB-INF/home.html";
        forward(request, response, homePath, getServletContext(), templateEngine);
    }
}
