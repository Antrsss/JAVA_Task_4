package by.zgirskaya.course.controller.auth;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.command.CommandFactory;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(urlPatterns = {
    PageParameter.Path.LOGIN,
    PageParameter.Path.LOGOUT,
    PageParameter.Path.REGISTER
})
public class AuthServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    logger.debug("AuthServlet processing request: {}", request.getServletPath());

    try {
      Command command = CommandFactory.createAuthCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Error processing authentication request: {}", request.getRequestURI(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
    }
  }
}