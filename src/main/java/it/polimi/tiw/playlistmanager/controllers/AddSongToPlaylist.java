package it.polimi.tiw.playlistmanager.controllers;

import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler;
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

/**
 * Servlet implementation class AddSongToPlaylist
 */
@WebServlet("/AddSongToPlaylist")
public class AddSongToPlaylist extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    private ConstructHandler constructHandler;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddSongToPlaylist() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        this.templateEngine = ThymeleafHandler.handler(servletContext);
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters, error is: " + e.getMessage());
            return;
        }

        // Create the binder between the playlist and the songs
        BinderDAO binderDAO = new BinderDAO(connection);
        try {
            ConstructHandler.songListPlaylistBinder(binderDAO, response, songIds, playlistID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String playlistIdString = Integer.toString(playlistID);
        request.getSession().setAttribute("currentPage", 1);
        response.sendRedirect(getServletContext().getContextPath() + "/GoToPlaylistPage?playlistId=" + playlistIdString);
    }
}
