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
import java.util.UUID;

public class ViewOrderDetailsCommand implements Command {
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
      // Получаем ID заказа из параметра запроса или из path
      String orderIdParam = request.getParameter("orderId");
      if (orderIdParam == null || orderIdParam.isEmpty()) {
        // Попробуем получить из path /orders/view/{id}
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/view/")) {
          orderIdParam = pathInfo.substring("/view/".length());
        }
      }

      if (orderIdParam == null || orderIdParam.isEmpty()) {
        throw new ServiceException("Order ID is required");
      }

      UUID orderId = UUID.fromString(orderIdParam);

      // Получаем детали заказа
      Order order = orderService.findOrderById(orderId);
      if (order == null) {
        throw new ServiceException("Order not found");
      }

      // Проверяем, что заказ принадлежит текущему пользователю
      if (!order.getCustomerId().equals(currentUser.getId())) {
        throw new ServiceException("Access denied");
      }

      // Получаем товары заказа
      List<Item> orderItems = orderService.findOrderItems(orderId);

      request.setAttribute("order", order);
      request.setAttribute("orderItems", orderItems);
      request.setAttribute("itemsCount", orderItems.size());

      request.getRequestDispatcher(PageParameters.Jsp.ORDER_DETAILS_CONTENT).forward(request, response);

    } catch (IllegalArgumentException e) {
      logger.error("Invalid order ID format", e);
      request.setAttribute(AttributeParameters.ERROR, "Invalid order ID format");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.ORDERS_REDIRECT);
    } catch (ServiceException e) {
      logger.error("Error loading order details", e);
      request.setAttribute(AttributeParameters.ERROR, "Failed to load order details: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameters.Path.ORDERS_REDIRECT);
    }
  }
}