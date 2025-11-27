package by.zgirskaya.course.task_4_web.servlet;

import by.zgirskaya.course.task_4_web.model.user.AbstractUserModel;
import by.zgirskaya.course.task_4_web.util.HomeParameters;
import by.zgirskaya.course.task_4_web.util.WebServletParameters;
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

    if (session == null || session.getAttribute(HomeParameters.USER_ATTRIBUTE) == null) {
      response.sendRedirect(request.getContextPath() + HomeParameters.AUTH_LOGIN_REDIRECT);
      return;
    }

    AbstractUserModel user = (AbstractUserModel) session.getAttribute(HomeParameters.USER_ATTRIBUTE);
    request.setAttribute(HomeParameters.USER_ATTRIBUTE, user);
    renderPage(request, response, HomeParameters.HOME_JSP, HomeParameters.HOME_PAGE_TITLE);
  }
}