package by.zgirskaya.course.servlet;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.auth.AuthService;
import by.zgirskaya.course.service.auth.impl.AuthServiceImpl;
import by.zgirskaya.course.util.*;
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

@WebServlet(WebServletParameters.AUTH_PATH)
public class AuthServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();
  private final AuthService authService = new AuthServiceImpl();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String path = request.getPathInfo();
    logger.debug("Processing GET request for path: {}", path);

    try {
      if (path == null || path.equals(PageParameters.Path.ROOT) || path.equals(PageParameters.Path.LOGIN)) {
        showLoginPage(request, response);
      } else if (path.equals(PageParameters.Path.REGISTER)) {
        showRegisterPage(request, response);
      } else {
        logger.warn("Requested path not found: {}", path);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Error processing GET request for path: {}", path, e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String path = request.getPathInfo();
    logger.debug("Processing POST request for path: {}", path);

    try {
      if (path == null || path.equals(PageParameters.Path.ROOT) || path.equals(PageParameters.Path.LOGIN)) {
        processLogin(request, response);
      } else if (path.equals(PageParameters.Path.REGISTER)) {
        processRegistration(request, response);
      } else {
        logger.warn("Requested path not found: {}", path);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("Error processing POST request for path: {}", path, e);
    }
  }

  private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.debug("Displaying login page");
    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.LOGIN);
    request.setAttribute(AttributeParameters.PAGE_TITLE, PageParameters.Title.LOGIN);
    request.getRequestDispatcher(PageParameters.Jsp.LOGIN).forward(request, response);
  }

  private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.debug("Displaying registration page");
    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.REGISTER);
    request.setAttribute(AttributeParameters.PAGE_TITLE, PageParameters.Title.REGISTER);
    request.getRequestDispatcher(PageParameters.Jsp.REGISTER).forward(request, response);
  }

  private void processLogin(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);
    String password = request.getParameter(AuthParameters.Parameters.PASSWORD);

    logger.info("Login attempt - Identifier: {}", identifier);

    try {
      if (identifier == null || identifier.isBlank()) {
        logger.warn("Login failed - no identifier provided");
        request.setAttribute(AttributeParameters.ERROR, AuthParameters.Validation.PHONE_OR_EMAIL_REQUIRED);
        showLoginPage(request, response);
        return;
      }

      if (password == null || password.isBlank()) {
        logger.warn("Login failed - no password provided");
        request.setAttribute(AttributeParameters.ERROR, "Password is required");
        showLoginPage(request, response);
        return;
      }

      Optional<AbstractUserModel> userOptional = authService.authenticate(identifier, password);

      if (userOptional.isPresent()) {
        AbstractUserModel user = userOptional.get();
        logger.info("Successful login for user: {} (ID: {})", identifier, user.getId());

        HttpSession session = request.getSession();
        session.setAttribute(AttributeParameters.USER, user);
        session.setAttribute(AttributeParameters.USER_ROLE, user.getRoleId().toString());

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
  }

  private void processRegistration(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String name = request.getParameter(AuthParameters.Parameters.NAME);
    String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);
    String role = request.getParameter(AuthParameters.Parameters.ROLE);
    String password = request.getParameter(AuthParameters.Parameters.PASSWORD);
    String username = request.getParameter(AuthParameters.Parameters.USERNAME);
    String passportId = request.getParameter(AuthParameters.Parameters.PASSPORT_ID);

    logger.info("Registration attempt - Name: {}, Identifier: {}, Role: {}", name, identifier, role);

    try {
      AbstractUserModel user = authService.registerUser(name, identifier, role, password, username, passportId);

      logger.info("User successfully registered: {} (ID: {})", identifier, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute(AttributeParameters.USER, user);
      session.setAttribute(AttributeParameters.USER_ROLE, user.getRoleId().toString());

      response.sendRedirect(request.getContextPath() + PageParameters.Path.ROOT);

    } catch (ServiceException e) {
      logger.error("Registration failed for identifier: {}", identifier, e);
      request.setAttribute(AttributeParameters.ERROR, e.getMessage());
      showRegisterPage(request, response);
    }
  }
}