package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Item;
import by.zgirskaya.course.model.Order;
import by.zgirskaya.course.model.AbstractUserModel;
import by.zgirskaya.course.service.OrderService;
import by.zgirskaya.course.service.impl.OrderServiceImpl;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ViewOrderDetailsCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final OrderService orderService = new OrderServiceImpl();

  private static final String ORDER_ID = "orderId";

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Attempting to view order details");

    Optional<AbstractUserModel> userOptional = getUserFromSession(request, response);
    if (userOptional.isEmpty()) {
      logger.warn("User not found in session, redirecting to login");
      return;
    }

    String orderIdParam = request.getParameter(ORDER_ID);
    logger.debug("Order ID from parameter: {}", orderIdParam);

    if (orderIdParam == null || orderIdParam.isEmpty()) {
      String pathInfo = request.getPathInfo();
      logger.debug("Path info: {}", pathInfo);

      if (pathInfo != null && pathInfo.startsWith(VIEW_PATH)) {
        orderIdParam = pathInfo.substring(VIEW_PATH.length());
        logger.debug("Extracted order ID from path: {}", orderIdParam);
      }
    }

    UUID orderId = UUID.fromString(orderIdParam);
    Order order = orderService.findOrderById(orderId);
    List<Item> orderItems = orderService.findOrderItems(orderId);

    request.setAttribute("order", order);
    request.setAttribute("orderItems", orderItems);
    request.setAttribute("itemsCount", orderItems.size());

    request.getRequestDispatcher(PageParameter.Jsp.ORDER_DETAILS_CONTENT).forward(request, response);
  }
}