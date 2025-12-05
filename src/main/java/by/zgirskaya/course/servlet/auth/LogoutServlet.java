package by.zgirskaya.course.servlet.auth;

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

@WebServlet(PageParameters.Path.LOGOUT)
public class LogoutServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processLogout(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processLogout(request, response);
  }

  private void processLogout(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.debug("Processing logout request");

    HttpSession session = request.getSession(false);

    if (session != null) {
      Object user = session.getAttribute("user");
      if (user != null) {
        logger.info("User logout: {}", user);
      }

      session.removeAttribute("user");
      session.removeAttribute("userRole");
      session.removeAttribute("customerId");
      session.removeAttribute("currentCart");
      session.removeAttribute("successMessage");
      session.removeAttribute("error");

      session.invalidate();

      logger.info("Session invalidated successfully");
    } else {
      logger.debug("No active session found for logout");
    }

    response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN);
  }
}