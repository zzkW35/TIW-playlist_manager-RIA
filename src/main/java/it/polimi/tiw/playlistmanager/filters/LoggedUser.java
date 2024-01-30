package it.polimi.tiw.playlistmanager.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.playlistmanager.beans.User;
import org.thymeleaf.TemplateEngine;


import static it.polimi.tiw.playlistmanager.handlers.ThymeleafHandler.*;

/**
 * Servlet Filter implementation class LoggedUser
 */
@WebFilter("/LoggedUser")
public class LoggedUser implements Filter {

    private TemplateEngine templateEngine;

    /**
     * Default constructor.
     */
    public LoggedUser() {
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        this.templateEngine = handler(servletContext);
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        if (session != null) {
            User user = (User) session.getAttribute("currentUser");

            if (user != null) {
                chain.doFilter(request, response);
                return;
            }
        }

        String error = "You are not allowed to access this page";
        forwardToErrorPage(req, res, error, req.getServletContext(), templateEngine);
    }

}