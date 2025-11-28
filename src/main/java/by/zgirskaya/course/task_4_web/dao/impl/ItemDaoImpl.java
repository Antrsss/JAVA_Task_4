package by.zgirskaya.course.task_4_web.dao.impl;

import by.zgirskaya.course.task_4_web.connection.DatabaseConnection;
import by.zgirskaya.course.task_4_web.dao.BaseDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.cart.Item;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class ItemDaoImpl implements BaseDao<Item> {

  private static final String INSERT_ITEM = """
        INSERT INTO order_items (order_id, book_id, quantity, total_price) 
        VALUES (?, ?, ?, ?)
        """;

  private static final String DELETE_ITEM = "DELETE FROM order_items WHERE id = ?";

  @Override
  public void create(Item item) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS)) {

      statement.setObject(1, item.getOrderId());
      statement.setObject(2, item.getBookId());
      statement.setInt(3, item.getQuantity());
      statement.setDouble(4, item.getTotalPrice());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Creating item failed, no rows affected.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          item.setId((UUID) generatedKeys.getObject(1));
        } else {
          throw new DaoException("Creating item failed, no ID obtained.");
        }
      }

    } catch (SQLException e) {
      throw new DaoException("Error creating item", e);
    }
  }
}