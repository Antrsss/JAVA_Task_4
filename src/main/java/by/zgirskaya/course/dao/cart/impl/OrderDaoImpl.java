package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.OrderDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Order;
import by.zgirskaya.course.util.TableColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDaoImpl implements OrderDao {
  private static final Logger logger = LogManager.getLogger();

  private static final String INSERT_ORDER = """
        INSERT INTO orders (id, customer_id, purchase_date, delivery_date, order_price)
        VALUES (?, ?, ?, ?, ?)
        """;

  private static final String SELECT_ORDERS_BY_CUSTOMER_ID = """
        SELECT id, customer_id, purchase_date, delivery_date, order_price
        FROM orders
        WHERE customer_id = ?
        ORDER BY purchase_date DESC
        """;

  private static final String SELECT_ORDER_BY_ID = """
        SELECT id, customer_id, purchase_date, delivery_date, order_price
        FROM orders
        WHERE id = ?
        """;

  @Override
  public void create(Order order) throws DaoException {
    logger.debug("Creating order for customer: {}", order.getCustomerId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_ORDER)) {

      UUID orderId = (order.getId() != null) ? order.getId() : UUID.randomUUID();

      statement.setObject(1, orderId);
      statement.setObject(2, order.getCustomerId());

      if (order.getPurchaseDate() != null) {
        statement.setTimestamp(3, order.getPurchaseDate());
      } else {
        statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
      }

      if (order.getDeliveryDate() != null) {
        statement.setDate(4, new Date(order.getDeliveryDate().getTime()));
      } else {
        statement.setNull(4, Types.DATE);
      }

      if (order.getOrderPrice() != null) {
        statement.setDouble(5, order.getOrderPrice());
        logger.debug("Setting order price: {}", order.getOrderPrice());
      } else {
        statement.setDouble(5, 1000.0);
        logger.warn("Order price is null, setting to 1000.0");
      }

      logger.debug("ORDER_PRICE: {}", order.getOrderPrice());
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
  public Order findOrderById(UUID id) throws DaoException {
    logger.debug("Finding order by ID: {}", id);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ORDER_BY_ID)) {

      statement.setObject(1, id);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Order order = extractOrderFromResultSet(resultSet);
          logger.debug("Found order by ID: {}", id);
          return order;
        }
      }

      logger.debug("Order not found with ID: {}", id);
      return null;

    } catch (SQLException e) {
      logger.error("Error finding order by ID: {}", id, e);
      throw new DaoException("Error finding order by id: " + id, e);
    }
  }

  @Override
  public List<Order> findOrdersByCustomerId(UUID customerId) throws DaoException {
    logger.debug("Finding orders for customer: {}", customerId);

    List<Order> orders = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ORDERS_BY_CUSTOMER_ID)) {

      statement.setObject(1, customerId);

      try (ResultSet resultSet = statement.executeQuery()) {
        int count = 0;
        while (resultSet.next()) {
          Order order = extractOrderFromResultSet(resultSet);
          orders.add(order);
          count++;
        }
        logger.debug("Found {} orders for customer: {}", count, customerId);
      }
    } catch (SQLException e) {
      logger.error("Error getting orders for customer: {}", customerId, e);
      throw new DaoException("Error getting orders for customer with id: " + customerId, e);
    }

    return orders;
  }

  private Order extractOrderFromResultSet(ResultSet resultSet) throws SQLException {
    logger.debug("Extracting order from ResultSet");

    UUID id = (UUID) resultSet.getObject(TableColumn.Order.ID);
    UUID customerId = (UUID) resultSet.getObject(TableColumn.Order.CUSTOMER_ID);
    Timestamp purchaseDate = resultSet.getTimestamp(TableColumn.Order.PURCHASE_DATE);
    java.util.Date deliveryDate = resultSet.getDate(TableColumn.Order.DELIVERY_DATE);
    Double orderPrice = resultSet.getDouble(TableColumn.Order.ORDER_PRICE);

    Order order = new Order(id, customerId, purchaseDate, orderPrice);
    order.setDeliveryDate(deliveryDate);

    logger.debug("Extracted order: {} (Customer: {}, Date: {}, Price: {})",
        id, customerId, purchaseDate, orderPrice);

    return order;
  }
}