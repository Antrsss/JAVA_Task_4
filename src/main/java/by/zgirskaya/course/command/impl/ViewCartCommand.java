package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Cart;
import by.zgirskaya.course.model.Item;
import by.zgirskaya.course.model.AbstractUserModel;
import by.zgirskaya.course.service.CartService;
import by.zgirskaya.course.service.ItemService;
import by.zgirskaya.course.service.impl.CartServiceImpl;
import by.zgirskaya.course.service.impl.ItemServiceImpl;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ViewCartCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final CartService cartService = new CartServiceImpl();
  private final ItemService itemService = new ItemServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing ViewCartCommand");

    Optional<AbstractUserModel> userOptional = getUserFromSession(request, response);
    if (userOptional.isEmpty()) {
      return;
    }
    AbstractUserModel currentUser = userOptional.get();
    HttpSession session = request.getSession();

    String userRole = (String) session.getAttribute(AttributeParameter.USER_ROLE);
    if (!AuthParameter.Roles.CUSTOMER.equals(userRole)) {
      logger.warn("User role {} attempted to remove from cart - forbidden", userRole);
      request.setAttribute(AttributeParameter.ERROR, "Only customers can view items from the shopping cart");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.BOOKS_REDIRECT);
      return;
    }

    UUID customerId = getCustomerIdFromSession(session, currentUser);
    if (customerId == null) {
      logger.error("Customer ID not found in session");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.LOGIN_REDIRECT);
      return;
    }

    logger.debug("Processing cart for customerId: {}", customerId);

    Cart cart = cartService.findOrCreateCartForCustomer(customerId);
    logger.debug("Cart retrieved: {}", cart.getId());

    List<Item> items = itemService.findItemsByCartId(cart.getId());
    logger.debug("Found {} items in cart", items.size());

    double orderTotal = cartService.calculateCartTotal(cart.getId());

    request.setAttribute(AttributeParameter.CART, cart);
    request.setAttribute(AttributeParameter.ITEMS, items);
    request.setAttribute(AttributeParameter.TOTAL_PRICE, orderTotal);
    request.setAttribute(AttributeParameter.PAGE_TITLE, "Shopping Cart");

    request.getRequestDispatcher(PageParameter.Jsp.CART_CONTENT).forward(request, response);
  }
}