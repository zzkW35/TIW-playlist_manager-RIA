package it.polimi.tiw.playlistmanager.controllers;

import it.polimi.tiw.playlistmanager.beans.Song;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.SongDAO;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

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

/**
 * Servlet implementation class UploadSong
 */
@WebServlet("/UploadSong")
public class UploadSong extends HttpServlet {
	@Serial
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadSong() {
        super();
    }

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		this.templateEngine = ThymeleafHandler.handler(servletContext);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String songTitle;
		String songCoverPath;
		String songAlbum;
		String songArtist;
		int songAlbumYear;
		String songGenre;
		String songFilePath;
		User songUploader = (User) request.getSession().getAttribute("currentUser");
		int songUploaderId = songUploader.getId();

		try {
			songTitle = request.getParameter("songTitle");
			songCoverPath = request.getParameter("songCoverPath");
			songAlbum = request.getParameter("songAlbum");
			songArtist = request.getParameter("songArtist");
			songAlbumYear = Integer.parseInt((request.getParameter("songAlbumYear")));
			songGenre = request.getParameter("songGenre");
			songFilePath = request.getParameter("songFilePath");
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing parameters, error is: " + e.getMessage());
			return;
		}

		// Insert the song into the database
		SongDAO songDAO = new SongDAO(connection);
		try {
			songDAO.addSong(songTitle, songCoverPath, songAlbum, songArtist, songAlbumYear, songGenre, songFilePath,
					songUploaderId);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not upload the song");
			return;
		}

		// Get the songs of the user from the database
		User user = (User) request.getSession().getAttribute("currentUser");
		List<Song> userSongs;
		try {
			userSongs = songDAO.findAllSongsByUserId(user.getId());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error is: " + e.getMessage());
			return;
		}
		HttpSession session = request.getSession();
		session.setAttribute("userSongs", userSongs);
		String homePath = "/WEB-INF/home.html";
		forward(request, response, homePath);
	}
	private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}
}
