package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.AbstractUserModel;
import by.zgirskaya.course.service.AuthService;
import by.zgirskaya.course.service.impl.AuthServiceImpl;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.AuthParameter;
import by.zgirskaya.course.util.PageParameter;
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

      request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.REGISTER_CONTENT);
      request.setAttribute(AttributeParameter.PAGE_TITLE, PageParameter.Title.REGISTER);
      request.getRequestDispatcher(PageParameter.Jsp.REGISTER_CONTENT).forward(request, response);
    } else if ("POST".equalsIgnoreCase(httpMethod)) {
      String name = request.getParameter(AuthParameter.Parameters.NAME);
      String identifier = request.getParameter(AuthParameter.Parameters.IDENTIFIER);
      String role = request.getParameter(AuthParameter.Parameters.ROLE);
      String password = request.getParameter(AuthParameter.Parameters.PASSWORD);
      String username = request.getParameter(AuthParameter.Parameters.USERNAME);
      String passportId = request.getParameter(AuthParameter.Parameters.PASSPORT_ID);

      logger.info("Registration attempt - Name: {}, Identifier: {}, Role: {}", name, identifier, role);

      AbstractUserModel user = authService.registerUser(name, identifier, role, password, username, passportId);
      String userRole = authService.findRoleById(user.getRoleId());
      logger.info("User successfully registered: {} (ID: {})", identifier, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute(AttributeParameter.USER, user);
      session.setAttribute(AttributeParameter.USER_ROLE, userRole);

      if (AuthParameter.Roles.CUSTOMER.equals(userRole)) {
        session.setAttribute(AttributeParameter.CUSTOMER_ID, user.getId());
      }

      response.sendRedirect(request.getContextPath() + PageParameter.Path.ROOT);
    } else {
      logger.warn("Unsupported HTTP method: {}", httpMethod);
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
  }
}