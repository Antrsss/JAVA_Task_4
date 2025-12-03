package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.OrderDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Date;

class OrderDaoImpl implements OrderDao {

  private static final String INSERT_ORDER = """
      INSERT INTO orders (id, customer_id, purchase_date, delivery_date, order_price)
      VALUES (?, ?, ?, ?, ?)
      """;

  private static final String SELECT_ROLE_BY_CUSTOMER_ID = """
        SELECT id, customer_id, purchase_date, delivery_date, order_price
        FROM orders
        WHERE customer_id = ?
        ORDER BY purchase_date DESC
        """;

  @Override
  public void create(Order order) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {

      UUID orderId = UUID.randomUUID();
      statement.setObject(1, orderId);
      statement.setObject(2, order.getCustomerId());
      statement.setTimestamp(3, order.getPurchaseDate());
      statement.setDate(4, new Date(order.getDeliveryDate().getTime()));
      statement.setDouble(5, order.getOrderPrice());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Creating order failed, no rows affected.");
      }

      order.setId(orderId);

    } catch (SQLException e) {
      throw new DaoException("Error creating order", e);
    }
  }

  @Override
  public List<Order> findOrdersByCustomerId(UUID id) throws DaoException {
    List<Order> orders = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
      PreparedStatement statement = connection.prepareStatement(SELECT_ROLE_BY_CUSTOMER_ID)
    ) {

      statement.setObject(1, id);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Order order = extractOrderFromResultSet(resultSet);
          orders.add(order);
        }
      }
    } catch (SQLException e) {
      throw new DaoException("Error getting orders for customer with id: " + id, e);
    }

    return orders;
  }

  private Order extractOrderFromResultSet(ResultSet resultSet) throws SQLException {
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