package by.zgirskaya.course.servlet.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.command.CommandFactory;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(name = "OrderServlet", urlPatterns = {"/orders", "/orders/*", "/order/confirmation"})
public class OrderServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createOrderCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Error loading orders", e);
      request.setAttribute(AttributeParameters.ERROR, "Failed to load orders: " + e.getMessage());
      request.getRequestDispatcher(PageParameters.Jsp.ERROR_CONTENT).forward(request, response);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createOrderCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Error loading orders", e);
      request.setAttribute(AttributeParameters.ERROR, "Failed to load orders: " + e.getMessage());
      request.getRequestDispatcher(PageParameters.Jsp.ERROR_CONTENT).forward(request, response);
    }
  }
}