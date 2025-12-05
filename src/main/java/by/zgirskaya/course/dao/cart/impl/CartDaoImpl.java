package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.CartDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Cart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class CartDaoImpl implements CartDao {
  private static final Logger logger = LogManager.getLogger();

  private static final String SELECT_CART_BY_CUSTOMER_ID = """
        SELECT id, customer_id, created_at, updated_at
        FROM carts
        WHERE customer_id = ?
        """;

  private static final String SELECT_CART_BY_ID = """
        SELECT id, customer_id, created_at, updated_at
        FROM carts
        WHERE id = ?
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

  private static final String DELETE_CART = "DELETE FROM carts WHERE id = ?";

  @Override
  public Cart findCartByCustomerId(UUID customerId) throws DaoException {
    logger.debug("Finding cart by customer ID: {}", customerId);

    if (customerId == null) {
      logger.warn("Attempted to find cart with null customer ID");
      throw new DaoException("Customer ID is required");
    }

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
  public Optional<Cart> findCartById(UUID cartId) throws DaoException {
    logger.debug("Finding cart by ID: {}", cartId);

    if (cartId == null) {
      logger.warn("Attempted to find cart with null ID");
      return Optional.empty();
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_CART_BY_ID)) {

      statement.setObject(1, cartId);
      logger.debug("Executing query to find cart by ID: {}", cartId);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Cart cart = mapResultSetToCart(resultSet);
          logger.info("Found cart by ID: {}", cartId);
          return Optional.of(cart);
        } else {
          logger.debug("Cart not found by ID: {}", cartId);
          return Optional.empty();
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding cart by ID: {}", cartId, e);
      throw new DaoException("Error finding cart by ID: " + cartId, e);
    }
  }

  @Override
  public Cart createCartForCustomer(UUID customerId) throws DaoException {
    logger.debug("Creating cart for customer ID: {}", customerId);

    if (customerId == null) {
      logger.warn("Attempted to create cart with null customer ID");
      throw new DaoException("Customer ID is required");
    }

    // Сначала проверяем, нет ли уже корзины у этого пользователя
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

      statement.setObject(1, cartId);
      statement.setObject(2, customerId);
      statement.setTimestamp(3, now);
      statement.setTimestamp(4, now);

      int affectedRows = statement.executeUpdate();
      logger.debug("Cart creation executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Creating cart failed - no rows affected for customer: {}", customerId);
        throw new DaoException("Creating cart failed, no rows affected.");
      }

      Cart cart = new Cart(cartId, customerId, now, now);
      logger.info("Cart created successfully for customer ID {}: {}", customerId, cartId);

      return cart;

    } catch (SQLException e) {
      logger.error("Error creating cart for customer ID: {}", customerId, e);
      throw new DaoException("Error creating cart for customer ID: " + customerId, e);
    }
  }

  @Override
  public boolean updateCart(Cart cart) throws DaoException {
    logger.debug("Updating cart: {}", cart.getId());

    if (cart.getId() == null) {
      logger.error("Cannot update cart without ID");
      throw new DaoException("Cart ID is required for update");
    }

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

      return updated;

    } catch (SQLException e) {
      logger.error("Error updating cart: {}", cart.getId(), e);
      throw new DaoException("Error updating cart: " + cart.getId(), e);
    }
  }

  @Override
  public boolean deleteCart(UUID cartId) throws DaoException {
    logger.debug("Deleting cart: {}", cartId);

    if (cartId == null) {
      logger.warn("Attempted to delete cart with null ID");
      throw new DaoException("Cart ID is required");
    }

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_CART)) {

      statement.setObject(1, cartId);

      int affectedRows = statement.executeUpdate();
      logger.debug("Cart deletion executed, affected rows: {}", affectedRows);

      boolean deleted = affectedRows > 0;
      if (deleted) {
        logger.info("Cart deleted successfully: {}", cartId);
      } else {
        logger.warn("Cart deletion failed, cart not found: {}", cartId);
      }

      return deleted;

    } catch (SQLException e) {
      logger.error("Error deleting cart: {}", cartId, e);
      throw new DaoException("Error deleting cart: " + cartId, e);
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