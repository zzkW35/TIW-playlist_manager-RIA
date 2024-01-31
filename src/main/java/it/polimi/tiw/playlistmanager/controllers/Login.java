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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import it.polimi.tiw.playlistmanager.beans.Playlist;
import it.polimi.tiw.playlistmanager.beans.Song;
import it.polimi.tiw.playlistmanager.beans.UserData;
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;
import it.polimi.tiw.playlistmanager.dao.SongDAO;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.UserDAO;


/**
 * Servlet implementation class GoToHomePage
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        HttpSession session = request.getSession();

        // Check parameters
        boolean isLogged = session.getAttribute("currentUser") != null;
            if ((email == null || password == null || email.isEmpty() || password.isEmpty()) && !isLogged) {
            String error = "Missing login credentials";
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(error);
            return;
        }

        // Get user from the database
        UserDAO userDAO = new UserDAO(connection);
        User user;
        try {
            user = userDAO.findUser(email, password);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
            return;
        }

        // Get the playlists of the user from the database
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        List<Playlist> orderedUserPlaylists;
        try {
            orderedUserPlaylists = playlistDAO.findPlaylistsByUserIdOrderByCreationDateDesc(user.getId());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
            return;
        }

        // Get the songs of the user from the database
        SongDAO songDAO = new SongDAO(connection);
        List<Song> userSongs;
        try {
            userSongs = songDAO.findAllSongsByUserId(user.getId());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
            return;
        }

        // Send user, their playlists and their songs to the client
        session.setAttribute("currentUser", user);
        String userInfo = new Gson().toJson(new UserData(user, orderedUserPlaylists, userSongs));
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userInfo);
    }
}
