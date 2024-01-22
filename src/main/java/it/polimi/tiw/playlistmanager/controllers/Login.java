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

import it.polimi.tiw.playlistmanager.handlers.ConnectionHandler;
import it.polimi.tiw.playlistmanager.beans.User;
import it.polimi.tiw.playlistmanager.dao.UserDAO;

import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.handler;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        this.templateEngine = handler(servletContext);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        UserDAO userDAO = new UserDAO(connection);
        User user = null;
        try {
            user = userDAO.findUser(email, password);
            System.out.println("User: " + user.getName());
            response.getWriter().append("Hello ").append(user.getName());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error is: " + e.getMessage());
            return;
        }
    }

}
