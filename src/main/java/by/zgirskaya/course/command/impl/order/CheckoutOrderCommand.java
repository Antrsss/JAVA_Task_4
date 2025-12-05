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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckoutOrderCommand implements Command {
  private static final Logger logger = LogManager.getLogger();

  private final OrderService orderService = new OrderServiceImpl();
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException, ParseException {

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
      String deliveryDateStr = request.getParameter("deliveryDate");
      if (deliveryDateStr == null || deliveryDateStr.trim().isEmpty()) {
        session.setAttribute("error", "Delivery date is required");
        response.sendRedirect(request.getContextPath() + "/cart");
        return;
      }

      Date deliveryDate = dateFormat.parse(deliveryDateStr);
      Date today = new Date();

      if (deliveryDate.before(today)) {
        session.setAttribute("error", "Delivery date cannot be in the past");
        response.sendRedirect(request.getContextPath() + "/cart");
        return;
      }

      // Оформляем заказ (меняем статус и устанавливаем дату доставки)
      boolean success = orderService.checkoutOrder(currentUser.getId(), deliveryDate);

      if (success) {
        session.setAttribute("successMessage",
            String.format("Order placed successfully! Delivery date: %s",
                dateFormat.format(deliveryDate)));
        response.sendRedirect(request.getContextPath() + "/orders");
      } else {
        session.setAttribute("error", "Failed to place order. Your order might be empty.");
        response.sendRedirect(request.getContextPath() + "/cart");
      }

    } catch (ParseException e) {
      logger.error("Invalid date format", e);
      session.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD");
      response.sendRedirect(request.getContextPath() + "/cart");
    } catch (IllegalArgumentException e) {
      logger.error("Invalid parameter", e);
      session.setAttribute("error", e.getMessage());
      response.sendRedirect(request.getContextPath() + "/cart");
    }
  }
}
