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
import it.polimi.tiw.playlistmanager.dao.PlaylistDAO;
import it.polimi.tiw.playlistmanager.dao.SongDAO;
import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.UserDAO;

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.*;

/**
 * Servlet implementation class GoToHomePage
 */
@WebServlet("/GoToHomePage")
public class GoToHomePage extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHomePage() {
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
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Check parameters
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            String error = "Missing or incorrect login credentials";
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Get user from the database
        UserDAO userDAO = new UserDAO(connection);
        User user;
        try {
            user = userDAO.findUser(email, password);
        } catch (Exception e) {
            forwardToErrorPage(request, response, e.getMessage(), getServletContext(), templateEngine);
            return;
        }

        // Get the playlists of the user from the database
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        List<Playlist> orderedUserPlaylists;
        try {
            orderedUserPlaylists = playlistDAO.findPlaylistsByUserIdOrderByCreationDateDesc(user.getId());
        } catch (Exception e) {
            forwardToErrorPage(request, response, e.getMessage(), getServletContext(), templateEngine);
            return;
        }

        // Get the songs of the user from the database
        SongDAO songDAO = new SongDAO(connection);
        List<Song> userSongs;
        try {
            userSongs = songDAO.findAllSongsByUserId(user.getId());
        } catch (Exception e) {
            forwardToErrorPage(request, response, e.getMessage(), getServletContext(), templateEngine);
            return;
        }

        // Save user, their playlists and their songs in the session
        HttpSession session = request.getSession();
        session.setAttribute("currentUser", user);
        session.setAttribute("orderedUserPlaylists", orderedUserPlaylists);
        session.setAttribute("userSongs", userSongs);

        // Redirect to the Home page
        String homePath = "/WEB-INF/home.html";
        forward(request, response, homePath, getServletContext(), templateEngine);
    }
}
