package by.zgirskaya.course.task_4_web.servlet;

import by.zgirskaya.course.task_4_web.dao.user.UserDao;
import by.zgirskaya.course.task_4_web.dao.user.impl.UserDaoImpl;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.user.AbstractUserModel;
import by.zgirskaya.course.task_4_web.model.user.Customer;
import by.zgirskaya.course.task_4_web.model.user.Employee;
import by.zgirskaya.course.task_4_web.util.AuthParameters;
import by.zgirskaya.course.task_4_web.util.WebServletParameters;
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
  private static final Logger logger = LogManager.getLogger();

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

    String phoneNumber = request.getParameter(AuthParameters.Parameters.PHONE_NUMBER);
    logger.info("Login attempt for phoneNumber: {}", phoneNumber);

    try {
      Optional<AbstractUserModel> userOptional = userDao.findByPhoneNumber(phoneNumber);

      if (userOptional.isPresent()) {
        AbstractUserModel user = userOptional.get();
        String password = request.getParameter(AuthParameters.Parameters.PASSWORD);

        if (password.equals(user.getPassword())) {
          logger.info("Successful login for user: {} (ID: {})", phoneNumber, user.getId());

          HttpSession session = request.getSession();
          session.setAttribute(AuthParameters.Attributes.USER, AuthParameters.Attributes.USER);
          session.setAttribute(AuthParameters.Attributes.USER_ROLE, user.getRoleId().toString());

          logger.debug("User session created for ID: {}", user.getId());
          response.sendRedirect(request.getContextPath() + AuthParameters.Paths.ROOT);

        } else {
          logger.warn("Failed login attempt - invalid password for user: {}", phoneNumber);
          request.setAttribute(AuthParameters.Attributes.ERROR, "Invalid phoneNumber or password");
          showLoginPage(request, response);
        }
      } else {
        logger.warn("Failed login attempt - user not found: {}", phoneNumber);
        request.setAttribute(AuthParameters.Attributes.ERROR, "Invalid phoneNumber or password");
        showLoginPage(request, response);
      }
    } catch (DaoException e) {
      logger.error("Database error during login for user: {}", phoneNumber, e);
      request.setAttribute(AuthParameters.Attributes.ERROR, "Database error: " + e.getMessage());
      showLoginPage(request, response);
    }
  }

  private void processRegistration(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    String name = request.getParameter(AuthParameters.Parameters.NAME);
    String email = request.getParameter(AuthParameters.Parameters.EMAIL);
    String phoneNumber = request.getParameter(AuthParameters.Parameters.PHONE_NUMBER);
    String role = request.getParameter(AuthParameters.Parameters.ROLE);

    logger.info("Registration attempt - Name: {}, Email: {}, Role: {}",
            name, email, role);

    try {
      if (userDao.existsByPhoneNumber(phoneNumber)) {
        logger.warn("Registration failed - phoneNumber already exists: {}", phoneNumber);
        request.setAttribute(AuthParameters.Attributes.ERROR, "User with this phoneNumber already exists");
        showLoginPage(request, response);
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
          renderPage(request, response, AuthParameters.Jsp.REGISTER, AuthParameters.Pages.REGISTER_TITLE);
          return;
        }

        user = new Employee(name, phoneNumber, email, request.getParameter(AuthParameters.Parameters.PASSWORD),
                roleId, passportId);
        logger.debug("Employee object created with passport ID");

      } else {
        logger.debug("Processing customer registration");
        roleId = userDao.getCustomerRoleId();

        String username = request.getParameter(AuthParameters.Parameters.USERNAME);
        if (username == null || username.isBlank()) {
          logger.warn("Registration failed - missing username for customer");
          request.setAttribute(AuthParameters.Attributes.ERROR, "Username is required for customers");
          renderPage(request, response, AuthParameters.Jsp.REGISTER, AuthParameters.Pages.REGISTER_TITLE);
          return;
        }

        user = new Customer(name, phoneNumber, email, request.getParameter("password"),
                roleId, username);
        logger.debug("Customer object created with username: {}", username);
      }

      userDao.create(user);
      logger.info("User successfully registered: {} (ID: {})", email, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute(AuthParameters.Attributes.USER, user);
      session.setAttribute(AuthParameters.Attributes.USER_ROLE, role);

      logger.debug("User session created after registration for ID: {}", user.getId());
      response.sendRedirect(request.getContextPath() + "/");

    } catch (DaoException e) {

      logger.error("Registration failed for email: {}", email, e);
      request.setAttribute(AuthParameters.Attributes.ERROR, "Registration failed: " + e.getMessage());
      renderPage(request, response, AuthParameters.Jsp.REGISTER, AuthParameters.Pages.REGISTER_TITLE);
    }
  }
}