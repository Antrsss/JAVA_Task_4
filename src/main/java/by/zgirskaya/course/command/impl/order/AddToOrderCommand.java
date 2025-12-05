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
import java.util.UUID;

public class AddToOrderCommand implements Command {
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
      UUID bookId = UUID.fromString(request.getParameter("bookId"));
      int quantity = Integer.parseInt(request.getParameter("quantity"));

      if (quantity <= 0) {
        session.setAttribute("error", "Quantity must be positive");
        response.sendRedirect(request.getContextPath() + "/books/" + bookId);
        return;
      }

      orderService.getOrCreateCurrentOrder(currentUser.getId());
      orderService.addItemToOrder(currentUser.getId(), bookId, quantity);

      session.setAttribute("successMessage", "Book added to your order");
      response.sendRedirect(request.getContextPath() + "/cart");

    } catch (IllegalArgumentException e) {
      logger.error("Invalid parameter format", e);
      session.setAttribute("error", "Invalid book data");
      response.sendRedirect(request.getContextPath() + "/books");
    }
  }
}