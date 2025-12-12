package by.zgirskaya.course.command.impl.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.book.Book;
import by.zgirskaya.course.model.cart.Cart;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.book.BookService;
import by.zgirskaya.course.service.book.impl.BookServiceImpl;
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
import java.util.UUID;

public class AddToCartCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final CartService cartService = new CartServiceImpl();
  private final ItemService itemService = new ItemServiceImpl();
  private final BookService bookService = new BookServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing AddToCartCommand");

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
      request.setAttribute(AttributeParameters.ERROR, "Only customers can add items to the shopping cart");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
      return;
    }

    UUID customerId = getCustomerIdFromSession(session, currentUser);
    String bookIdStr = request.getParameter("bookId");

    logger.debug("Parameters - customerId: {}, bookId: {}", customerId, bookIdStr);

    UUID bookId = UUID.fromString(bookIdStr);
    Book book = bookService.findBookById(bookId);
    double unitPrice = book.getPrice();
    int quantity = 1;

    logger.debug("Adding to cart - bookId: {}, quantity: {}, unitPrice: {}",
        bookId, quantity, unitPrice);

    Cart cart = cartService.findOrCreateCartForCustomer(customerId);
    logger.info("Cart found/created: {}", cart.getId());

    itemService.addItemToCart(cart.getId(), bookId, quantity, unitPrice);
    logger.info("Item added to cart successfully - bookId: {}, cartId: {}", bookId, cart.getId());

    session.setAttribute(AttributeParameters.SUCCESS_MESSAGE, "Book added to cart successfully!");

    String redirectUrl = request.getContextPath() + PageParameters.Path.CART_REDIRECT + "?action=view";
    logger.debug("Redirecting to: {}", redirectUrl);
    response.sendRedirect(redirectUrl);
  }
}