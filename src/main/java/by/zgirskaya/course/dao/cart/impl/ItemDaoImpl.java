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
import java.util.Optional;
import java.util.UUID;

public class ItemDaoImpl implements ItemDao {
  private static final Logger logger = LogManager.getLogger();

  private static final String INSERT_ITEM = """
        INSERT INTO items (id, order_id, book_id, quantity, total_price, unit_price)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

  private static final String SELECT_ITEMS_BY_ORDER_ID = """
        SELECT id, order_id, book_id, quantity, total_price, unit_price
        FROM items WHERE order_id = ? ORDER BY book_id
        """;

  private static final String SELECT_ITEM_BY_ID = """
        SELECT id, order_id, book_id, quantity, total_price, unit_price
        FROM items
        WHERE id = ?
        """;

  private static final String SELECT_ITEM_BY_ORDER_AND_BOOK = """
        SELECT id, order_id, book_id, quantity, total_price, unit_price
        FROM items
        WHERE order_id = ? AND book_id = ?
        """;

  private static final String UPDATE_ITEM = """
        UPDATE items
        SET order_id = ?, book_id = ?, quantity = ?, total_price = ?, unit_price = ?
        WHERE id = ?
        """;

  private static final String DELETE_ITEM_BY_ID = "DELETE FROM items WHERE id = ?";

  private static final String DELETE_ITEMS_BY_ORDER_ID = "DELETE FROM items WHERE order_id = ?";

  private static final String INCREASE_ITEM_QUANTITY = """
        UPDATE items
        SET quantity = quantity + 1, total_price = total_price + unit_price
        WHERE id = ?
        """;

  private static final String DECREASE_ITEM_QUANTITY = """
        UPDATE items
        SET quantity = quantity - 1, total_price = total_price - unit_price
        WHERE id = ? AND quantity > 1
        """;

  private static final String DELETE_ITEM_IF_SINGLE = """
        DELETE FROM items
        WHERE id = ? AND quantity = 1
        """;

  @Override
  public void create(Item item) throws DaoException {
    logger.debug("Creating item for order: {}, book: {}", item.getOrderId(), item.getBookId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ITEM)) {

      UUID itemId = (item.getId() != null) ? item.getId() : UUID.randomUUID();

      statement.setObject(1, itemId);
      statement.setObject(2, item.getOrderId());
      statement.setObject(3, item.getBookId());
      statement.setInt(4, item.getQuantity());
      statement.setDouble(5, item.getTotalPrice() != null ? item.getTotalPrice() : 0.0);
      statement.setDouble(6, item.getUnitPrice() != null ? item.getUnitPrice() : 0.0);

      int affectedRows = statement.executeUpdate();
      logger.debug("Item creation executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Creating item failed - no rows affected for order: {}, book: {}",
            item.getOrderId(), item.getBookId());
        throw new DaoException("Creating item failed, no rows affected.");
      }

      item.setId(itemId);
      logger.info("Item created successfully: {} (Order: {}, Book: {}, Qty: {}, Price: {})",
          itemId, item.getOrderId(), item.getBookId(), item.getQuantity(), item.getTotalPrice());

    } catch (SQLException e) {
      logger.error("Error creating item for order: {}, book: {}",
          item.getOrderId(), item.getBookId(), e);
      throw new DaoException("Error creating item", e);
    }
  }

  @Override
  public Optional<Item> findById(UUID id) throws DaoException {
    logger.debug("Finding item by ID: {}", id);

    if (id == null) {
      logger.warn("Attempted to find item with null ID");
      return Optional.empty();
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ITEM_BY_ID)) {

      statement.setObject(1, id);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Item item = extractItemFromResultSet(resultSet);
          logger.debug("Found item: {} (Order: {}, Book: {}, Quantity: {})",
              id, item.getOrderId(), item.getBookId(), item.getQuantity());
          return Optional.of(item);
        } else {
          logger.debug("Item not found: {}", id);
          return Optional.empty();
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding item by ID: {}", id, e);
      throw new DaoException("Error finding item with id: " + id, e);
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

      statement.setObject(1, item.getOrderId());
      statement.setObject(2, item.getBookId());
      statement.setInt(3, item.getQuantity());
      statement.setDouble(4, item.getTotalPrice() != null ? item.getTotalPrice() : 0.0);
      statement.setDouble(5, item.getUnitPrice() != null ? item.getUnitPrice() : 0.0);
      statement.setObject(6, item.getId());

      int affectedRows = statement.executeUpdate();
      logger.debug("Item update executed, affected rows: {}", affectedRows);

      boolean updated = affectedRows > 0;
      logger.info("Item update {}: {} (Order: {}, Book: {}, Qty: {}, Price: {})",
          updated ? "successful" : "failed", item.getId(),
          item.getOrderId(), item.getBookId(), item.getQuantity(), item.getTotalPrice());

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

  @Override
  public Item findByOrderIdAndBookId(UUID orderId, UUID bookId) throws DaoException {
    logger.debug("Finding item by order: {} and book: {}", orderId, bookId);

    if (orderId == null || bookId == null) {
      logger.warn("Attempted to find item with null orderId or bookId");
      return null;
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ITEM_BY_ORDER_AND_BOOK)) {

      statement.setObject(1, orderId);
      statement.setObject(2, bookId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Item item = extractItemFromResultSet(resultSet);
          logger.debug("Found item: {} (Order: {}, Book: {}, Quantity: {})",
              item.getId(), orderId, bookId, item.getQuantity());
          return item;
        } else {
          logger.debug("Item not found for order: {} and book: {}", orderId, bookId);
          return null;
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding item by order: {} and book: {}", orderId, bookId, e);
      throw new DaoException("Error finding item by order and book", e);
    }
  }

  @Override
  public void deleteItemsByOrderId(UUID orderId) throws DaoException {
    logger.debug("Deleting all items for order: {}", orderId);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_ITEMS_BY_ORDER_ID)) {

      statement.setObject(1, orderId);

      int affectedRows = statement.executeUpdate();
      logger.debug("Items deletion executed, affected rows: {}", affectedRows);

      logger.info("Deleted {} items for order: {}", affectedRows, orderId);

    } catch (SQLException e) {
      logger.error("Error deleting items for order: {}", orderId, e);
      throw new DaoException("Error deleting items for order with id: " + orderId, e);
    }
  }

  private Item extractItemFromResultSet(ResultSet resultSet) throws SQLException {
    logger.debug("Extracting item from ResultSet");

    UUID id = (UUID) resultSet.getObject(TableColumns.Item.ID);
    UUID orderId = (UUID) resultSet.getObject(TableColumns.Item.ORDER_ID);
    UUID bookId = (UUID) resultSet.getObject(TableColumns.Item.BOOK_ID);
    int quantity = resultSet.getInt(TableColumns.Item.QUANTITY);
    Double totalPrice = resultSet.getDouble(TableColumns.Item.TOTAL_PRICE);
    Double unitPrice = resultSet.getDouble(TableColumns.Item.UNIT_PRICE);

    if (resultSet.wasNull()) {
      totalPrice = null;
      unitPrice = null;
    }

    Item item = new Item(orderId, bookId, quantity, unitPrice);

    logger.debug("Extracted item: {} (Order: {}, Book: {}, Qty: {}, UnitPrice: {}, Total: {})",
        id, orderId, bookId, quantity, unitPrice, totalPrice);

    return item;
  }

  private int getItemQuantity(UUID itemId) throws DaoException {
    logger.debug("Getting quantity for item: {}", itemId);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement("SELECT quantity FROM items WHERE id = ?")) {

      statement.setObject(1, itemId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          int quantity = resultSet.getInt("quantity");
          logger.debug("Quantity for item {}: {}", itemId, quantity);
          return quantity;
        }
      }

    } catch (SQLException e) {
      logger.error("Error getting item quantity for id: {}", itemId, e);
      throw new DaoException("Error getting item quantity for id: " + itemId, e);
    }

    logger.warn("Item not found for quantity check: {}", itemId);
    return 0;
  }
}