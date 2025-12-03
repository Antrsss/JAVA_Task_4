package by.zgirskaya.course.servlet;

import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(WebServletParameters.HOME_PATH)
public class HomeServlet extends BaseServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute(AttributeParameters.USER) == null) {
      response.sendRedirect(request.getContextPath() + PathParameters.AUTH_LOGIN_REDIRECT);
      return;
    }

    AbstractUserModel user = (AbstractUserModel) session.getAttribute(AttributeParameters.USER);
    request.setAttribute(AttributeParameters.USER, user);
    renderPage(request, response, JspParameters.HOME, PageTitleParameters.HOME);
  }
}