package by.zgirskaya.course.controller;

import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(PageParameter.Path.HOME)
public class HomeServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute(AttributeParameter.USER) == null) {
      response.sendRedirect(request.getContextPath() + PageParameter.Path.LOGIN_REDIRECT);
      return;
    }

    AbstractUserModel user = (AbstractUserModel) session.getAttribute(AttributeParameter.USER);
    request.setAttribute(AttributeParameter.USER, user);
    request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.HOME_CONTENT);
    request.setAttribute(AttributeParameter.PAGE_TITLE, PageParameter.Title.HOME);
    request.getRequestDispatcher(PageParameter.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }
}