package by.zgirskaya.course.command.impl.auth;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.auth.AuthService;
import by.zgirskaya.course.service.auth.impl.AuthServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.AuthParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RegisterCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final AuthService authService = new AuthServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServiceException, IOException, ServletException {

    logger.debug("Executing RegisterCommand");

    String httpMethod = request.getMethod();

    if ("GET".equalsIgnoreCase(httpMethod)) {
      logger.debug("Displaying registration page");

      request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.REGISTER_CONTENT);
      request.setAttribute(AttributeParameters.PAGE_TITLE, PageParameters.Title.REGISTER);
      request.getRequestDispatcher(PageParameters.Jsp.REGISTER_CONTENT).forward(request, response);
    } else if ("POST".equalsIgnoreCase(httpMethod)) {
      String name = request.getParameter(AuthParameters.Parameters.NAME);
      String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);
      String role = request.getParameter(AuthParameters.Parameters.ROLE);
      String password = request.getParameter(AuthParameters.Parameters.PASSWORD);
      String username = request.getParameter(AuthParameters.Parameters.USERNAME);
      String passportId = request.getParameter(AuthParameters.Parameters.PASSPORT_ID);

      logger.info("Registration attempt - Name: {}, Identifier: {}, Role: {}", name, identifier, role);

      AbstractUserModel user = authService.registerUser(name, identifier, role, password, username, passportId);
      String userRole = authService.findRoleById(user.getRoleId());
      logger.info("User successfully registered: {} (ID: {})", identifier, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute(AttributeParameters.USER, user);
      session.setAttribute(AttributeParameters.USER_ROLE, userRole);

      if (AuthParameters.Roles.CUSTOMER.equals(userRole)) {
        session.setAttribute(AttributeParameters.CUSTOMER_ID, user.getId());
      }

      response.sendRedirect(request.getContextPath() + PageParameters.Path.ROOT);
    } else {
      logger.warn("Unsupported HTTP method: {}", httpMethod);
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
  }
}