package by.zgirskaya.course.dao.cart.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.cart.SupplyDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Supply;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Date;

class SupplyDaoImpl implements SupplyDao {

  private static final String INSERT_SUPPLY = """
        INSERT INTO supplies (id, employee_id, publisher_id, date, supply_price)
        VALUES (?, ?, ?, ?, ?)
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
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_SUPPLY)) {

      UUID supplyId = UUID.randomUUID();
      statement.setObject(1, supplyId);
      statement.setObject(2, supply.getEmployeeId());
      statement.setObject(3, supply.getPublisherId());
      statement.setDate(4, new Date(supply.getDate().getTime()));
      statement.setDouble(5, supply.getSupplyPrice());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Creating supply failed, no rows affected.");
      }

      supply.setId(supplyId);

    } catch (SQLException e) {
      throw new DaoException("Error creating supply", e);
    }
  }

  @Override
  public void updateSupply(Supply supply) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(UPDATE_SUPPLY)) {

      statement.setObject(1, supply.getEmployeeId());
      statement.setObject(2, supply.getPublisherId());
      statement.setDate(3, new Date(supply.getDate().getTime()));
      statement.setDouble(4, supply.getSupplyPrice());
      statement.setObject(5, supply.getId());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Updating supply failed, no rows affected.");
      }

    } catch (SQLException e) {
      throw new DaoException("Error updating supply with id: " + supply.getId(), e);
    }
  }

  @Override
  public void deleteSupply(UUID id) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_SUPPLY)) {

      statement.setObject(1, id);

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Deleting supply failed, no rows affected.");
      }

    } catch (SQLException e) {
      throw new DaoException("Error deleting supply with id: " + id, e);
    }
  }

  @Override
  public List<Supply> getAllSupplies() throws DaoException {
    List<Supply> supplies = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SUPPLIES);
         ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {
        Supply supply = extractSupplyFromResultSet(resultSet);
        supplies.add(supply);
      }

    } catch (SQLException e) {
      throw new DaoException("Error getting all supplies", e);
    }

    return supplies;
  }

  private Supply extractSupplyFromResultSet(ResultSet resultSet) throws SQLException {
    UUID id = (UUID) resultSet.getObject("id");
    UUID employeeId = (UUID) resultSet.getObject("employee_id");
    UUID publisherId = (UUID) resultSet.getObject("publisher_id");
    Date date = resultSet.getDate("date");
    Double supplyPrice = resultSet.getDouble("supply_price");

    Supply supply = new Supply(employeeId, publisherId, date, supplyPrice);
    supply.setId(id);

    return supply;
  }
}