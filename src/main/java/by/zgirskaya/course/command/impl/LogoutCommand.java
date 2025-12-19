package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LogoutCommand implements Command {
  private static final Logger logger = LogManager.getLogger();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServiceException, IOException, ServletException {

    logger.debug("Executing LogoutCommand");
    HttpSession session = request.getSession(false);

    if (session != null) {
      session.removeAttribute(AttributeParameter.USER);
      session.removeAttribute(AttributeParameter.USER_ROLE);
      session.removeAttribute(AttributeParameter.CUSTOMER_ID);
      session.removeAttribute("currentCart");
      session.removeAttribute(AttributeParameter.SUCCESS_MESSAGE);
      session.removeAttribute(AttributeParameter.ERROR);

      session.invalidate();
      logger.info("Session invalidated successfully");
    }
    response.sendRedirect(request.getContextPath() + PageParameter.Path.LOGIN);
  }
}