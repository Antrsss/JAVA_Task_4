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

  private static final String SELECT_CURRENT_ORDER_BY_CUSTOMER_ID = """
        SELECT id, customer_id, purchase_date, delivery_date, order_price
        FROM orders
        WHERE customer_id = ?
        ORDER BY purchase_date DESC
        LIMIT 1
        """;

  private static final String SELECT_ORDERS_BY_CUSTOMER_ID_AND_STATUS = """
        SELECT id, customer_id, purchase_date, delivery_date, order_price
        FROM orders
        WHERE customer_id = ?
        ORDER BY purchase_date DESC
        """;

  private static final String UPDATE_ORDER = """
        UPDATE orders
        SET customer_id = ?, purchase_date = ?, delivery_date = ?, order_price = ?
        WHERE id = ?
        """;

  private static final String DELETE_ORDER_BY_ID = """
        DELETE FROM orders
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
  public Order findById(UUID id) throws DaoException {
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

  @Override
  public Order findCurrentOrderByCustomerId(UUID customerId) throws DaoException {
    logger.debug("Finding current order for customer: {}", customerId);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_CURRENT_ORDER_BY_CUSTOMER_ID)) {

      statement.setObject(1, customerId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Order order = extractOrderFromResultSet(resultSet);
          logger.debug("Found current order: {} for customer: {}", order.getId(), customerId);
          return order;
        }
      }

      logger.debug("No current order found for customer: {}", customerId);
      return null;

    } catch (SQLException e) {
      logger.error("Error finding current order for customer: {}", customerId, e);
      throw new DaoException("Error finding current order for customer: " + customerId, e);
    }
  }

  @Override
  public List<Order> findOrdersByCustomerIdAndStatus(UUID customerId, String status) throws DaoException {
    logger.debug("Finding orders for customer: {} with status: {}", customerId, status);

    List<Order> orders = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ORDERS_BY_CUSTOMER_ID_AND_STATUS)) {

      statement.setObject(1, customerId);
      statement.setString(2, status);

      try (ResultSet resultSet = statement.executeQuery()) {
        int count = 0;
        while (resultSet.next()) {
          Order order = extractOrderFromResultSet(resultSet);
          orders.add(order);
          count++;
        }
        logger.debug("Found {} orders for customer: {} with status: {}",
            count, customerId, status);
      }
    } catch (SQLException e) {
      logger.error("Error getting orders for customer: {} with status: {}", customerId, status, e);
      throw new DaoException("Error getting orders for customer: " + customerId + " with status: " + status, e);
    }

    return orders;
  }

  @Override
  public boolean update(Order order) throws DaoException {
    logger.debug("Updating order: {}", order.getId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(UPDATE_ORDER)) {

      statement.setObject(1, order.getCustomerId());

      if (order.getPurchaseDate() != null) {
        statement.setTimestamp(2, order.getPurchaseDate());
      } else {
        statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      }

      if (order.getDeliveryDate() != null) {
        statement.setDate(3, new Date(order.getDeliveryDate().getTime()));
      } else {
        statement.setNull(3, Types.DATE);
      }

      statement.setDouble(4, order.getOrderPrice());
      statement.setObject(5, order.getId());

      int affectedRows = statement.executeUpdate();
      logger.debug("Order update executed, affected rows: {}", affectedRows);

      boolean updated = affectedRows > 0;
      logger.info("Order update {}: {} (Customer: {}, Price: {})",
          updated ? "successful" : "failed", order.getId(),
          order.getCustomerId(), order.getOrderPrice());

      return updated;

    } catch (SQLException e) {
      logger.error("Error updating order: {}", order.getId(), e);
      throw new DaoException("Error updating order with id: " + order.getId(), e);
    }
  }

  @Override
  public boolean delete(UUID id) throws DaoException {
    logger.debug("Deleting order: {}", id);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_ORDER_BY_ID)) {

      statement.setObject(1, id);

      int affectedRows = statement.executeUpdate();
      logger.debug("Order deletion executed, affected rows: {}", affectedRows);

      boolean deleted = affectedRows > 0;
      logger.info("Order deletion {}: {}", deleted ? "successful" : "failed", id);

      return deleted;

    } catch (SQLException e) {
      logger.error("Error deleting order: {}", id, e);
      throw new DaoException("Error deleting order with id: " + id, e);
    }
  }

  private Order extractOrderFromResultSet(ResultSet resultSet) throws SQLException {
    logger.debug("Extracting order from ResultSet");

    UUID id = (UUID) resultSet.getObject(TableColumns.Order.ID);
    UUID customerId = (UUID) resultSet.getObject(TableColumns.Order.CUSTOMER_ID);
    Timestamp purchaseDate = resultSet.getTimestamp(TableColumns.Order.PURCHASE_DATE);
    java.util.Date deliveryDate = resultSet.getDate(TableColumns.Order.DELIVERY_DATE);
    Double orderPrice = resultSet.getDouble(TableColumns.Order.ORDER_PRICE);
    String status = resultSet.getString(TableColumns.Order.STATUS);

    Order order = new Order(id, customerId, purchaseDate, orderPrice);
    order.setDeliveryDate(deliveryDate);

    logger.debug("Extracted order: {} (Customer: {}, Status: {}, Date: {}, Price: {})",
        id, customerId, status, purchaseDate, orderPrice);

    return order;
  }
}