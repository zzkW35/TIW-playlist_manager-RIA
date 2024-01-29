package it.polimi.tiw.playlistmanager.controllers;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.playlistmanager.beans.Song;
import it.polimi.tiw.playlistmanager.dao.SongDAO;
import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;

import it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler;

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.*;

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
        // Get songId and extract the song from the database
        int songId;
        Song song;
        try {
            songId = Integer.parseInt(request.getParameter("songId"));
        } catch (Exception e) {
            String error = "Incorrect or missing song parameters, details: " + e.getMessage();
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }
        SongDAO songDAO = new SongDAO(connection);
        try {
            song = songDAO.findSongById(songId);
        } catch (Exception e) {
            String error = "Incorrect or missing song parameters, details: " + e.getMessage();
            forwardToErrorPage(request, response, error, getServletContext(), templateEngine);
            return;
        }

        // Save the song in the session
        HttpSession session = request.getSession();
        session.setAttribute("selectedSong", song);

        // Redirect to the player page
        String playerPath = "/WEB-INF/player.html";
        forward(request, response, playerPath, getServletContext(), templateEngine);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
