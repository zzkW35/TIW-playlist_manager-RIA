package it.polimi.tiw.playlistmanager.controllers;

import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;
import it.polimi.tiw.playlistmanager.dao.SongDAO;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddSongToPlaylist
 */
@WebServlet("/AddSongToExistingPlaylist")
public class AddSongToExistingPlaylist extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddSongToExistingPlaylist() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        this.templateEngine = ThymeleafHandler.handler(servletContext);
    }
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // Handle GET request
//        // For example, you might want to display a form to the user
//        String path = "/WEB-INF/playlist.html";
//        forward(request, response, path);
//    }
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] songIds;
        User songUploader = (User) request.getSession().getAttribute("currentUser");
        Playlist playlist = (Playlist) request.getSession().getAttribute("playlist");
        String playlistTitle = playlist.getTitle();
        int playlistID = playlist.getId();
        int songUploaderId = songUploader.getId();

        try {
//            playlistTitle = request.getSession("playlistTitle");
            songIds = request.getParameterValues("songSelection");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters, error is: " + e.getMessage());
            return;
        }

        // Create the binder between the playlist and the songs
        BinderDAO binderDAO = new BinderDAO(connection);
        SongDAO songDAO = new SongDAO(connection);
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

        response.sendRedirect("/WEB-INF/playlist.html");

    }
    private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }
}
