package by.zgirskaya.course.task_4_web.dao.impl;

import by.zgirskaya.course.task_4_web.connection.DatabaseConnection;
import by.zgirskaya.course.task_4_web.dao.BaseDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.cart.Order;

import java.sql.*;
import java.util.List;
import java.util.UUID;

class OrderDaoImpl implements BaseDao<Order> {

  private static final String INSERT_ORDER = """
        INSERT INTO orders (customer_id, purchase_date, delivery_date, order_price) 
        VALUES (?, ?, ?, ?)
        """;

  private static final String SELECT_BY_ID = """
        SELECT id, customer_id, purchase_date, delivery_date, order_price 
        FROM orders WHERE id = ?
        """;

  @Override
  public void create(Order order) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {

      statement.setObject(1, order.getCustomerId());
      statement.setTimestamp(2, order.getPurchaseDate());
      statement.setDate(3, new java.sql.Date(order.getDeliveryDate().getTime()));
      statement.setDouble(4, order.getOrderPrice());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Creating order failed, no rows affected.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          order.setId((UUID) generatedKeys.getObject(1));
        } else {
          throw new DaoException("Creating order failed, no ID obtained.");
        }
      }

    } catch (SQLException e) {
      throw new DaoException("Error creating order", e);
    }
  }

  @Override
  public Order getById(UUID id) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {

      statement.setObject(1, id);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToOrder(resultSet);
        } else {
          throw new DaoException("Order not found with id: " + id);
        }
      }
    } catch (SQLException e) {
      throw new DaoException("Error getting order by id: " + id, e);
    }
  }

  @Override
  public void update(Order order) throws DaoException {

  }

  @Override
  public void delete(UUID id) throws DaoException {

  }

  @Override
  public List<Order> getAll() throws DaoException {
    return List.of();
  }

  private Order mapResultSetToOrder(ResultSet resultSet) throws SQLException {
    UUID id = (UUID) resultSet.getObject("id");
    UUID customerId = (UUID) resultSet.getObject("customer_id");
    Timestamp purchaseDate = resultSet.getTimestamp("purchase_date");
    Date deliveryDate = resultSet.getDate("delivery_date");
    Double orderPrice = resultSet.getDouble("order_price");

    Order order = new Order(customerId, purchaseDate, deliveryDate, orderPrice);
    order.setId(id);
    return order;
  }
}