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

import com.google.gson.Gson;
import it.polimi.tiw.playlistmanager.dao.BinderDAO;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Servlet implementation class SortSongs
 */
@WebServlet("/SortSongs")
public class SortSongs extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection = null;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public SortSongs() {
        super();
    }

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        connection = ConnectionHandler.getConnection(getServletContext());

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] songIds;
        int playlistId;
        Gson gson = new Gson();

        try {
            String songSelectionEncoded = request.getParameterValues("newOrder")[0];
            String songSelectionDecoded = java.net.URLDecoder.decode(songSelectionEncoded, UTF_8);
            songIds = gson.fromJson(songSelectionDecoded, String[].class);
            playlistId = Integer.parseInt(request.getParameter("playlistId"));
        } catch (Exception e) {
            String error = "Incorrect or missing parameters, error is: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }

        BinderDAO binderDAO = new BinderDAO(connection);
        try {
            binderDAO.reorderSongs(playlistId, songIds);
        } catch (Exception e) {
            String error = "Could not sort songs, error is: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(error);
            return;
        }
        // Send the updated song list to the client
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");



    }
}
