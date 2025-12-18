package by.zgirskaya.course.controller.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.command.CommandFactory;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(PageParameter.Path.CART)
public class CartServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createCartCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Error getting items in cart", e);
      request.setAttribute(AttributeParameter.ERROR, "Unable to get items in cart: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameter.Path.BOOKS_REDIRECT);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createCartCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Error adding item to cart", e);
      request.setAttribute(AttributeParameter.ERROR, "Unable to add item to cart: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameter.Path.BOOKS_REDIRECT);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createCartCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Invalid item deletion in cart", e);
      request.setAttribute(AttributeParameter.ERROR, "Invalid item deletion in cart");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.CART_REDIRECT);
    }
  }
}