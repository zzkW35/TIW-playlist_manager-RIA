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

//		int songIndex = (int) session.getAttribute("songIndex");
//		int updatedSongIndex = songIndex;
//		System.out.println("songindex: " + songIndex + " updatedSongIndex: " + updatedSongIndex);
//
		// Check if the user wants to go to the next song or to the previous one
		String direction = request.getParameter("direction");
		if (direction == null || direction.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing direction");
			return;
		}
		if (direction.equals("next")) {
			goToNextPage(request, response);
		} else if (direction.equals("previous")) {
			goToPreviousPage(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid direction");
			return;
		}

//
//		// Update the song index in the session
//		session.setAttribute("songIndex", updatedSongIndex);
//		// Redirect to the BrowseSongs page
//		String playlistPath = "/WEB-INF/playlist.html";
//		forward(request, response, playlistPath);
//		String playlistPath = "/WEB-INF/playlist.html";
//		forward(request, response, playlistPath);
		forward(request, response, "/WEB-INF/playlist.html");

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
	private static final int SONGS_PER_PAGE = 5;
	private List<Song> getCurrentPageSongs(HttpServletRequest request, List<Song> songList, int CurrentPage) {
		int startIndex = (CurrentPage - 1) * SONGS_PER_PAGE;
		int endIndex = Math.min(startIndex + SONGS_PER_PAGE, songList.size());
		try {
			return songList.subList(startIndex, endIndex);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	private int getCurrentPage(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String pageString = (String) session.getAttribute("page");
//		String pageString = request.getParameter("page");
		if (pageString == null) {
			return 1;
		}
		try {
			return Integer.parseInt(pageString);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	private int getNumberOfPages(List<Song> songList) {
		return (int) Math.ceil((double) songList.size() / SONGS_PER_PAGE);
	}

	// Go to next page of songs
	private void goToNextPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		List<Song> songs = (List<Song>) session.getAttribute("songs");
		int currentPage = getCurrentPage(request);
		int numberOfPages = getNumberOfPages(songs);
		if (currentPage < numberOfPages) {
			currentPage++;
		}
		session.setAttribute("trimmedSongList", getCurrentPageSongs(request, songs, currentPage));
		session.setAttribute("currentPage", currentPage);
//		response.sendRedirect(getServletContext().getContextPath() + "/RefreshPlaylistPage");
		System.out.println("current page: " + currentPage + " number of pages: " + numberOfPages);
		for (Song song : getCurrentPageSongs(request, songs, currentPage)) {
			System.out.println(song.getTitle());
		}
	}

	// Go to previous page of songs
	private void goToPreviousPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		List<Song> songs = (List<Song>) session.getAttribute("songs");
		int currentPage = getCurrentPage(request);
		int numberOfPages = getNumberOfPages(songs);
		if (currentPage > 1) {
			currentPage--;
		}
		session.setAttribute("trimmedSongList", getCurrentPageSongs(request, songs, currentPage));
		session.setAttribute("currentPage", currentPage);
//		response.sendRedirect(getServletContext().getContextPath() + "/RefreshPlaylistPage");
		System.out.println("current page: " + currentPage + " number of pages: " + numberOfPages);
		for (Song song : getCurrentPageSongs(request, songs, currentPage)) {
			System.out.println(song.getTitle());
		}
	}

}
