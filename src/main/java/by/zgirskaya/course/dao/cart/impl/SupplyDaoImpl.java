package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.SupplyDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Supply;
import by.zgirskaya.course.util.TableColumns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Date;

public class SupplyDaoImpl implements SupplyDao {
  private static final Logger logger = LogManager.getLogger();

  private static final String INSERT_SUPPLY = """
        INSERT INTO supplies (id, employee_id, publisher_id, date, supply_price)
        VALUES (?, ?, ?, ?, ?)
        """;



  private static final String FIND_SUPPLY_BY_ID = """
        SELECT id, employee_id, publisher_id, date, supply_price
        FROM supplies
        WHERE id = ?
        """;

  private static final String UPDATE_SUPPLY = """
        UPDATE supplies
        SET employee_id = ?, publisher_id = ?, date = ?, supply_price = ?
        WHERE id = ?
        """;

  private static final String DELETE_SUPPLY = "DELETE FROM supplies WHERE id = ?";

  private static final String SELECT_ALL_SUPPLIES = """
        SELECT id, employee_id, publisher_id, date, supply_price
        FROM supplies ORDER BY date DESC
        """;

  @Override
  public void create(Supply supply) throws DaoException {
    logger.debug("Creating supply for employee: {}, publisher: {}",
        supply.getEmployeeId(), supply.getPublisherId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_SUPPLY)) {

      UUID supplyId = UUID.randomUUID();
      statement.setObject(1, supplyId);
      statement.setObject(2, supply.getEmployeeId());
      statement.setObject(3, supply.getPublisherId());
      statement.setDate(4, new Date(supply.getDate().getTime()));
      statement.setDouble(5, supply.getSupplyPrice());

      int affectedRows = statement.executeUpdate();
      logger.debug("Supply creation executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Creating supply failed - no rows affected for employee: {}, publisher: {}",
            supply.getEmployeeId(), supply.getPublisherId());
        throw new DaoException("Creating supply failed, no rows affected.");
      }

      supply.setId(supplyId);
      logger.info("Supply created successfully: {} (Employee: {}, Publisher: {}, Price: {})",
          supplyId, supply.getEmployeeId(), supply.getPublisherId(), supply.getSupplyPrice());

    } catch (SQLException e) {
      logger.error("Error creating supply for employee: {}, publisher: {}",
          supply.getEmployeeId(), supply.getPublisherId(), e);
      throw new DaoException("Error creating supply", e);
    }
  }

  @Override
  public Optional<Supply> findSupplyById(UUID id) throws DaoException {
    logger.debug("Finding supply by ID: {}", id);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(FIND_SUPPLY_BY_ID)) {

      statement.setObject(1, id);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          Supply supply = extractSupplyFromResultSet(resultSet);
          logger.debug("Found supply: {} (Employee: {}, Publisher: {})",
              id, supply.getEmployeeId(), supply.getPublisherId());
          return Optional.of(supply);
        } else {
          logger.debug("Supply not found: {}", id);
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      logger.error("Error finding supply by ID: {}", id, e);
      throw new DaoException("Error finding supply with id: " + id, e);
    }
  }

  @Override
  public void updateSupply(Supply supply) throws DaoException {
    logger.debug("Updating supply: {} (Employee: {}, Publisher: {})",
        supply.getId(), supply.getEmployeeId(), supply.getPublisherId());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(UPDATE_SUPPLY)) {

      statement.setObject(1, supply.getEmployeeId());
      statement.setObject(2, supply.getPublisherId());
      statement.setDate(3, new Date(supply.getDate().getTime()));
      statement.setDouble(4, supply.getSupplyPrice());
      statement.setObject(5, supply.getId());

      int affectedRows = statement.executeUpdate();
      logger.debug("Supply update executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Updating supply failed - no rows affected for supply: {}", supply.getId());
        throw new DaoException("Updating supply failed, no rows affected.");
      }

      logger.info("Supply updated successfully: {} (Employee: {}, Publisher: {}, Price: {})",
          supply.getId(), supply.getEmployeeId(), supply.getPublisherId(), supply.getSupplyPrice());

    } catch (SQLException e) {
      logger.error("Error updating supply: {}", supply.getId(), e);
      throw new DaoException("Error updating supply with id: " + supply.getId(), e);
    }
  }

  @Override
  public void deleteSupply(UUID id) throws DaoException {
    logger.debug("Deleting supply: {}", id);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_SUPPLY)) {

      statement.setObject(1, id);

      int affectedRows = statement.executeUpdate();
      logger.debug("Supply deletion executed, affected rows: {}", affectedRows);

      if (affectedRows == 0) {
        logger.error("Deleting supply failed - no rows affected for supply: {}", id);
        throw new DaoException("Deleting supply failed, no rows affected.");
      }

      logger.info("Supply deleted successfully: {}", id);

    } catch (SQLException e) {
      logger.error("Error deleting supply: {}", id, e);
      throw new DaoException("Error deleting supply with id: " + id, e);
    }
  }

  @Override
  public List<Supply> getAllSupplies() throws DaoException {
    logger.debug("Getting all supplies");

    List<Supply> supplies = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SUPPLIES);
         ResultSet resultSet = statement.executeQuery()) {

      int count = 0;
      while (resultSet.next()) {
        Supply supply = extractSupplyFromResultSet(resultSet);
        supplies.add(supply);
        count++;
      }
      logger.debug("Found {} supplies total", count);

    } catch (SQLException e) {
      logger.error("Error getting all supplies", e);
      throw new DaoException("Error getting all supplies", e);
    }

    return supplies;
  }

  private Supply extractSupplyFromResultSet(ResultSet resultSet) throws SQLException {
    logger.debug("Extracting supply from ResultSet");

    UUID id = (UUID) resultSet.getObject(TableColumns.Supply.ID);
    UUID employeeId = (UUID) resultSet.getObject(TableColumns.Supply.EMPLOYEE_ID);
    UUID publisherId = (UUID) resultSet.getObject(TableColumns.Supply.PUBLISHER_ID);
    Date date = resultSet.getDate(TableColumns.Supply.DATE);
    Double supplyPrice = resultSet.getDouble(TableColumns.Supply.SUPPLY_PRICE);

    Supply supply = new Supply(employeeId, publisherId, date, supplyPrice);
    supply.setId(id);

    logger.debug("Extracted supply: {} (Employee: {}, Publisher: {}, Date: {}, Price: {})",
        id, employeeId, publisherId, date, supplyPrice);

    return supply;
  }
}