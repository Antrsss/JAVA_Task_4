package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.ItemDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.util.TableColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemDaoImpl implements ItemDao {
  private static final Logger logger = LogManager.getLogger();

  // ИЗМЕНЕНО: Добавлено cart_id в INSERT
  private static final String INSERT_ITEM = """
        INSERT INTO items (id, cart_id, order_id, book_id, quantity, total_price, unit_price)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

  // ИЗМЕНЕНО: Добавлен выбор по cart_id
  private static final String SELECT_ITEMS_BY_CART_ID = """
        SELECT id, cart_id, order_id, book_id, quantity, total_price, unit_price
        FROM items WHERE cart_id = ? ORDER BY book_id
        """;

  private static final String SELECT_ITEMS_BY_ORDER_ID = """
        SELECT id, cart_id, order_id, book_id, quantity, total_price, unit_price
        FROM items WHERE order_id = ? ORDER BY book_id
        """;

  // ИЗМЕНЕНО: Добавлено cart_id в UPDATE
  private static final String UPDATE_ITEM = """
        UPDATE items
        SET cart_id = ?, order_id = ?, book_id = ?, quantity = ?, total_price = ?, unit_price = ?
        WHERE id = ?
        """;

  private static final String FIND_ITEM_BY_ID = """
    SELECT id, cart_id, order_id, book_id, quantity, total_price, unit_price
    FROM items
    WHERE id = ?
    """;

  // ИЗМЕНЕНО: Поиск по cart_id вместо order_id
  private static final String FIND_ITEM_BY_CART_AND_BOOK = """
        SELECT id, cart_id, order_id, book_id, quantity, total_price, unit_price
        FROM items
        WHERE cart_id = ? AND book_id = ?
        """;

  private static final String DELETE_ITEM_BY_ID = "DELETE FROM items WHERE id = ?";

  @Override
  public void create(Item item) throws DaoException {
    logger.debug("Creating item for cart: {}, book: {}", item.getCartId(), item.getBookId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ITEM)) {

      UUID itemId = (item.getId() != null) ? item.getId() : UUID.randomUUID();

      statement.setObject(1, itemId);
      statement.setObject(2, item.getCartId());
      statement.setObject(3, item.getOrderId());
      statement.setObject(4, item.getBookId());
      statement.setInt(5, item.getQuantity());
      statement.setDouble(6, item.getTotalPrice() != null ? item.getTotalPrice() : 0.0);
      statement.setDouble(7, item.getUnitPrice() != null ? item.getUnitPrice() : 0.0);

      int affectedRows = statement.executeUpdate();
      logger.debug("Item creation executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Creating item failed - no rows affected for cart: {}, book: {}",
            item.getCartId(), item.getBookId());
        throw new DaoException("Creating item failed, no rows affected.");
      }

      item.setId(itemId);
      logger.info("Item created successfully: {} (Cart: {}, Book: {}, Qty: {}, Price: {})",
          itemId, item.getCartId(), item.getBookId(), item.getQuantity(), item.getTotalPrice());

    } catch (SQLException e) {
      logger.error("Error creating item for cart: {}, book: {}",
          item.getCartId(), item.getBookId(), e);  // ИЗМЕНЕНО
      throw new DaoException("Error creating item", e);
    }
  }

  @Override
  public boolean update(Item item) throws DaoException {
    logger.debug("Updating item: {}", item.getId());

    if (item.getId() == null) {
      throw new DaoException("Cannot update item without ID");
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(UPDATE_ITEM)) {

      statement.setObject(1, item.getCartId());
      statement.setObject(2, item.getOrderId());
      statement.setObject(3, item.getBookId());
      statement.setInt(4, item.getQuantity());
      statement.setDouble(5, item.getTotalPrice() != null ? item.getTotalPrice() : 0.0);
      statement.setDouble(6, item.getUnitPrice() != null ? item.getUnitPrice() : 0.0);
      statement.setObject(7, item.getId());

      int affectedRows = statement.executeUpdate();
      logger.debug("Item update executed, affected rows: {}", affectedRows);

      boolean updated = affectedRows > 0;
      logger.info("Item update {}: {} (Cart: {}, Book: {}, Qty: {}, Price: {})",
          updated ? "successful" : "failed", item.getId(),
          item.getCartId(), item.getBookId(), item.getQuantity(), item.getTotalPrice());

      return updated;

    } catch (SQLException e) {
      logger.error("Error updating item: {}", item.getId(), e);
      throw new DaoException("Error updating item with id: " + item.getId(), e);
    }
  }

  @Override
  public boolean delete(UUID id) throws DaoException {
    logger.debug("Deleting item by ID: {}", id);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_ITEM_BY_ID)) {

      statement.setObject(1, id);

      int affectedRows = statement.executeUpdate();
      logger.debug("Item deletion executed, affected rows: {}", affectedRows);

      boolean deleted = affectedRows > 0;
      logger.info("Item deletion {}: {}", deleted ? "successful" : "failed", id);

      return deleted;

    } catch (SQLException e) {
      logger.error("Error deleting item: {}", id, e);
      throw new DaoException("Error deleting item with id: " + id, e);
    }
  }

  @Override
  public Item findById(UUID itemId) throws DaoException {
    logger.debug("Finding item by ID: {}", itemId);

    if (itemId == null) {
      logger.warn("Attempted to find item with null ID");
      return null;
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(FIND_ITEM_BY_ID)) {

      statement.setObject(1, itemId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return extractItemFromResultSet(resultSet);  // ИЗМЕНЕНО: используем новый метод
        }
      }

      logger.debug("No item found with ID: {}", itemId);
      return null;

    } catch (SQLException e) {
      logger.error("Failed to find item by ID: {}", itemId, e);
      throw new DaoException("Failed to find item by ID: " + e.getMessage(), e);
    }
  }

  @Override
  public Item findItemByCartAndBook(UUID cartId, UUID bookId) throws DaoException {
    logger.debug("Finding item by cartId={} and bookId={}", cartId, bookId);

    if (cartId == null || bookId == null) {
      return null;
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(FIND_ITEM_BY_CART_AND_BOOK)) {

      statement.setObject(1, cartId);
      statement.setObject(2, bookId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return extractItemFromResultSet(resultSet);  // ИЗМЕНЕНО: используем новый метод
        }
      }

      logger.debug("No item found for cartId={} and bookId={}", cartId, bookId);
      return null;

    } catch (SQLException e) {
      logger.error("Failed to find item by cartId={} and bookId={}", cartId, bookId, e);
      throw new DaoException("Failed to find item by cart and book: " + e.getMessage(), e);
    }
  }

  @Override
  public List<Item> findItemsByOrderId(UUID orderId) throws DaoException {
    logger.debug("Getting items for order: {}", orderId);

    List<Item> items = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ITEMS_BY_ORDER_ID)) {

      statement.setObject(1, orderId);

      try (ResultSet resultSet = statement.executeQuery()) {
        int count = 0;
        while (resultSet.next()) {
          Item item = extractItemFromResultSet(resultSet);
          items.add(item);
          count++;
        }
        logger.debug("Found {} items for order: {}", count, orderId);
      }

    } catch (SQLException e) {
      logger.error("Error getting items for order: {}", orderId, e);
      throw new DaoException("Error getting items for order with id: " + orderId, e);
    }

    return items;
  }

  // ИЗМЕНЕНО: Добавлен новый метод для поиска по cart_id
  @Override
  public List<Item> findItemsByCartId(UUID cartId) throws DaoException {
    logger.debug("Getting items for cart: {}", cartId);

    List<Item> items = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ITEMS_BY_CART_ID)) {

      statement.setObject(1, cartId);

      try (ResultSet resultSet = statement.executeQuery()) {
        int count = 0;
        while (resultSet.next()) {
          Item item = extractItemFromResultSet(resultSet);
          items.add(item);
          count++;
        }
        logger.debug("Found {} items for cart: {}", count, cartId);
      }

    } catch (SQLException e) {
      logger.error("Error getting items for cart: {}", cartId, e);
      throw new DaoException("Error getting items for cart with id: " + cartId, e);
    }

    return items;
  }

  private Item extractItemFromResultSet(ResultSet resultSet) throws SQLException {
    logger.debug("Extracting item from ResultSet");

    UUID id = (UUID) resultSet.getObject("id");
    UUID cartId = (UUID) resultSet.getObject("cart_id");
    UUID orderId = (UUID) resultSet.getObject("order_id");
    UUID bookId = (UUID) resultSet.getObject("book_id");
    int quantity = resultSet.getInt("quantity");
    Double totalPrice = resultSet.getDouble("total_price");
    Double unitPrice = resultSet.getDouble("unit_price");

    if (resultSet.wasNull()) {
      totalPrice = null;
      unitPrice = null;
    }

    Item item = new Item(id, cartId, orderId, bookId, quantity, unitPrice);

    logger.debug("Extracted item: {} (Cart: {}, Order: {}, Book: {}, Qty: {}, UnitPrice: {}, Total: {})",
        id, cartId, orderId, bookId, quantity, unitPrice, totalPrice);

    return item;
  }
}