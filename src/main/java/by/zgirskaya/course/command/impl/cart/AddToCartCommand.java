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

public class AddToCartCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final CartService cartService = new CartServiceImpl();
  private final ItemService itemService = new ItemServiceImpl();
  private final BookService bookService = new BookServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing AddToCartCommand");

    Optional<AbstractUserModel> userOptional = getUserFromSession(request, response);
    if (userOptional.isEmpty()) {
      return;
    }

    AbstractUserModel currentUser = userOptional.get();
    HttpSession session = request.getSession();

    String userRole = (String) session.getAttribute(AttributeParameter.USER_ROLE);
    if (!AuthParameter.Roles.CUSTOMER.equals(userRole)) {
      request.setAttribute(AttributeParameter.ERROR, "Only customers can add items to the shopping cart");
      response.sendRedirect(request.getContextPath() + PageParameter.Path.BOOKS_REDIRECT);
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

    session.setAttribute(AttributeParameter.SUCCESS_MESSAGE, "Book added to cart successfully!");

    String redirectUrl = request.getContextPath() + PageParameter.Path.CART_REDIRECT + "?action=view";
    logger.debug("Redirecting to: {}", redirectUrl);
    response.sendRedirect(redirectUrl);
  }
}