package it.polimi.tiw.playlistmanager.controllers;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.beans.PlaylistData;
import it.polimi.tiw.playlistmanager.beans.Song;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;


/**
 * Servlet implementation class GoToPlaylistPage
 */
@WebServlet("/GoToPlaylistPage")
public class GoToPlaylistPage extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToPlaylistPage() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
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
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        // Parse the playlistId
        int playlistId;
        try {
            playlistId = Integer.parseInt(playlistIdString);
        } catch (NumberFormatException e) {
            String error = "PlaylistId is not an integer";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        // Get the playlist from the database
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        Playlist playlist;
        try {
            playlist = playlistDAO.findPlaylistById(playlistId);
        } catch (Exception e) {
            String error = "PlaylistId is not valid";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        // Find all the songs in the playlist
        BinderDAO binderDAO = new BinderDAO(connection);
        List<Song> songs;
        try {
            songs = binderDAO.findAllSongsByPlaylistId(playlistId);
        } catch (Exception e) {
            String error = "Something went wrong while retrieving the songs, details: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
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
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        if (playlist == null) {
            String error = "Playlist not found";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        // Send the playlist to the client
        String playlistInfo = new Gson().toJson(new PlaylistData(playlist, songs, songsNotInPlaylist));
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(playlistInfo);
    }
}
