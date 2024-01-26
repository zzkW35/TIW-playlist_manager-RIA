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
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.UserDAO;

import it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler;

/**
 * Servlet implementation class GoToPlayerPage
 */
@WebServlet("/GoToPlayerPage")
public class GoToPlayerPage extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToPlayerPage() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        this.templateEngine = ThymeleafHandler.handler(servletContext);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get songId and exctract the song from the database
        int songId;
        Song song;
        try {
            songId = Integer.parseInt(request.getParameter("songId"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters, error is: " + e.getMessage());
            return;
        }
        SongDAO songDAO = new SongDAO(connection);
        try {
            song = songDAO.findSongById(songId);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters, error is: " + e.getMessage());
            return;
        }

        // Save the song in the session
        HttpSession session = request.getSession();
        session.setAttribute("selectedSong", song);

        // Redirect to the player page
        String path = "/WEB-INF/player.html";
        forward(request, response, path);

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }
}
