package by.zgirskaya.course.dao.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.ItemDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.Item;
import by.zgirskaya.course.util.TableColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemDaoImpl implements ItemDao {
  private static final Logger logger = LogManager.getLogger();

  private static final String INSERT_ITEM = """
        INSERT INTO items (id, cart_id, order_id, book_id, quantity, unit_price, total_price)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

  private static final String SELECT_ITEMS_BY_CART_ID = """
        SELECT id, cart_id, order_id, book_id, quantity, unit_price, total_price
        FROM items WHERE cart_id = ? ORDER BY book_id
        """;

  private static final String SELECT_ITEMS_BY_ORDER_ID = """
        SELECT id, cart_id, order_id, book_id, quantity, unit_price, total_price
        FROM items WHERE order_id = ? ORDER BY book_id
        """;

  private static final String UPDATE_ITEM = """
        UPDATE items
        SET cart_id = ?, order_id = ?, book_id = ?, quantity = ?, unit_price = ?, total_price = ?
        WHERE id = ?
        """;

  private static final String FIND_ITEM_BY_ID = """
    SELECT id, cart_id, order_id, book_id, quantity, unit_price, total_price
    FROM items
    WHERE id = ?
    """;

  private static final String FIND_ITEM_BY_CART_AND_BOOK = """
        SELECT id, cart_id, order_id, book_id, quantity, unit_price, total_price
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
      statement.setDouble(6, item.getUnitPrice());
      statement.setDouble(7, item.getTotalPrice());

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
          item.getCartId(), item.getBookId(), e);
      throw new DaoException("Error creating item", e);
    }
  }

  @Override
  public void update(Item item) throws DaoException {
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
      statement.setDouble(5, item.getUnitPrice());
      statement.setDouble(6, item.getTotalPrice());
      statement.setObject(7, item.getId());

      int affectedRows = statement.executeUpdate();
      logger.debug("Item update executed, affected rows: {}", affectedRows);

      boolean updated = affectedRows > 0;
      logger.info("Item update {}: {} (Cart: {}, Book: {}, Qty: {}, Price: {})",
          updated ? "successful" : "failed", item.getId(),
          item.getCartId(), item.getBookId(), item.getQuantity(), item.getTotalPrice());

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

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(FIND_ITEM_BY_ID)) {

      statement.setObject(1, itemId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return extractItemFromResultSet(resultSet);
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

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(FIND_ITEM_BY_CART_AND_BOOK)) {

      statement.setObject(1, cartId);
      statement.setObject(2, bookId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return extractItemFromResultSet(resultSet);
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

    UUID id = (UUID) resultSet.getObject(TableColumn.Item.ID);
    UUID cartId = (UUID) resultSet.getObject(TableColumn.Item.CART_ID);
    UUID orderId = (UUID) resultSet.getObject(TableColumn.Item.ORDER_ID);
    UUID bookId = (UUID) resultSet.getObject(TableColumn.Item.BOOK_ID);
    int quantity = resultSet.getInt(TableColumn.Item.QUANTITY);
    Double unitPrice = resultSet.getDouble(TableColumn.Item.UNIT_PRICE);
    Double totalPrice = resultSet.getDouble(TableColumn.Item.TOTAL_PRICE);

    Item item = new Item(id, cartId, orderId, bookId, quantity, unitPrice);

    logger.debug("Extracted item: {} (Cart: {}, Order: {}, Book: {}, Qty: {}, UnitPrice: {}, Total: {})",
        id, cartId, orderId, bookId, quantity, unitPrice, totalPrice);

    return item;
  }
}