package by.zgirskaya.course.command.impl.order;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Order;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.OrderService;
import by.zgirskaya.course.service.cart.impl.OrderServiceImpl;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ViewOrdersCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final OrderService orderService = new OrderServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Attempting to view orders for user");

    Optional<AbstractUserModel> userOptional = getUserFromSession(request, response);
    if (userOptional.isEmpty()) {
      logger.warn("User not found in session, redirecting to login");
      return;
    }
    AbstractUserModel currentUser = userOptional.get();

    List<Order> completedOrders = orderService.findOrdersByCustomerId(currentUser.getId());
    int orderCount = completedOrders.size();

    logger.debug("Found {} orders for user ID: {}", orderCount, currentUser.getId());

    request.setAttribute(AttributeParameter.ORDERS, completedOrders);
    request.setAttribute(AttributeParameter.ORDER_COUNT, orderCount);

    request.getRequestDispatcher(PageParameter.Jsp.ORDERS_CONTENT).forward(request, response);
  }
}