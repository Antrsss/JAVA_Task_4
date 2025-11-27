package by.zgirskaya.course.task_4_web.servlet;

import by.zgirskaya.course.task_4_web.dao.user.UserDao;
import by.zgirskaya.course.task_4_web.dao.user.impl.UserDaoImpl;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.user.AbstractUserModel;
import by.zgirskaya.course.task_4_web.model.user.Customer;
import by.zgirskaya.course.task_4_web.model.user.Employee;
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

@WebServlet("/auth/*")
public class AuthServlet extends BaseServlet {
  private static final Logger logger = LogManager.getLogger(AuthServlet.class);

  private static final String LOGIN_JSP = "/WEB-INF/jsp/auth/login-content.jsp";
  private static final String REGISTER_JSP = "/WEB-INF/jsp/auth/register-content.jsp";

  private static final UserDao userDao = new UserDaoImpl();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = request.getPathInfo();
    logger.debug("Processing GET request for path: {}", path);

    try {
      if (path == null || path.equals("/") || path.equals("/login")) {
        showLoginPage(request, response);
      } else if (path.equals("/register")) {
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
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = request.getPathInfo();
    logger.debug("Processing POST request for path: {}", path);

    try {
      if (path == null || path.equals("/") || path.equals("/login")) {
        processLogin(request, response);
      } else if (path.equals("/register")) {
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
    renderPage(request, response, LOGIN_JSP, "Login");
  }

  private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    logger.debug("Displaying registration page");
    renderPage(request, response, REGISTER_JSP, "Register");
  }

  private void processLogin(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    String email = request.getParameter("email");
    logger.info("Login attempt for email: {}", email);

    try {
      Optional<AbstractUserModel> userOptional = userDao.findByEmail(email);

      if (userOptional.isPresent()) {
        AbstractUserModel user = userOptional.get();
        String password = request.getParameter("password");

        // В реальном приложении используйте хеширование паролей!
        if (password.equals(user.getPassword())) {
          logger.info("Successful login for user: {} (ID: {})", email, user.getId());

          HttpSession session = request.getSession();
          session.setAttribute("user", user);
          session.setAttribute("userRole", user.getRoleId().toString());

          logger.debug("User session created for ID: {}", user.getId());
          response.sendRedirect(request.getContextPath() + "/");

        } else {
          logger.warn("Failed login attempt - invalid password for user: {}", email);
          request.setAttribute("error", "Invalid email or password");
          showLoginPage(request, response);
        }
      } else {
        logger.warn("Failed login attempt - user not found: {}", email);
        request.setAttribute("error", "Invalid email or password");
        showLoginPage(request, response);
      }
    } catch (DaoException e) {
      logger.error("Database error during login for user: {}", email, e);
      request.setAttribute("error", "Database error: " + e.getMessage());
      showLoginPage(request, response);
    }
  }

  private void processRegistration(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String phoneNumber = request.getParameter("phoneNumber");
    String role = request.getParameter("role");

    logger.info("Registration attempt - Name: {}, Email: {}, Role: {}",
            name, email, role);

    try {
      if (userDao.existsByEmail(email)) {
        logger.warn("Registration failed - email already exists: {}", email);
        request.setAttribute("error", "User with this email already exists");
        showLoginPage(request, response);
        return;
      }

      UUID roleId;
      AbstractUserModel user;

      if ("Employee".equals(role)) {
        logger.debug("Processing employee registration");
        roleId = userDao.getEmployeeRoleId();

        String passportId = request.getParameter("passportId");
        if (passportId == null || passportId.isBlank()) {
          logger.warn("Registration failed - missing passport ID for employee");
          request.setAttribute("error", "Passport ID is required for employees");
          renderPage(request, response, REGISTER_JSP, "Register");
          return;
        }

        user = new Employee(name, phoneNumber, email, request.getParameter("password"),
                roleId, passportId);
        logger.debug("Employee object created with passport ID");

      } else {
        logger.debug("Processing customer registration");
        roleId = userDao.getCustomerRoleId();

        String username = request.getParameter("username");
        if (username == null || username.isBlank()) {
          logger.warn("Registration failed - missing username for customer");
          request.setAttribute("error", "Username is required for customers");
          renderPage(request, response, REGISTER_JSP, "Register");
          return;
        }

        user = new Customer(name, phoneNumber, email, request.getParameter("password"),
                roleId, username);
        logger.debug("Customer object created with username: {}", username);
      }

      userDao.create(user);
      logger.info("User successfully registered: {} (ID: {})", email, user.getId());

      HttpSession session = request.getSession();
      session.setAttribute("user", user);
      session.setAttribute("userRole", role);

      logger.debug("User session created after registration for ID: {}", user.getId());
      response.sendRedirect(request.getContextPath() + "/");

    } catch (DaoException e) {
      logger.error("Registration failed for email: {}", email, e);
      request.setAttribute("error", "Registration failed: " + e.getMessage());
      renderPage(request, response, REGISTER_JSP, "Register");
    }
  }
}