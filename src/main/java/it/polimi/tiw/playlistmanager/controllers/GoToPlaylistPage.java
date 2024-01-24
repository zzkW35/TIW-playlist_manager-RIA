package it.polimi.tiw.playlistmanager.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.beans.Song;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.handler;

/**
 * Servlet implementation class GoToPlaylistPage
 */
@WebServlet("/GoToPlaylistPage")
public class GoToPlaylistPage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToPlaylistPage() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        this.templateEngine = handler(servletContext);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get the playlistId from the request
        String playlistIdString = request.getParameter("playlistId");
        if (playlistIdString == null || playlistIdString.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing playlistId");
            return;
        }

        // Parse the playlistId
        int playlistId;
        try {
            playlistId = Integer.parseInt(playlistIdString);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "PlaylistId is not an integer");
            return;
        }

        // Get the playlist from the database
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        Playlist playlist;
        try {
            playlist = playlistDAO.findPlaylistById(playlistId);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "PlaylistId is not valid");
            return;
        }

        // Find all the songs in the playlist
        BinderDAO binderDAO = new BinderDAO(connection);
        List<Song> songs;
        try {
            songs = binderDAO.findAllSongsByPlaylistId(playlistId);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Something went wrong while retrieving the songs: " + e.getMessage());
            return;
        }

        // Get the first 5 songs
        List<Song> trimmedSongList = songs.subList(0, Math.min(5, songs.size()));

        // Add the playlist and the songs to the parameters and redirect to the playlist page
        HttpSession session = request.getSession();
        session.setAttribute("playlist", playlist);
        session.setAttribute("songs", songs);
        session.setAttribute("trimmedSongList", trimmedSongList);
        session.setAttribute("songIndex", 5);
        String playlistPath = "/WEB-INF/playlist.html";
        forward(request, response, playlistPath);
    }

//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // TODO Auto-generated method stub
//        doGet(request, response);
//    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }
}
