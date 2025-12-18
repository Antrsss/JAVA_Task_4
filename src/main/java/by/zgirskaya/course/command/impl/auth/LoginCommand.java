package by.zgirskaya.course.command.impl.auth;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.auth.AuthService;
import by.zgirskaya.course.service.auth.impl.AuthServiceImpl;
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
import java.util.Optional;

public class LoginCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final AuthService authService = new AuthServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServiceException, IOException, ServletException {

    logger.debug("Executing LoginCommand");

    String httpMethod = request.getMethod();

    if ("GET".equalsIgnoreCase(httpMethod)) {
      logger.debug("Displaying login page");

      request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.LOGIN_CONTENT);
      request.setAttribute(AttributeParameter.PAGE_TITLE, PageParameter.Title.LOGIN);
      request.getRequestDispatcher(PageParameter.Jsp.LOGIN_CONTENT).forward(request, response);

    } else if ("POST".equalsIgnoreCase(httpMethod)) {
      processLogin(request, response);
      request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.LOGIN_CONTENT);
      request.setAttribute(AttributeParameter.PAGE_TITLE, PageParameter.Title.LOGIN);
      request.getRequestDispatcher(PageParameter.Jsp.LOGIN_CONTENT).forward(request, response);
    } else {
      logger.warn("Unsupported HTTP method: {}", httpMethod);
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
  }

  private void processLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServiceException {

    String identifier = request.getParameter(AuthParameter.Parameters.IDENTIFIER);
    String password = request.getParameter(AuthParameter.Parameters.PASSWORD);

    logger.info("Login attempt - Identifier: {}", identifier);

    Optional<AbstractUserModel> userOptional = authService.authenticateUser(identifier, password);

    if (userOptional.isPresent()) {
      AbstractUserModel user = userOptional.get();
      String userRole = authService.findRoleById(user.getRoleId());
      logger.info("Successful login for user: {} (ID: {})", identifier, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute(AttributeParameter.USER, user);
      session.setAttribute(AttributeParameter.USER_ROLE, userRole);

      if (AuthParameter.Roles.CUSTOMER.equals(userRole)) {
        session.setAttribute(AttributeParameter.CUSTOMER_ID, user.getId());
      }

      response.sendRedirect(request.getContextPath() + PageParameter.Path.ROOT);
    } else {
      logger.warn("Failed login attempt for identifier: {}", identifier);
      request.setAttribute(AttributeParameter.ERROR, "Invalid credentials");
    }
  }
}