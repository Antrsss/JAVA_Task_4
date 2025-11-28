package by.zgirskaya.course.task_4_web.dao.impl;

import by.zgirskaya.course.task_4_web.connection.DatabaseConnection;
import by.zgirskaya.course.task_4_web.dao.BaseDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.cart.Supply;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class SupplyDaoImpl implements BaseDao<Supply> {

  private static final String INSERT_SUPPLY = """
        INSERT INTO supplies (employee_id, publisher_id, date, supply_price) 
        VALUES (?, ?, ?, ?)
        """;

  private static final String SELECT_ALL = """
        SELECT id, employee_id, publisher_id, date, supply_price 
        FROM supplies ORDER BY date DESC
        """;

  @Override
  public void create(Supply supply) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_SUPPLY, Statement.RETURN_GENERATED_KEYS)) {

      statement.setObject(1, supply.getEmployeeId());
      statement.setObject(2, supply.getPublisherId());
      statement.setDate(3, new java.sql.Date(supply.getDate().getTime()));
      statement.setDouble(4, supply.getSupplyPrice());

      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Creating supply failed, no rows affected.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          supply.setId((UUID) generatedKeys.getObject(1));
        } else {
          throw new DaoException("Creating supply failed, no ID obtained.");
        }
      }

    } catch (SQLException e) {
      throw new DaoException("Error creating supply", e);
    }
  }

  private Supply mapResultSetToSupply(ResultSet resultSet) throws SQLException {
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