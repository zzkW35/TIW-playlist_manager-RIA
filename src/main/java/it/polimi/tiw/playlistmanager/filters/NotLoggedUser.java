package it.polimi.tiw.playlistmanager.filters;

import it.polimi.tiw.playlistmanager.beans.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Servlet Filter implementation class NotLoggedUser
 */
@WebFilter("/NotLoggedUser")
public class NotLoggedUser implements Filter {


    /**
     * Default constructor.
     */
    public NotLoggedUser() {
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
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
//                forward(req, res, "/WEB-INF/home.html", req.getServletContext(), templateEngine);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}