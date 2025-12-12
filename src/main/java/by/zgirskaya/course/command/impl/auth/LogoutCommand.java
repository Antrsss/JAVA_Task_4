package by.zgirskaya.course.command.impl.auth;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
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

    processLogout(request, response);
  }

  private void processLogout(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    HttpSession session = request.getSession(false);

    if (session != null) {
      Object user = session.getAttribute(AttributeParameters.USER);
      if (user != null) {
        logger.info("User logout: {}", user);
      }

      // Очищаем все атрибуты сессии
      session.removeAttribute(AttributeParameters.USER);
      session.removeAttribute(AttributeParameters.USER_ROLE);
      session.removeAttribute(AttributeParameters.CUSTOMER_ID);
      session.removeAttribute("currentCart");
      session.removeAttribute(AttributeParameters.SUCCESS_MESSAGE);
      session.removeAttribute(AttributeParameters.ERROR);

      session.invalidate();
      logger.info("Session invalidated successfully");
    } else {
      logger.debug("No active session found for logout");
    }

    response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN);
  }
}