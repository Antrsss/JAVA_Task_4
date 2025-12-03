package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.ItemDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemDaoImpl implements ItemDao {

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
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ITEM)) {

      UUID itemId = UUID.randomUUID();
      statement.setObject(1, itemId);
      statement.setObject(2, item.getOrderId());
      statement.setObject(3, item.getBookId());
      statement.setInt(4, item.getQuantity());
      statement.setDouble(5, item.getTotalPrice());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Creating item failed, no rows affected.");
      }

      item.setId(itemId);

    } catch (SQLException e) {
      throw new DaoException("Error creating item", e);
    }
  }

  @Override
  public List<Item> getItemsByOrderId(UUID orderId) throws DaoException {
    List<Item> items = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ITEMS_BY_ORDER_ID)) {

      statement.setObject(1, orderId);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Item item = extractItemFromResultSet(resultSet);
          items.add(item);
        }
      }

    } catch (SQLException e) {
      throw new DaoException("Error getting items for order with id: " + orderId, e);
    }

    return items;
  }

  @Override
  public void increaseItemCount(Item item) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INCREASE_ITEM_QUANTITY)) {

      statement.setObject(1, item.getId());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Increasing item count failed, no rows affected.");
      }

    } catch (SQLException e) {
      throw new DaoException("Error increasing item count for item with id: " + item.getId(), e);
    }
  }

  @Override
  public void decreaseItemCount(Item item) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection()) {
      int currentQuantity = getItemQuantity(item.getId());

      if (currentQuantity > 1) {
        try (PreparedStatement statement = connection.prepareStatement(DECREASE_ITEM_QUANTITY)) {
          statement.setObject(1, item.getId());
          statement.executeUpdate();
        }
      } else {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ITEM_IF_SINGLE)) {
          statement.setObject(1, item.getId());
          statement.executeUpdate();
        }
      }

    } catch (SQLException e) {
      throw new DaoException("Error decreasing item count for item with id: " + item.getId(), e);
    }
  }

  @Override
  public void deleteItemById(UUID itemId) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_ITEM_BY_ID)) {

      statement.setObject(1, itemId);

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Deleting item failed, no rows affected.");
      }

    } catch (SQLException e) {
      throw new DaoException("Error deleting item with id: " + itemId, e);
    }
  }

  private Item extractItemFromResultSet(ResultSet resultSet) throws SQLException {
    UUID id = (UUID) resultSet.getObject("id");
    UUID orderId = (UUID) resultSet.getObject("order_id");
    UUID bookId = (UUID) resultSet.getObject("book_id");
    int quantity = resultSet.getInt("quantity");
    Double totalPrice = resultSet.getDouble("total_price");

    Item item = new Item(orderId, bookId, quantity, totalPrice);
    item.setId(id);

    return item;
  }

  private int getItemQuantity(UUID itemId) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement("SELECT quantity FROM items WHERE id = ?")) {

      statement.setObject(1, itemId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt("quantity");
        }
      }

    } catch (SQLException e) {
      throw new DaoException("Error getting item quantity for id: " + itemId, e);
    }

    return 0;
  }
}