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
import java.util.List;

public class ListBooksCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private final BookService bookService = new BookServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    logger.debug("Executing ListBooksCommand");

    try {
      List<Book> books = bookService.getAllBooks();
      logger.info("Retrieved {} books for display", books.size());

      request.setAttribute("books", books);
      request.setAttribute("pageTitle", "Book Catalog");

      request.getRequestDispatcher("/WEB-INF/jsp/book/list.jsp").forward(request, response);

    } catch (ServiceException e) {
      logger.error("Error executing ListBooksCommand", e);
      request.setAttribute("errorMessage", "Unable to load book catalog. Please try again later.");
      request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
    }
  }
}