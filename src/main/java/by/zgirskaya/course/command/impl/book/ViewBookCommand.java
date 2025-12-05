package by.zgirskaya.course.command.impl.book;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.book.Book;
import by.zgirskaya.course.service.book.BookService;
import by.zgirskaya.course.service.book.impl.BookServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

public class ViewBookCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final BookService bookService = new BookServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing ViewBookCommand");

    try {
      // Извлекаем ID книги из пути
      String pathInfo = request.getPathInfo();
      String bookIdStr = extractBookIdFromPath(pathInfo);

      if (bookIdStr == null || bookIdStr.isEmpty()) {
        logger.error("Book ID not found in path: {}", pathInfo);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Book ID is required");
        return;
      }

      // Парсим UUID
      UUID bookId = UUID.fromString(bookIdStr);

      // Получаем книгу из сервиса
      Book book = bookService.findBookById(bookId);

      if (book == null) {
        logger.warn("Book not found with ID: {}", bookId);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
        return;
      }

      // Устанавливаем атрибуты для JSP
      request.setAttribute("book", book);
      request.setAttribute("pageTitle", book.getTitle());

      // Форвардим на JSP страницу
      request.getRequestDispatcher("/WEB-INF/jsp/book/view.jsp").forward(request, response);

    } catch (IllegalArgumentException e) {
      logger.error("Invalid book ID format", e);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID format");
    } catch (ServiceException e) {
      logger.error("Error executing ViewBookCommand", e);
      request.setAttribute("errorMessage", "Unable to load book details. Please try again later.");
      request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
    }
  }

  private String extractBookIdFromPath(String pathInfo) {
    if (pathInfo == null) {
      return null;
    }

    // Путь вида /books/view/{bookId}
    String[] pathParts = pathInfo.split("/");

    for (int i = 0; i < pathParts.length; i++) {
      if ("view".equals(pathParts[i]) && i + 1 < pathParts.length) {
        return pathParts[i + 1];
      }
    }

    return null;
  }
}