package by.zgirskaya.course.servlet.auth;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.auth.AuthService;
import by.zgirskaya.course.service.auth.impl.AuthServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.AuthParameters;
import by.zgirskaya.course.util.PageParameters;
import by.zgirskaya.course.util.WebServletParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(WebServletParameters.REGISTER_PATH)
public class RegisterServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();
  private final AuthService authService = new AuthServiceImpl();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("Processing GET request for path: {}", WebServletParameters.REGISTER_PATH);

    try {
      showRegisterPage(request, response);
    } catch (ServletException | IOException e) {
      logger.error("Error processing GET request for path: {}", WebServletParameters.REGISTER_PATH, e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("Processing POST request for path: {}", WebServletParameters.REGISTER_PATH);

    String name = request.getParameter(AuthParameters.Parameters.NAME);
    String identifier = request.getParameter(AuthParameters.Parameters.IDENTIFIER);
    String role = request.getParameter(AuthParameters.Parameters.ROLE);
    String password = request.getParameter(AuthParameters.Parameters.PASSWORD);
    String username = request.getParameter(AuthParameters.Parameters.USERNAME);
    String passportId = request.getParameter(AuthParameters.Parameters.PASSPORT_ID);

    logger.info("Registration attempt - Name: {}, Identifier: {}, Role: {}", name, identifier, role);

    try {
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
    } catch (ServletException | IOException e) {
      logger.error("Error processing POST request for path: {}", WebServletParameters.REGISTER_PATH, e);
    }
  }

  private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    logger.debug("Displaying registration page");

    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.REGISTER);
    request.setAttribute(AttributeParameters.PAGE_TITLE, PageParameters.Title.REGISTER);
    request.getRequestDispatcher(PageParameters.Jsp.REGISTER).forward(request, response);
  }
}
