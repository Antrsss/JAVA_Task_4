package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Book;
import by.zgirskaya.course.service.BookService;
import by.zgirskaya.course.service.impl.BookServiceImpl;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
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
      throws ServiceException, IOException, ServletException {

    logger.debug("Executing ListBooksCommand");

    List<Book> books = bookService.findAllBooks();
    logger.info("Retrieved {} books for display", books.size());

    request.setAttribute(AttributeParameter.BOOKS, books);
    request.setAttribute(AttributeParameter.PAGE_TITLE, PageParameter.Title.BOOK_CATALOG);

    request.getRequestDispatcher(PageParameter.Jsp.BOOK_LIST_CONTENT).forward(request, response);
  }
}