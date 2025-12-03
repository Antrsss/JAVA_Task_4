package by.zgirskaya.course.servlet;

import by.zgirskaya.course.dao.user.UserDao;
import by.zgirskaya.course.dao.user.impl.UserDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.model.user.Customer;
import by.zgirskaya.course.model.user.Employee;
import by.zgirskaya.course.util.AuthParameters;
import by.zgirskaya.course.util.WebServletParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@WebServlet(WebServletParameters.AUTH_PATH)
public class AuthServlet extends BaseServlet {
  private static final Logger logger = LogManager.getLogger(AuthServlet.class);

  private static final UserDao userDao = new UserDaoImpl();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    String path = request.getPathInfo();
    logger.debug("Processing GET request for path: {}", path);

    try {
      if (path == null || path.equals(AuthParameters.Paths.ROOT) || path.equals(AuthParameters.Paths.LOGIN)) {
        showLoginPage(request, response);
      } else if (path.equals(AuthParameters.Paths.REGISTER)) {
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
      if (path == null || path.equals(AuthParameters.Paths.ROOT) || path.equals(AuthParameters.Paths.LOGIN)) {
        processLogin(request, response);
      } else if (path.equals(AuthParameters.Paths.REGISTER)) {
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
    renderPage(request, response, AuthParameters.Jsp.LOGIN, AuthParameters.Pages.LOGIN_TITLE);
  }

  private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    logger.debug("Displaying registration page");
    renderPage(request, response, AuthParameters.Jsp.REGISTER, AuthParameters.Pages.REGISTER_TITLE);
  }

  private void processLogin(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);

    logger.info("Login attempt - Identifier: {}", identifier);

    try {
      if (identifier == null || identifier.isBlank()) {
        logger.warn("Login failed - no identifier provided");
        request.setAttribute(AuthParameters.Attributes.ERROR, "Phone number or email is required");
        showLoginPage(request, response);
        return;
      }

      Optional<AbstractUserModel> userOptional = userDao.findByIdentifier(identifier);

      if (userOptional.isPresent()) {
        AbstractUserModel user = userOptional.get();
        String password = request.getParameter(AuthParameters.Parameters.PASSWORD);

        if (password.equals(user.getPassword())) {
          logger.info("Successful login for user: {} (ID: {})", identifier, user.getId());

          HttpSession session = request.getSession();
          session.setAttribute(AuthParameters.Attributes.USER, user);
          session.setAttribute(AuthParameters.Attributes.USER_ROLE, user.getRoleId().toString());

          response.sendRedirect(request.getContextPath() + AuthParameters.Paths.ROOT);

        } else {
          logger.warn("Failed login attempt - invalid password for user: {}", identifier);
          request.setAttribute(AuthParameters.Attributes.ERROR, "Invalid credentials");
          showLoginPage(request, response);
        }
      } else {
        logger.warn("Failed login attempt - user not found: {}", identifier);
        request.setAttribute(AuthParameters.Attributes.ERROR, "Invalid credentials");
        showLoginPage(request, response);
      }
    } catch (DaoException e) {
      logger.error("Database error during login for identifier: {}", identifier, e);
      request.setAttribute(AuthParameters.Attributes.ERROR, "Database error: " + e.getMessage());
      showLoginPage(request, response);
    }
  }

  private void processRegistration(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String name = request.getParameter(AuthParameters.Parameters.NAME);
    String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);
    String role = request.getParameter(AuthParameters.Parameters.ROLE);

    logger.info("Registration attempt - Name: {}, Identifier: {}, Role: {}",
        name, identifier, role);

    try {
      if (identifier == null || identifier.isBlank()) {
        logger.warn("Registration failed - no identifier provided");
        request.setAttribute(AuthParameters.Attributes.ERROR, "Phone number or email is required");
        showRegisterPage(request, response);
        return;
      }

      String phoneNumber = null;
      String email = null;
      boolean userExists;

      if (identifier.contains("@")) {
        email = identifier;
        userExists = userDao.existsByEmail(email);
      } else {
        phoneNumber = identifier;
        userExists = userDao.existsByPhoneNumber(phoneNumber);
      }

      if (userExists) {
        logger.warn("Registration failed - user with phone or email already exists");
        request.setAttribute(AuthParameters.Attributes.ERROR,
            AuthParameters.Validation.PHONE_OR_EMAIL_EXISTS);
        showRegisterPage(request, response);
        return;
      }

      UUID roleId;
      AbstractUserModel user;

      if (AuthParameters.Roles.EMPLOYEE.equals(role)) {
        logger.debug("Processing employee registration");
        roleId = userDao.getEmployeeRoleId();

        String passportId = request.getParameter(AuthParameters.Parameters.PASSPORT_ID);
        if (passportId == null || passportId.isBlank()) {
          logger.warn("Registration failed - missing passport ID for employee");
          request.setAttribute(AuthParameters.Attributes.ERROR, "Passport ID is required for employees");
          showRegisterPage(request, response);
          return;
        }

        user = new Employee(name, phoneNumber, email, request.getParameter(AuthParameters.Parameters.PASSWORD),
            roleId, passportId);

      } else {
        logger.debug("Processing customer registration");
        roleId = userDao.getCustomerRoleId();

        String username = request.getParameter(AuthParameters.Parameters.USERNAME);
        if (username == null || username.isBlank()) {
          logger.warn("Registration failed - missing username for customer");
          request.setAttribute(AuthParameters.Attributes.ERROR, "Username is required for customers");
          showRegisterPage(request, response);
          return;
        }

        user = new Customer(name, phoneNumber, email, request.getParameter(AuthParameters.Parameters.PASSWORD),
            roleId, username);
      }

      userDao.create(user);

      String logIdentifier = phoneNumber != null ? phoneNumber : email;
      logger.info("User successfully registered: {} (ID: {})", logIdentifier, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute(AuthParameters.Attributes.USER, user);
      session.setAttribute(AuthParameters.Attributes.USER_ROLE, user.getRoleId().toString());

      response.sendRedirect(request.getContextPath() + AuthParameters.Paths.ROOT);

    } catch (DaoException e) {
      logger.error("Registration failed for identifier: {}", identifier, e);
      request.setAttribute(AuthParameters.Attributes.ERROR, "Registration failed: " + e.getMessage());
      showRegisterPage(request, response);
    }
  }
}