package by.zgirskaya.course.service.book.impl;

import by.zgirskaya.course.dao.book.BookDao;
import by.zgirskaya.course.dao.book.impl.BookDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.book.Book;
import by.zgirskaya.course.service.book.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

public class BookServiceImpl implements BookService {
  private static final Logger logger = LogManager.getLogger();
  private final BookDao bookDao = new BookDaoImpl();

  @Override
  public List<Book> getAllBooks() throws ServiceException {
    logger.debug("Getting all books");

    try {
      List<Book> books = bookDao.getAllBooks();
      logger.info("Successfully retrieved {} books", books.size());
      return books;

    } catch (DaoException e) {
      logger.error("Failed to get all books", e);
      throw new ServiceException("Failed to get all books: " + e.getMessage(), e);
    }
  }

  @Override
  public Book findBookById(UUID id) throws ServiceException {
    logger.debug("Finding book by ID: {}", id);

    try {
      Book book = bookDao.findBookById(id);
      logger.info("Found book by ID {}: {}", id, book.getTitle());
      return book;

    } catch (DaoException e) {
      logger.error("Failed to find book by ID: {}", id, e);
      throw new ServiceException("Failed to find book: " + e.getMessage(), e);
    }
  }
}