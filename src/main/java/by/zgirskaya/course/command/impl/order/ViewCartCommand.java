package by.zgirskaya.course.command.impl.order;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.model.cart.Order;
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
import java.util.List;

public class ViewCartCommand implements Command {
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
      Order currentOrder = orderService.getCurrentOrder(currentUser.getId());
      List<Item> items = null;
      double totalPrice = 0.0;

      if (currentOrder != null) {
        items = orderService.getOrderItems(currentOrder.getId());
        totalPrice = currentOrder.getOrderPrice();
      }

      request.setAttribute("currentOrder", currentOrder);
      request.setAttribute("items", items);
      request.setAttribute("totalPrice", totalPrice);

      request.getRequestDispatcher(PageParameters.Jsp.CART_CONTENT).forward(request, response);

    } catch (ServiceException e) {
      logger.error("Error loading cart", e);
      request.setAttribute(AttributeParameters.ERROR, "Failed to load cart: " + e.getMessage());
      request.getRequestDispatcher(PageParameters.Jsp.ERROR_CONTENT).forward(request, response);
    }
  }
}