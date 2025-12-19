package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class HomeCommand implements Command {
  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServiceException, IOException, ServletException {

    HttpSession session = request.getSession(false);

    if (session == null || session.getAttribute(AttributeParameter.USER) == null) {
      request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.LOGIN_CONTENT);
      request.setAttribute(AttributeParameter.PAGE_TITLE, PageParameter.Title.LOGIN);
      request.getRequestDispatcher(PageParameter.Jsp.TEMPLATE_CONTENT).forward(request, response);
      return;
    }

    Object user = session.getAttribute(AttributeParameter.USER);
    request.setAttribute(AttributeParameter.USER, user);
    request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.HOME_CONTENT);
    request.setAttribute(AttributeParameter.PAGE_TITLE, PageParameter.Title.HOME);
    request.getRequestDispatcher(PageParameter.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }
}