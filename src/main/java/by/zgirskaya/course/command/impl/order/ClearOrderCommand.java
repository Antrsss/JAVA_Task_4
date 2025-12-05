package by.zgirskaya.course.command.impl.order;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.OrderService;
import by.zgirskaya.course.service.cart.impl.OrderServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ClearOrderCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final OrderService orderService = new OrderServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    HttpSession session = request.getSession(false);
    if (session == null) {
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return;
    }

    AbstractUserModel currentUser = (AbstractUserModel) session.getAttribute(AttributeParameters.USER);
    if (currentUser == null) {
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return;
    }

    try {
      var currentOrder = orderService.getCurrentOrder(currentUser.getId());

      if (currentOrder != null) {
        orderService.clearOrder(currentOrder.getId());
        session.setAttribute("successMessage", "Your order has been cleared");
      } else {
        session.setAttribute("error", "No active order found");
      }

      response.sendRedirect(request.getContextPath() + "/cart");

    } catch (ServiceException e) {
      logger.error("Error clearing order", e);
      session.setAttribute("error", "Failed to clear order: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + "/cart");
    }
  }
}