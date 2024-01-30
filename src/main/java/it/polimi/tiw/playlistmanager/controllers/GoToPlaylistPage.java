package it.polimi.tiw.playlistmanager.controllers;

import java.io.IOException;
import java.io.Serial;
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
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;
import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.*;

/**
 * Servlet implementation class GoToPlaylistPage
 */
@WebServlet("/GoToPlaylistPage")
public class GoToPlaylistPage extends HttpServlet {
    @Serial
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
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (playlistIdString == null || playlistIdString.isEmpty()) {
            String error = "Missing playlistId";
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Parse the playlistId
        int playlistId;
        try {
            playlistId = Integer.parseInt(playlistIdString);
        } catch (NumberFormatException e) {
            String error = "PlaylistId is not an integer";
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Get the playlist from the database
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        Playlist playlist;
        try {
            playlist = playlistDAO.findPlaylistById(playlistId);
        } catch (Exception e) {
            String error = "PlaylistId is not valid";
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Find all the songs in the playlist
        BinderDAO binderDAO = new BinderDAO(connection);
        List<Song> songs;
        try {
            songs = binderDAO.findAllSongsByPlaylistId(playlistId);
        } catch (Exception e) {
            String error = "Something went wrong while retrieving the songs, details: " + e.getMessage();
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }
        // Order songs by their albumYear
        songs.sort((s1, s2) -> s2.getAlbumYear() - s1.getAlbumYear());

        // Get songs not in the playlist
        List<Song> songsNotInPlaylist;
        try {
            songsNotInPlaylist = binderDAO.findAllSongsOfUserNotInPlaylist(playlistId, currentUser.getId());
        } catch (Exception e) {
            String error = "Something went wrong while retrieving the songs, details: " + e.getMessage();
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        if (playlist == null) {
            String error = "Playlist not found";
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Set the parameters for the pagination
        if (songs.size() > 5) {
            request.getSession().setAttribute("hasNextPage", 1);
        } else {
            request.getSession().setAttribute("hasNextPage", 0);
        }
        request.getSession().setAttribute("currentPage", 1);

        // Add the playlist and the songs to the parameters and redirect to the playlist page
        HttpSession session = request.getSession();
        session.setAttribute("playlist", playlist);
        session.setAttribute("playlistTitle", playlist.getTitle());
        session.setAttribute("songs", songs); //Full list of songs
        session.setAttribute("trimmedSongList", songs); //List of songs to be displayed
        session.setAttribute("songIndex", songs.size());
        session.setAttribute("songsNotInPlaylist", songsNotInPlaylist);
        String playlistPath = "/WEB-INF/playlist.html";
        forward(request, response, playlistPath, getServletContext(), templateEngine);
    }
}
