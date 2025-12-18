package by.zgirskaya.course.command.impl.book;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.book.Book;
import by.zgirskaya.course.service.book.BookService;
import by.zgirskaya.course.service.book.impl.BookServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

public class ViewBookCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private static final String VIEW_PATH = "view";
  private final BookService bookService = new BookServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing ViewBookCommand");

    String pathInfo = request.getPathInfo();
    String bookIdStr = extractBookIdFromPath(pathInfo);

    UUID bookId = UUID.fromString(bookIdStr);
    Book book = bookService.findBookById(bookId);

    if (book == null) {
      logger.warn("Book not found with ID: {}", bookId);
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
      return;
    }

    request.setAttribute(AttributeParameters.BOOK, book);
    request.setAttribute(AttributeParameters.PAGE_TITLE, book.getTitle());

    request.getRequestDispatcher(PageParameters.Jsp.BOOK_DETAILS_CONTENT).forward(request, response);
  }

  private String extractBookIdFromPath(String pathInfo) {
    String[] pathParts = pathInfo.split("/");
    for (int i = 0; i < pathParts.length; i++) {
      if (VIEW_PATH.equals(pathParts[i]) && i + 1 < pathParts.length) {
        return pathParts[i + 1];
      }
    }
    return null;
  }
}