package by.zgirskaya.course.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class BaseServlet extends HttpServlet {
  private static final String TEMPLATE_JSP = "/WEB-INF/jsp/common/template.jsp";

  protected void renderPage(HttpServletRequest request, HttpServletResponse response,
                            String contentPage, String pageTitle)
          throws ServletException, IOException {

    request.setAttribute("contentPage", contentPage);
    request.setAttribute("pageTitle", pageTitle);
    request.getRequestDispatcher(TEMPLATE_JSP).forward(request, response);
  }
}
