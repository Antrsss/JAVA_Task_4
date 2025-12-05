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
      logger.warn("User role {} attempted to add to cart - forbidden", userRole);
      request.setAttribute(AttributeParameters.ERROR, "Only customers can add items to the shopping cart");
      response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
      return;
    }

    try {
      UUID customerId = getCustomerIdFromSession(session, currentUser);
      if (customerId == null) {
        logger.error("Customer ID not found in session");
        response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
        return;
      }

      String bookIdStr = request.getParameter("bookId");
      String quantityStr = request.getParameter("quantity");
      String unitPriceStr = request.getParameter("unitPrice");

      logger.debug("Parameters - customerId: {}, bookId: {}, quantity: {}, unitPrice: {}",
          customerId, bookIdStr, quantityStr, unitPriceStr);

      if (bookIdStr == null || bookIdStr.isEmpty()) {
        logger.error("Book ID is required");
        request.setAttribute(AttributeParameters.ERROR, "Book ID is required");
        response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
        return;
      }

      UUID bookId = UUID.fromString(bookIdStr);
      int quantity = quantityStr != null ? Integer.parseInt(quantityStr) : 1;
      double unitPrice = 0.0;

      // Получаем цену книги
      if (unitPriceStr == null || unitPriceStr.isEmpty()) {
        try {
          Book book = bookService.findBookById(bookId);
          if (book == null) {
            logger.error("Book not found with ID: {}", bookId);
            request.setAttribute(AttributeParameters.ERROR, "Book not found");
            response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
            return;
          }
          unitPrice = book.getPrice();
          logger.debug("Retrieved book price from service: {}", unitPrice);

          // Проверяем наличие книги на складе
          if (book.getQuantity() <= 0) {
            logger.warn("Attempted to add out-of-stock book to cart: {}", bookId);
            request.setAttribute(AttributeParameters.ERROR, "This book is out of stock");
            response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
            return;
          }

          // Проверяем, достаточно ли товара на складе
          if (book.getQuantity() < quantity) {
            logger.warn("Insufficient stock for book: {}. Requested: {}, Available: {}",
                bookId, quantity, book.getQuantity());
            request.setAttribute(AttributeParameters.ERROR,
                String.format("Only %d items available in stock", book.getQuantity()));
            response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
            return;
          }

        } catch (Exception e) {
          logger.warn("Could not get book price, using default: {}", e.getMessage());
          unitPrice = 10.0; // Значение по умолчанию
        }
      } else {
        unitPrice = Double.parseDouble(unitPriceStr);
      }

      logger.debug("Adding to cart - bookId: {}, quantity: {}, unitPrice: {}",
          bookId, quantity, unitPrice);

      Cart cart = cartService.getOrCreateCartForCustomer(customerId);
      logger.info("Cart found/created: {}", cart.getId());

      itemService.addItemToCart(cart.getId(), bookId, quantity, unitPrice);
      logger.info("Item added to cart successfully - bookId: {}, cartId: {}", bookId, cart.getId());

      session.setAttribute(AttributeParameters.SUCCESS_MESSAGE, "Book added to cart successfully!");

      String redirectUrl = request.getContextPath() + PageParameters.Path.CART_REDIRECT + "?action=view";
      logger.debug("Redirecting to: {}", redirectUrl);
      response.sendRedirect(redirectUrl);

    } catch (IllegalArgumentException e) {
      logger.error("Invalid UUID format or number format", e);
      request.setAttribute(AttributeParameters.ERROR, "Invalid parameter format: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
    } catch (Exception e) {
      logger.error("Error adding item to cart", e);
      request.setAttribute(AttributeParameters.ERROR, "Unable to add item to cart: " + e.getMessage());
      response.sendRedirect(request.getContextPath() + PageParameters.Path.BOOKS_REDIRECT);
    }
  }

  private UUID getCustomerIdFromSession(HttpSession session, AbstractUserModel user) {
    Object customerIdObj = session.getAttribute(AttributeParameters.CUSTOMER_ID);

    if (customerIdObj != null) {
      // Если customerId уже есть в сессии
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