package by.zgirskaya.course.servlet;

import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(PageParameters.Path.HOME)
public class HomeServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute(AttributeParameters.USER) == null) {
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return;
    }

    AbstractUserModel user = (AbstractUserModel) session.getAttribute(AttributeParameters.USER);
    request.setAttribute(AttributeParameters.USER, user);
    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.HOME);
    request.setAttribute(AttributeParameters.PAGE_TITLE, PageParameters.Title.HOME);
    request.getRequestDispatcher(PageParameters.Jsp.TEMPLATE).forward(request, response);
  }
}