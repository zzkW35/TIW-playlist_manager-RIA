package it.polimi.tiw.playlistmanager.handlers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ThymeleafHandler {

    public static TemplateEngine handler(ServletContext context){
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        return templateEngine;
    }

    public static void forward(HttpServletRequest request, HttpServletResponse response, String path,
                        ServletContext servletContext, TemplateEngine templateEngine) throws IOException {
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());
    }

    public static void forwardToErrorPage(HttpServletRequest request, HttpServletResponse response, String error,
                                    ServletContext servletContext, TemplateEngine templateEngine) throws IOException{
        HttpSession session = request.getSession();
        session.setAttribute("errorInfo", error);
        System.out.println(error);
        forward(request, response, "/WEB-INF/error.html", servletContext, templateEngine);
    }
}
