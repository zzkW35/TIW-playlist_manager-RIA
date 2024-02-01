package it.polimi.tiw.playlistmanager.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.handlers.ConstructHandler;

import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Servlet implementation class CreateNewPlaylist
 */
@WebServlet("/CreateNewPlaylist")
public class CreateNewPlaylist extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateNewPlaylist() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        String playlistTitle;
        String[] songIds;
        User songUploader = (User) request.getSession().getAttribute("currentUser");
        int songUploaderId = songUploader.getId();
        int playlistId;

        try {
            playlistTitle = request.getParameter("playlistTitle");
            String songSelectionEncoded = request.getParameterValues("songSelection")[0];
            String songSelectionDecoded = java.net.URLDecoder.decode(songSelectionEncoded, UTF_8);
            songIds = gson.fromJson(songSelectionDecoded, String[].class);
            if (playlistTitle == null || playlistTitle.isEmpty() || songIds == null || songIds.length == 0) {
                throw new Exception("Missing or incorrect parameters");
            }
        } catch (Exception e) {
            String error = "Incorrect or missing parameters, error is: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        // Insert the playlist into the database
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        BinderDAO binderDAO = new BinderDAO(connection);
        try {
            playlistId = playlistDAO.createPlaylist(playlistTitle, songUploaderId);
            ConstructHandler.songListPlaylistBinder(binderDAO, response, songIds, playlistId);
        } catch (Exception e) {
            String error = "Could not create playlist: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        // Get user's ordered playlists to refresh the homepage
        List<Playlist> orderedUserPlaylists;
        try {
            orderedUserPlaylists = playlistDAO.findPlaylistsByUserIdOrderByCreationDateDesc(songUploaderId);
        } catch (Exception e) {
            String error = "Could not retrieve playlists: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }
//        HttpSession session = request.getSession();
//        session.setAttribute("orderedUserPlaylists", orderedUserPlaylists);
//        String homePath = "/WEB-INF/home.html";
//        forward(request, response, homePath, getServletContext(), templateEngine);

        String orderedUserPlaylistsJson = new Gson().toJson(orderedUserPlaylists);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(orderedUserPlaylistsJson);

    }
}
