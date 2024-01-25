package it.polimi.tiw.playlistmanager.controllers;

import java.io.IOException;
import java.sql.Connection;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.handler;

/**
 * Servlet implementation class GoToLoginPage
 */
@WebServlet("/RefreshPlaylistPage")
public class RefreshPlaylistPage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public RefreshPlaylistPage() {
        super();
    }

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.templateEngine = handler(servletContext);
        this.connection = ConnectionHandler.getConnection(getServletContext());
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect to the Home page and add missions to the parameters
        String loginPage = "/WEB-INF/playlist.html";
        forward(request, response, loginPage);
    }
    private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }
}
