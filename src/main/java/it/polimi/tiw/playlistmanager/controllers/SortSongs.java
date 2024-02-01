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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.forward;
import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.handler;

/**
 * Servlet implementation class SortSongs
 */
@WebServlet("/SortSongs")
public class SortSongs extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public SortSongs() {
        super();
    }

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        this.templateEngine = handler(servletContext);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Clean all the session attributes
        request.getSession().invalidate();

        // Redirect to the login page
        String loginPage = "/WEB-INF/login.html";
        forward(request, response, loginPage, getServletContext(), templateEngine);
    }
}
