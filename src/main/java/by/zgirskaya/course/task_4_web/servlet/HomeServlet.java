package by.zgirskaya.course.task_4_web.servlet;

import by.zgirskaya.course.task_4_web.model.user.AbstractUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("")
public class HomeServlet extends BaseServlet {
  private static final String HOME_JSP = "/WEB-INF/jsp/home-content.jsp";
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute("user") == null) {
      response.sendRedirect(request.getContextPath() + "/auth/login");
      return;
    }

    AbstractUserModel user = (AbstractUserModel) session.getAttribute("user");
    request.setAttribute("user", user);
    renderPage(request, response, HOME_JSP, "Home");
  }
}