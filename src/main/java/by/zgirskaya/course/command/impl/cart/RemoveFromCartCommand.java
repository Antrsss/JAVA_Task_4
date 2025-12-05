package by.zgirskaya.course.command.impl.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.ItemService;
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
import java.util.UUID;

public class RemoveFromCartCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final ItemService itemService = new ItemServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing RemoveFromCartCommand");

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
      logger.warn("User role {} attempted to remove from cart - forbidden", userRole);
      request.setAttribute(AttributeParameters.ERROR, "Only customers can remove items from the shopping cart");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
      return;
    }

    try {
      String itemIdStr = request.getParameter("itemId");

      logger.debug("Removing item from cart - itemId: {}", itemIdStr);

      if (itemIdStr == null || itemIdStr.isEmpty()) {
        logger.error("Item ID is required");
        session.setAttribute(AttributeParameters.ERROR, "Item ID is required");
        response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
        return;
      }

      UUID itemId = UUID.fromString(itemIdStr);

      itemService.removeItemFromCart(itemId);
      logger.info("Item {} removed from cart by user {}", itemId, currentUser.getId());

      session.setAttribute(AttributeParameters.SUCCESS_MESSAGE, "Item removed from cart successfully!");

      response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);

    } catch (IllegalArgumentException e) {
      logger.error("Invalid UUID format for itemId", e);
      session.setAttribute(AttributeParameters.ERROR, "Invalid item identifier");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
    } catch (ServiceException e) {
      logger.error("Error removing item from cart", e);
      session.setAttribute(AttributeParameters.ERROR, "Unable to remove item from cart: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
    } catch (Exception e) {
      logger.error("Unexpected error removing item from cart", e);
      session.setAttribute(AttributeParameters.ERROR, "An unexpected error occurred while removing item from cart");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.CART_REDIRECT);
    }
  }
}