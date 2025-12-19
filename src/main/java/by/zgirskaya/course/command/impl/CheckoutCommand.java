package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Cart;
import by.zgirskaya.course.model.Item;
import by.zgirskaya.course.model.AbstractUserModel;
import by.zgirskaya.course.service.CartService;
import by.zgirskaya.course.service.ItemService;
import by.zgirskaya.course.service.OrderService;
import by.zgirskaya.course.service.impl.CartServiceImpl;
import by.zgirskaya.course.service.impl.ItemServiceImpl;
import by.zgirskaya.course.service.impl.OrderServiceImpl;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.AuthParameter;
import by.zgirskaya.course.util.PageParameter;
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
import java.util.Optional;
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

    Optional<AbstractUserModel> userOptional = getUserFromSession(request, response);
    if (userOptional.isEmpty()) {
      return;
    }

    AbstractUserModel currentUser = userOptional.get();
    HttpSession session = request.getSession();

    String userRole = (String) session.getAttribute(AttributeParameter.USER_ROLE);
    if (!AuthParameter.Roles.CUSTOMER.equals(userRole)) {
      logger.warn("User role {} attempted to remove from cart - forbidden", userRole);
      request.setAttribute(AttributeParameter.ERROR, "Only customers can checkout shopping cart");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.BOOKS_REDIRECT);
      return;
    }

    UUID customerId = getCustomerIdFromSession(session, currentUser);
    logger.debug("Processing checkout for customerId: {}", customerId);

    Cart cart = cartService.findOrCreateCartForCustomer(customerId);
    logger.debug("Cart retrieved: {}", cart.getId());

    List<Item> items = itemService.findItemsByCartId(cart.getId());
    logger.debug("Found {} items in cart for checkout", items.size());

    if (items.isEmpty()) {
      logger.warn("Attempted to checkout with empty cart for customer {}", customerId);
      session.setAttribute(AttributeParameter.ERROR, "Your cart is empty");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.CART_REDIRECT);
      return;
    }

    String deliveryDateStr = request.getParameter("deliveryDate");
    Date deliveryDate = parseDeliveryDate(deliveryDateStr);
    UUID orderId = orderService.createOrderFromCart(cart, items, deliveryDate);
    logger.info("Order created successfully: {} for customer {}", orderId, customerId);

    itemService.clearCart(cart.getId());
    logger.info("Cart cleared after successful checkout: {}", cart.getId());

    session.setAttribute(AttributeParameter.SUCCESS_MESSAGE,
        String.format("Order #%s created successfully! Delivery scheduled for %s",
            orderId.toString().substring(0, 8), deliveryDateStr));

    session.removeAttribute("currentCart");

    String redirectUrl = request.getContextPath() + PageParameter.Path.ORDER_CONFIRMATION_REDIRECT +
        PageParameter.Path.ORDER_ID_REDIRECT + orderId;
    logger.debug("Redirecting to order confirmation: {}", redirectUrl);
    response.sendRedirect(redirectUrl);
  }

  private Date parseDeliveryDate(String deliveryDateStr) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      dateFormat.setLenient(false);
      return dateFormat.parse(deliveryDateStr);
    } catch (ParseException e) {
      logger.warn("Failed to parse delivery date with format yyyy-MM-dd, trying other formats");
      return null;
    }
  }
}