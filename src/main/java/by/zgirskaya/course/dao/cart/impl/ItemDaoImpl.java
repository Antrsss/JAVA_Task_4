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

  private static final String INSERT_ITEM = """
        INSERT INTO items (id, order_id, book_id, quantity, total_price)
        VALUES (?, ?, ?, ?, ?)
        """;

  private static final String SELECT_ITEMS_BY_ORDER_ID = """
        SELECT id, order_id, book_id, quantity, total_price
        FROM items WHERE order_id = ? ORDER BY book_id
        """;

  private static final String DELETE_ITEM_BY_ID = "DELETE FROM items WHERE id = ?";

  private static final String INCREASE_ITEM_QUANTITY = """
        UPDATE items
        SET quantity = quantity + 1, total_price = total_price + (total_price / quantity)
        WHERE id = ?
        """;

  private static final String DECREASE_ITEM_QUANTITY = """
        UPDATE items
        SET quantity = quantity - 1, total_price = total_price - (total_price / quantity)
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

      UUID itemId = UUID.randomUUID();
      statement.setObject(1, itemId);
      statement.setObject(2, item.getOrderId());
      statement.setObject(3, item.getBookId());
      statement.setInt(4, item.getQuantity());
      statement.setDouble(5, item.getTotalPrice());

      int affectedRows = statement.executeUpdate();
      logger.debug("Item creation executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Creating item failed - no rows affected for order: {}, book: {}",
            item.getOrderId(), item.getBookId());
        throw new DaoException("Creating item failed, no rows affected.");
      }

      item.setId(itemId);
      logger.info("Item created successfully: {} (Order: {}, Book: {})",
          itemId, item.getOrderId(), item.getBookId());

    } catch (SQLException e) {
      logger.error("Error creating item for order: {}, book: {}",
          item.getOrderId(), item.getBookId(), e);
      throw new DaoException("Error creating item", e);
    }
  }

  @Override
  public List<Item> getItemsByOrderId(UUID orderId) throws DaoException {
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
  public void increaseItemCount(Item item) throws DaoException {
    logger.debug("Increasing item count for item: {} (Order: {})", item.getId(), item.getOrderId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INCREASE_ITEM_QUANTITY)) {

      statement.setObject(1, item.getId());

      int affectedRows = statement.executeUpdate();
      logger.debug("Item count increase executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Increasing item count failed - no rows affected for item: {}", item.getId());
        throw new DaoException("Increasing item count failed, no rows affected.");
      }

      logger.info("Item count increased successfully: {} (Order: {})", item.getId(), item.getOrderId());

    } catch (SQLException e) {
      logger.error("Error increasing item count for item: {}", item.getId(), e);
      throw new DaoException("Error increasing item count for item with id: " + item.getId(), e);
    }
  }

  @Override
  public void decreaseItemCount(Item item) throws DaoException {
    logger.debug("Decreasing item count for item: {} (Order: {})", item.getId(), item.getOrderId());

    try (Connection connection = DatabaseConnection.getConnection()) {
      int currentQuantity = getItemQuantity(item.getId());
      logger.debug("Current item quantity: {}", currentQuantity);

      if (currentQuantity > 1) {
        try (PreparedStatement statement = connection.prepareStatement(DECREASE_ITEM_QUANTITY)) {
          statement.setObject(1, item.getId());
          statement.executeUpdate();
          logger.debug("Item quantity was decreased");
        }
      } else {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ITEM_IF_SINGLE)) {
          statement.setObject(1, item.getId());
          statement.executeUpdate();
          logger.debug("Item quantity was equal 1, so item was removed");
        }
      }

    } catch (SQLException e) {
      throw new DaoException("Error decreasing item count for item with id: " + item.getId(), e);
    }
  }

  @Override
  public void deleteItemById(UUID itemId) throws DaoException {
    logger.debug("Deleting item by ID: {}", itemId);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_ITEM_BY_ID)) {

      statement.setObject(1, itemId);

      int affectedRows = statement.executeUpdate();
      logger.debug("Item deletion executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Deleting item failed - no rows affected for item: {}", itemId);
        throw new DaoException("Deleting item failed, no rows affected.");
      }

      logger.info("Item deleted successfully: {}", itemId);

    } catch (SQLException e) {
      logger.error("Error deleting item: {}", itemId, e);
      throw new DaoException("Error deleting item with id: " + itemId, e);
    }
  }

  private Item extractItemFromResultSet(ResultSet resultSet) throws SQLException {
    logger.debug("Extracting item from ResultSet");

    UUID id = (UUID) resultSet.getObject(TableColumns.Item.ID);
    UUID orderId = (UUID) resultSet.getObject(TableColumns.Item.ORDER_ID);
    UUID bookId = (UUID) resultSet.getObject(TableColumns.Item.BOOK_ID);
    int quantity = resultSet.getInt(TableColumns.Item.QUANTITY);
    Double totalPrice = resultSet.getDouble(TableColumns.Item.TOTAL_PRICE);

    Item item = new Item(orderId, bookId, quantity, totalPrice);
    item.setId(id);

    logger.debug("Extracted item: {} (Order: {}, Book: {}, Qty: {})",
        id, orderId, bookId, quantity);

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