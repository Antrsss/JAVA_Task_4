package by.zgirskaya.course.command.impl.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Cart;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.CartService;
import by.zgirskaya.course.service.cart.ItemService;
import by.zgirskaya.course.service.cart.impl.CartServiceImpl;
import by.zgirskaya.course.service.cart.impl.ItemServiceImpl;
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
import java.util.List;
import java.util.UUID;

public class ViewCartCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final CartService cartService = new CartServiceImpl();
  private final ItemService itemService = new ItemServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing ViewCartCommand");

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
      logger.warn("User role {} attempted to access cart - forbidden", userRole);
      request.setAttribute(AttributeParameters.ERROR, "Only customers can access the shopping cart");
      request.getRequestDispatcher(PageParameters.Jsp.ERROR_CONTENT).forward(request, response);
      return;
    }

    try {
      UUID customerId = getCustomerIdFromSession(session, currentUser);
      if (customerId == null) {
        logger.error("Customer ID not found in session");
        response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
        return;
      }

      logger.debug("Processing cart for customerId: {}", customerId);

      Cart cart = cartService.getOrCreateCartForCustomer(customerId);
      logger.debug("Cart retrieved: {}", cart.getId());

      List<Item> items = itemService.findItemsByCartId(cart.getId());
      logger.debug("Found {} items in cart", items.size());

      double orderTotal = cartService.calculateCartTotal(cart.getId());

      request.setAttribute("cart", cart);
      request.setAttribute("items", items);
      request.setAttribute("totalPrice", orderTotal);
      request.setAttribute(AttributeParameters.PAGE_TITLE, "Shopping Cart");

      request.getRequestDispatcher("/WEB-INF/jsp/cart/cart-content.jsp").forward(request, response);

    } catch (ServiceException e) {
      logger.error("Error executing ViewCartCommand", e);
      request.setAttribute(AttributeParameters.ERROR, "Unable to load cart. Please try again later.");
      request.getRequestDispatcher(PageParameters.Jsp.ERROR_CONTENT).forward(request, response);
    }
  }

  private UUID getCustomerIdFromSession(HttpSession session, AbstractUserModel user) {
    Object customerIdObj = session.getAttribute("customerId");

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
}