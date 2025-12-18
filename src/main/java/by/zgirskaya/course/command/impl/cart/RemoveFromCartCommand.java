package by.zgirskaya.course.command.impl.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.ItemService;
import by.zgirskaya.course.service.cart.impl.ItemServiceImpl;
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
import java.util.Optional;
import java.util.UUID;

public class RemoveFromCartCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final ItemService itemService = new ItemServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing RemoveFromCartCommand");

    Optional<AbstractUserModel> userOptional = getUserFromSession(request, response);
    if (userOptional.isEmpty()) {
      return;
    }
    AbstractUserModel currentUser = userOptional.get();
    HttpSession session = request.getSession();

    String userRole = (String) session.getAttribute(AttributeParameter.USER_ROLE);
    if (!AuthParameter.Roles.CUSTOMER.equals(userRole)) {
      logger.warn("User role {} attempted to remove from cart - forbidden", userRole);
      request.setAttribute(AttributeParameter.ERROR, "Only customers can remove items from the shopping cart");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.BOOKS_REDIRECT);
      return;
    }

    String itemIdStr = request.getParameter("itemId");

    logger.debug("Removing item from cart - itemId: {}", itemIdStr);

    if (itemIdStr == null || itemIdStr.isEmpty()) {
      logger.error("Item ID is required");
      session.setAttribute(AttributeParameter.ERROR, "Item ID is required");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.CART_REDIRECT);
      return;
    }

    UUID itemId = UUID.fromString(itemIdStr);

    itemService.removeItemFromCart(itemId);
    logger.info("Item {} removed from cart by user {}", itemId, currentUser.getId());

    session.setAttribute(AttributeParameter.SUCCESS_MESSAGE, "Item removed from cart successfully!");
    response.sendRedirect(request.getContextPath() + PageParameter.Path.CART_REDIRECT);
  }
}