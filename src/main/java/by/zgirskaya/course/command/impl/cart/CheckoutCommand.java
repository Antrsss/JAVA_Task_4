package by.zgirskaya.course.command.impl.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Cart;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.CartService;
import by.zgirskaya.course.service.cart.ItemService;
import by.zgirskaya.course.service.cart.OrderService;
import by.zgirskaya.course.service.cart.impl.CartServiceImpl;
import by.zgirskaya.course.service.cart.impl.ItemServiceImpl;
import by.zgirskaya.course.service.cart.impl.OrderServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.AuthParameters;
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
import java.util.List;
import java.util.UUID;

public class CheckoutCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final CartService cartService = new CartServiceImpl();
  private final ItemService itemService = new ItemServiceImpl();
  private final OrderService orderService = new OrderServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing CheckoutCommand");

    HttpSession session = request.getSession(false);
    if (session == null) {
      logger.warn("No session found, redirecting to login");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return;
    }

    AbstractUserModel currentUser = (AbstractUserModel) session.getAttribute(AttributeParameters.USER);
    if (currentUser == null) {
      logger.warn("User not authenticated, redirecting to login");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return;
    }

    String userRole = (String) session.getAttribute(AttributeParameters.USER_ROLE);
    if (!AuthParameters.Roles.CUSTOMER.equals(userRole)) {
      logger.warn("User role {} attempted to checkout - forbidden", userRole);
      session.setAttribute(AttributeParameters.ERROR, "Only customers can checkout orders");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
      return;
    }

    try {
      // Получаем ID покупателя
      UUID customerId = getCustomerIdFromSession(session, currentUser);
      if (customerId == null) {
        logger.error("Customer ID not found in session");
        response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
        return;
      }

      logger.debug("Processing checkout for customerId: {}", customerId);

      Cart cart = cartService.getOrCreateCartForCustomer(customerId);
      logger.debug("Cart retrieved: {}", cart.getId());

      List<Item> items = itemService.findItemsByCartId(cart.getId());
      logger.debug("Found {} items in cart for checkout", items.size());

      if (items == null || items.isEmpty()) {
        logger.warn("Attempted to checkout with empty cart for customer {}", customerId);
        session.setAttribute(AttributeParameters.ERROR, "Your cart is empty");
        response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
        return;
      }

      String deliveryDateStr = request.getParameter("deliveryDate");
      if (deliveryDateStr == null || deliveryDateStr.isEmpty()) {
        logger.warn("Delivery date not provided for checkout");
        session.setAttribute(AttributeParameters.ERROR, "Please select a delivery date");
        response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
        return;
      }

      Date deliveryDate = parseDeliveryDate(deliveryDateStr);
      UUID orderId = orderService.createOrderFromCart(cart, items, deliveryDate);
      logger.info("Order created successfully: {} for customer {}", orderId, customerId);

      itemService.clearCart(cart.getId());
      logger.info("Cart cleared after successful checkout: {}", cart.getId());

      session.setAttribute(AttributeParameters.SUCCESS_MESSAGE,
          String.format("Order #%s created successfully! Delivery scheduled for %s",
              orderId.toString().substring(0, 8), deliveryDateStr));

      session.removeAttribute("currentCart");

      String redirectUrl = request.getContextPath() + PageParameters.Path.ORDER_CONFIRMATION_REDIRECT +
          "?orderId=" + orderId;
      logger.debug("Redirecting to order confirmation: {}", redirectUrl);
      response.sendRedirect(redirectUrl);

    } catch (ServiceException e) {
      logger.error("Service error during checkout", e);
      session.setAttribute(AttributeParameters.ERROR,
          "Unable to process checkout: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid parameter during checkout", e);
      session.setAttribute(AttributeParameters.ERROR,
          "Invalid checkout parameters: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
    } catch (Exception e) {
      logger.error("Unexpected error during checkout", e);
      session.setAttribute(AttributeParameters.ERROR,
          "An unexpected error occurred during checkout. Please try again.");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
    }
  }

  private UUID getCustomerIdFromSession(HttpSession session, AbstractUserModel user) {
    Object customerIdObj = session.getAttribute(AttributeParameters.CUSTOMER_ID);

    if (customerIdObj != null) {
      if (customerIdObj instanceof UUID) {
        return (UUID) customerIdObj;
      } else if (customerIdObj instanceof String) {
        try {
          return UUID.fromString((String) customerIdObj);
        } catch (IllegalArgumentException e) {
          logger.error("Invalid customerId format in session: {}", customerIdObj, e);
        }
      }
    }

    return user.getId();
  }

  private Date parseDeliveryDate(String deliveryDateStr) throws ParseException {
    try {
      // Пытаемся парсить в формате "yyyy-MM-dd" (стандартный формат input[type="date"])
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      dateFormat.setLenient(false); // Строгая проверка даты
      return dateFormat.parse(deliveryDateStr);
    } catch (ParseException e) {
      logger.warn("Failed to parse delivery date with format yyyy-MM-dd, trying other formats");

      // Попробуем другие возможные форматы
      String[] possibleFormats = {
          "dd.MM.yyyy",
          "dd/MM/yyyy",
          "MM/dd/yyyy",
          "yyyy/MM/dd"
      };

      for (String format : possibleFormats) {
        try {
          SimpleDateFormat dateFormat = new SimpleDateFormat(format);
          dateFormat.setLenient(false);
          return dateFormat.parse(deliveryDateStr);
        } catch (ParseException ex) {
          // Продолжаем пробовать другие форматы
          continue;
        }
      }

      // Если ни один формат не подошел
      throw new ParseException("Unable to parse delivery date: " + deliveryDateStr, 0);
    }
  }
}