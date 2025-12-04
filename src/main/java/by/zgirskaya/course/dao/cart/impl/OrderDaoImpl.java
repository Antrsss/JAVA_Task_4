package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.OrderDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Order;
import by.zgirskaya.course.util.TableColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Date;

public class OrderDaoImpl implements OrderDao {
  private static final Logger logger = LogManager.getLogger();

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
    logger.debug("Creating order for customer: {}", order.getCustomerId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {

      UUID orderId = UUID.randomUUID();
      statement.setObject(1, orderId);
      statement.setObject(2, order.getCustomerId());
      statement.setTimestamp(3, order.getPurchaseDate());
      statement.setDate(4, new Date(order.getDeliveryDate().getTime()));
      statement.setDouble(5, order.getOrderPrice());

      int affectedRows = statement.executeUpdate();
      logger.debug("Order creation executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Creating order failed - no rows affected for customer: {}", order.getCustomerId());
        throw new DaoException("Creating order failed, no rows affected.");
      }

      order.setId(orderId);
      logger.info("Order created successfully: {} (Customer: {}, Price: {})",
          orderId, order.getCustomerId(), order.getOrderPrice());

    } catch (SQLException e) {
      logger.error("Error creating order for customer: {}", order.getCustomerId(), e);
      throw new DaoException("Error creating order", e);
    }
  }

  @Override
  public List<Order> findOrdersByCustomerId(UUID id) throws DaoException {
    logger.debug("Finding orders for customer: {}", id);

    List<Order> orders = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ROLE_BY_CUSTOMER_ID)) {

      statement.setObject(1, id);

      try (ResultSet resultSet = statement.executeQuery()) {
        int count = 0;
        while (resultSet.next()) {
          Order order = extractOrderFromResultSet(resultSet);
          orders.add(order);
          count++;
        }
        logger.debug("Found {} orders for customer: {}", count, id);
      }
    } catch (SQLException e) {
      logger.error("Error getting orders for customer: {}", id, e);
      throw new DaoException("Error getting orders for customer with id: " + id, e);
    }

    return orders;
  }

  private Order extractOrderFromResultSet(ResultSet resultSet) throws SQLException {
    logger.debug("Extracting order from ResultSet");

    UUID id = (UUID) resultSet.getObject(TableColumns.Order.ID);
    UUID customerId = (UUID) resultSet.getObject(TableColumns.Order.CUSTOMER_ID);
    Timestamp purchaseDate = resultSet.getTimestamp(TableColumns.Order.PURCHASE_DATE);
    Date deliveryDate = resultSet.getDate(TableColumns.Order.DELIVERY_DATE);
    Double orderPrice = resultSet.getDouble(TableColumns.Order.ORDER_PRICE);

    Order order = new Order(customerId, purchaseDate, deliveryDate, orderPrice);
    order.setId(id);

    logger.debug("Extracted order: {} (Customer: {}, Date: {}, Price: {})",
        id, customerId, purchaseDate, orderPrice);

    return order;
  }
}