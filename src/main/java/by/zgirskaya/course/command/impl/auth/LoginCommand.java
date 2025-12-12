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
      showLoginPage(request, response);
    } else if ("POST".equalsIgnoreCase(httpMethod)) {
      processLogin(request, response);
    } else {
      logger.warn("Unsupported HTTP method: {}", httpMethod);
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
  }

  private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    logger.debug("Displaying login page");

    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.LOGIN_CONTENT);
    request.setAttribute(AttributeParameters.PAGE_TITLE, PageParameters.Title.LOGIN);
    request.getRequestDispatcher(PageParameters.Jsp.LOGIN_CONTENT).forward(request, response);
  }

  private void processLogin(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);
    String password = request.getParameter(AuthParameters.Parameters.PASSWORD);

    logger.info("Login attempt - Identifier: {}", identifier);

    Optional<AbstractUserModel> userOptional = authService.authenticateUser(identifier, password);

    if (userOptional.isPresent()) {
      AbstractUserModel user = userOptional.get();
      String userRole = authService.findRoleById(user.getRoleId());
      logger.info("Successful login for user: {} (ID: {})", identifier, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute(AttributeParameters.USER, user);
      session.setAttribute(AttributeParameters.USER_ROLE, userRole);

      // Для покупателей устанавливаем customerId
      if (AuthParameters.Roles.CUSTOMER.equals(userRole)) {
        session.setAttribute(AttributeParameters.CUSTOMER_ID, user.getId());
      }

      response.sendRedirect(request.getContextPath() + PageParameters.Path.ROOT);
    } else {
      logger.warn("Failed login attempt for identifier: {}", identifier);
      request.setAttribute(AttributeParameters.ERROR, "Invalid credentials");
      showLoginPage(request, response);
    }
  }
}