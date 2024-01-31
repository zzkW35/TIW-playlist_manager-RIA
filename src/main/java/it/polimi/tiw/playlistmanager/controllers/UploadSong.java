package it.polimi.tiw.playlistmanager.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.playlistmanager.beans.Song;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.SongDAO;
import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;

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

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.*;

/**
 * Servlet implementation class UploadSong
 */
@WebServlet("/UploadSong")
public class UploadSong extends HttpServlet {
	@Serial
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadSong() {
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
			String error = "Incorrect or missing parameters, details: " + e.getMessage();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(error);
			return;
		}
		if (songAlbumYear < 0) {
			String error = "Year must be positive";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(error);
			return;
		}
		// Check parameters
		if (songTitle == null || songTitle.isEmpty() || songCoverPath == null || songCoverPath.isEmpty() ||
				songAlbum == null || songAlbum.isEmpty() || songArtist == null || songArtist.isEmpty() ||
				songGenre == null || songGenre.isEmpty() || songFilePath == null || songFilePath.isEmpty()) {
			String error = "Incorrect or missing parameters";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(error);
			return;
		}

		// Insert the song into the database
		SongDAO songDAO = new SongDAO(connection);
		try {
			songDAO.addSong(songTitle, songCoverPath, songAlbum, songArtist, songAlbumYear, songGenre, songFilePath,
					songUploaderId);
		} catch (Exception e) {
			String error = "Could not upload the song, details: " + e.getMessage();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(error);
			return;
		}

		// Get the songs of the user from the database
		List<Song> userSongs;
		try {
			userSongs = songDAO.findAllSongsByUserId(songUploaderId);
		} catch (Exception e) {
			String error = "Could not retrieve songs, details: " + e.getMessage();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(error);
			return;
		}
		String userSongsJson = new Gson().toJson(userSongs);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(userSongsJson);
	}
}
