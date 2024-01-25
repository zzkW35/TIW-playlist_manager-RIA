package it.polimi.tiw.playlistmanager.controllers;

import it.polimi.tiw.playlistmanager.beans.Song;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

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

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.handler;

/**
 * Servlet implementation class BrowseSongs
 */
@WebServlet("/BrowseSongs")
public class BrowseSongs extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseSongs() {
        super();
    }

	public void init() throws ServletException {
//		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		this.templateEngine = handler(servletContext);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());

		// Get the song list from the request
		HttpSession session = request.getSession();
		List<Song> songs = (List<Song>) session.getAttribute("songs");
		if (songs == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing songs");
			return;
		}

		int songIndex = (int) session.getAttribute("songIndex");
		int updatedSongIndex = songIndex;
		System.out.println("songindex: " + songIndex + " updatedSongIndex: " + updatedSongIndex);

		// Check if the user wants to go to the next song or to the previous one
		String direction = request.getParameter("direction");
		if (direction == null || direction.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing direction");
			return;
		}
		try{
			if (direction.equals("next")) {
				updatedSongIndex = Math.min(songIndex + 5, songs.size());
				session.setAttribute("trimmedSongList", songs.subList(songIndex, updatedSongIndex));
				System.out.println("songindex: " + songIndex + " updatedSongIndex: " + updatedSongIndex);
			} else if (direction.equals("previous")) {
				updatedSongIndex = Math.max(0, songIndex - 5);
				session.setAttribute("trimmedSongList", songs.subList(updatedSongIndex, songIndex));
				System.out.println("songindex: " + songIndex + " updatedSongIndex: " + updatedSongIndex);
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid direction");
			return;
		}

		// Update the song index in the session
		session.setAttribute("songIndex", updatedSongIndex);
		// Redirect to the BrowseSongs page
		String playlistPath = "/WEB-INF/playlist.html";
		forward(request, response, playlistPath);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}



}
