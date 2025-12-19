package by.zgirskaya.course.controller;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.command.CommandType;
import by.zgirskaya.course.exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/controller/*")
public class FrontControllerServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // Используем requestURI для получения полного пути
    String requestURI = request.getRequestURI();
    String contextPath = request.getContextPath();

    // Убираем contextPath из requestURI
    String path = requestURI.substring(contextPath.length());

    logger.debug("FrontController processing request: {}, path: {}", requestURI, path);

    try {
      Command command = CommandType.createCommand(request, path);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Error processing request: {}", requestURI, e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
    } catch (IllegalArgumentException e) {
      logger.error("Invalid command request: {}", requestURI, e);
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
    }
  }
}