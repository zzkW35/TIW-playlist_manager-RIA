package it.polimi.tiw.playlistmanager.controllers;

import it.polimi.tiw.playlistmanager.beans.Song;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
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
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseSongs() {
        super();
    }

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		this.templateEngine = handler(servletContext);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get the song list from the request
		HttpSession session = request.getSession();
		List<Song> songs = (List<Song>) session.getAttribute("songs");
		if (songs == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing songs");
			return;
		}

		//Get number of pages
		int numberOfPages = getNumberOfPages(songs, request);

		// Check if the user wants to go to the next song or to the previous one
		String direction = request.getParameter("direction");
		if (direction == null || direction.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing direction");
			return;
		}
		if (direction.equals("next")) {
			goToNextPage(request, numberOfPages);
		} else if (direction.equals("previous")) {
			goToPreviousPage(request);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid direction");
			return;
		}

		String playlistPath = "/WEB-INF/playlist.html";
		forward(request, response, playlistPath);
	}

	private static final int SONGS_PER_PAGE = 5;
	private List<Song> getCurrentPageSongs(List<Song> songList, int CurrentPage) {
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
		Integer currentPageInteger = (Integer) session.getAttribute("currentPage");
		String pageString = currentPageInteger != null ? Integer.toString(currentPageInteger) : null;
		if (pageString == null) {
			return 1;
		}
		try {
			return Integer.parseInt(pageString);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	private int getNumberOfPages(List<Song> songList, HttpServletRequest request) {
		HttpSession session = request.getSession();
		int numberOfPages = (int) Math.ceil((double) songList.size() / SONGS_PER_PAGE);
		session.setAttribute("numberOfPages", numberOfPages);
		return numberOfPages;
	}

	private void goToNextPage(HttpServletRequest request, int numberOfPages) throws IOException {
		HttpSession session = request.getSession();
		List<Song> songs = (List<Song>) session.getAttribute("songs");
		int currentPage = getCurrentPage(request);
		if (currentPage < numberOfPages) {
			currentPage++;
		}
		System.out.println("number of pages: " + numberOfPages);
		session.setAttribute("hasNextPage", currentPage < numberOfPages ? 1 : 0);
		session.setAttribute("trimmedSongList", getCurrentPageSongs(songs, currentPage));
		session.setAttribute("currentPage", currentPage);
	}

	private void goToPreviousPage(HttpServletRequest request) throws IOException {
		HttpSession session = request.getSession();
		List<Song> songs = (List<Song>) session.getAttribute("songs");
		int currentPage = getCurrentPage(request);
		if (currentPage > 1) {
			currentPage--;
		}
		session.setAttribute("hasPreviousPage", currentPage > 1 ? 1 : 0);
		session.setAttribute("hasNextPage", 1);
		session.setAttribute("trimmedSongList", getCurrentPageSongs(songs, currentPage));
		session.setAttribute("currentPage", currentPage);
	}

	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}
}
