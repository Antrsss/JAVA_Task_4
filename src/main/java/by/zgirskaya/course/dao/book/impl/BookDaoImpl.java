package by.zgirskaya.course.dao.book.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.book.BookDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.book.Book;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookDaoImpl implements BookDao {
  private static final Logger logger = LogManager.getLogger();

  private static final String SELECT_ALL_BOOKS = """
        SELECT id, title, price, publisher_id, discount_id, quantity
        FROM books
        ORDER BY title
        """;

  private static final String SELECT_BOOK_BY_ID = """
        SELECT id, title, price, publisher_id, discount_id, quantity
        FROM books
        WHERE id = ?
        """;

  @Override
  public List<Book> getAllBooks() throws DaoException {
    logger.debug("Attempting to retrieve all books");

    List<Book> books = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BOOKS);
         ResultSet resultSet = statement.executeQuery()) {

      logger.debug("Executing query to retrieve all books");

      while (resultSet.next()) {
        Book book = mapResultSetToBook(resultSet);
        books.add(book);
        logger.trace("Mapped book: {} (ID: {})", book.getTitle(), book.getId());
      }

      logger.info("Successfully retrieved {} books", books.size());
      return books;

    } catch (SQLException e) {
      logger.error("Error retrieving all books", e);
      throw new DaoException("Error retrieving all books", e);
    }
  }

  @Override
  public Book findBookById(UUID id) throws DaoException {
    logger.debug("Finding book by ID: {}", id);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_BOOK_BY_ID)) {

      statement.setObject(1, id);
      logger.debug("Executing query to find book by ID: {}", id);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Book book = mapResultSetToBook(resultSet);
          logger.info("Found book by ID {}: {}", id, book.getTitle());
          return book;
        } else {
          logger.warn("Book not found by ID: {}", id);
          throw new DaoException("Book not found by ID: " + id);
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding book by ID: {}", id, e);
      throw new DaoException("Error finding book by ID: " + id, e);
    }
  }

  private Book mapResultSetToBook(ResultSet resultSet) throws SQLException {
    logger.debug("Mapping ResultSet to Book object");

    UUID id = (UUID) resultSet.getObject("id");
    String title = resultSet.getString("title");
    Double price = resultSet.getDouble("price");
    UUID publisherId = (UUID) resultSet.getObject("publisher_id");
    UUID discountId = (UUID) resultSet.getObject("discount_id");
    int quantity = resultSet.getInt("quantity");
    if (resultSet.wasNull()) {
      quantity = 0;
    }

    Book book = new Book(id, publisherId, discountId, title, price, quantity);

    logger.trace("Book mapped: ID={}, Title={}, Price={}, Quantity={}",
        id, title, price, quantity);
    return book;
  }
}