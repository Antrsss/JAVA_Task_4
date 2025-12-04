package by.zgirskaya.course.servlet.auth;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.auth.AuthService;
import by.zgirskaya.course.service.auth.impl.AuthServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.AuthParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

@WebServlet(PageParameters.Path.LOGIN)
public class LoginServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();
  private final AuthService authService = new AuthServiceImpl();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("Processing GET request for path: {}", PageParameters.Path.LOGIN);

    try {
      showLoginPage(request, response);
    } catch (ServletException | IOException e) {
      logger.error("Error processing GET request for path: {}", PageParameters.Path.LOGIN, e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("Processing POST request for path: {}", PageParameters.Path.LOGIN);

    try {
      String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);
      String password = request.getParameter(AuthParameters.Parameters.PASSWORD);

      logger.info("Login attempt - Identifier: {}", identifier);

      try {
        Optional<AbstractUserModel> userOptional = authService.authenticateUser(identifier, password);

        if (userOptional.isPresent()) {
          AbstractUserModel user = userOptional.get();
          String userRole = authService.findRoleById(user.getRoleId());
          logger.info("Successful login for user: {} (ID: {})", identifier, user.getId());

          HttpSession session = request.getSession();
          session.setAttribute(AttributeParameters.USER, user);
          session.setAttribute(AttributeParameters.USER_ROLE, userRole);

          response.sendRedirect(request.getContextPath() + PageParameters.Path.ROOT);
        } else {
          logger.warn("Failed login attempt for identifier: {}", identifier);
          request.setAttribute(AttributeParameters.ERROR, "Invalid credentials");
          showLoginPage(request, response);
        }
      } catch (ServiceException e) {
        logger.error("Service error during login for identifier: {}", identifier, e);
        request.setAttribute(AttributeParameters.ERROR, "Authentication error. Please try again.");
        showLoginPage(request, response);
      }
    } catch (ServletException | IOException e) {
      logger.error("Error processing POST request for path: {}", PageParameters.Path.LOGIN, e);
    }
  }

  private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.debug("Displaying login page");

    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.LOGIN_CONTENT);
    request.setAttribute(AttributeParameters.PAGE_TITLE, PageParameters.Title.LOGIN);
    request.getRequestDispatcher(PageParameters.Jsp.LOGIN_CONTENT).forward(request, response);
  }
}