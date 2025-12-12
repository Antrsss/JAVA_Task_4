package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.CartDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Cart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.UUID;

public class CartDaoImpl implements CartDao {
  private static final Logger logger = LogManager.getLogger();

  private static final String SELECT_CART_BY_CUSTOMER_ID = """
        SELECT id, customer_id, created_at, updated_at
        FROM carts
        WHERE customer_id = ?
        """;

  private static final String INSERT_CART = """
        INSERT INTO carts (id, customer_id, created_at, updated_at)
        VALUES (?, ?, ?, ?)
        """;

  private static final String UPDATE_CART = """
        UPDATE carts
        SET updated_at = ?
        WHERE id = ?
        """;

  @Override
  public Cart findCartByCustomerId(UUID customerId) throws DaoException {
    logger.debug("Finding cart by customer ID: {}", customerId);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_CART_BY_CUSTOMER_ID)) {

      statement.setObject(1, customerId);
      logger.debug("Executing query to find cart by customer ID: {}", customerId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Cart cart = mapResultSetToCart(resultSet);
          logger.info("Found cart for customer ID {}: {}", customerId, cart.getId());
          return cart;
        } else {
          logger.debug("Cart not found for customer ID: {}", customerId);
          return null;
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding cart by customer ID: {}", customerId, e);
      throw new DaoException("Error finding cart by customer ID: " + customerId, e);
    }
  }

  @Override
  public Cart createCartForCustomer(UUID customerId) throws DaoException {
    logger.debug("Creating cart for customer ID: {}", customerId);

    Cart existingCart = findCartByCustomerId(customerId);
    if (existingCart != null) {
      logger.debug("Cart already exists for customer ID: {} (Cart ID: {})",
          customerId, existingCart.getId());
      return existingCart;
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_CART)) {

      UUID cartId = UUID.randomUUID();
      Timestamp now = new Timestamp(System.currentTimeMillis());
      Cart cart = new Cart(cartId, customerId, now, now);

      statement.setObject(1, cart.getId());
      statement.setObject(2, cart.getCustomerId());
      statement.setTimestamp(3, cart.getCreatedAt());
      statement.setTimestamp(4, cart.getUpdatedAt());

      int affectedRows = statement.executeUpdate();
      logger.debug("Cart creation executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Creating cart failed - no rows affected for customer: {}", customerId);
        throw new DaoException("Creating cart failed, no rows affected.");
      }

      logger.info("Cart created successfully for customer ID {}: {}", customerId, cartId);
      return cart;

    } catch (SQLException e) {
      logger.error("Error creating cart for customer ID: {}", customerId, e);
      throw new DaoException("Error creating cart for customer ID: " + customerId, e);
    }
  }

  @Override
  public void updateCart(Cart cart) throws DaoException {
    logger.debug("Updating cart: {}", cart.getId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(UPDATE_CART)) {

      Timestamp now = new Timestamp(System.currentTimeMillis());
      statement.setTimestamp(1, now);
      statement.setObject(2, cart.getId());

      int affectedRows = statement.executeUpdate();
      logger.debug("Cart update executed, affected rows: {}", affectedRows);

      boolean updated = affectedRows > 0;
      if (updated) {
        cart.setUpdatedAt(now);
        logger.info("Cart updated successfully: {}", cart.getId());
      } else {
        logger.warn("Cart update failed, cart not found: {}", cart.getId());
      }

    } catch (SQLException e) {
      logger.error("Error updating cart: {}", cart.getId(), e);
      throw new DaoException("Error updating cart: " + cart.getId(), e);
    }
  }

  private Cart mapResultSetToCart(ResultSet resultSet) throws SQLException {
    logger.debug("Mapping ResultSet to Cart object");

    UUID id = (UUID) resultSet.getObject("id");
    UUID customerId = (UUID) resultSet.getObject("customer_id");
    Timestamp createdAt = resultSet.getTimestamp("created_at");
    Timestamp updatedAt = resultSet.getTimestamp("updated_at");

    Cart cart = new Cart(id, customerId, createdAt, updatedAt);

    logger.trace("Cart mapped: ID={}, CustomerID={}, Created={}, Updated={}",
        id, customerId, createdAt, updatedAt);
    return cart;
  }
}