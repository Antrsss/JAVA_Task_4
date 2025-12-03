package by.zgirskaya.course.dao.user.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.user.UserDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.model.user.Customer;
import by.zgirskaya.course.model.user.Employee;
import by.zgirskaya.course.util.AuthParameters;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDaoImpl implements UserDao {

  private static final String INSERT_USER = """
    INSERT INTO users (name, phone_number, email, password, role_id)
    VALUES (?, ?, ?, ?, ?)
    """;

  private static final String INSERT_CUSTOMER =
      "INSERT INTO customers (user_id, username) VALUES (?, ?)";

  private static final String INSERT_EMPLOYEE =
      "INSERT INTO employees (user_id, passport_id) VALUES (?, ?)";

  private static final String SELECT_BY_PHONE_NUMBER = """
    SELECT u.*, c.username, e.passport_id, r.role_name
    FROM users u
    LEFT JOIN customers c ON u.id = c.user_id
    LEFT JOIN employees e ON u.id = e.user_id
    LEFT JOIN roles r ON u.role_id = r.id
    WHERE u.phone_number = ?
    """;

  private static final String SELECT_BY_EMAIL = """
      SELECT u.*, c.username, e.passport_id, r.role_name
      FROM users u
      LEFT JOIN customers c ON u.id = c.user_id
      LEFT JOIN employees e ON u.id = e.user_id
      LEFT JOIN roles r ON u.role_id = r.id
      WHERE u.email = ?
      """;

  private static final String SELECT_ROLE_ID_BY_NAME = "SELECT id FROM roles WHERE role_name = ?";

  private static final String EXISTS_BY_PHONE_NUMBER = "SELECT 1 FROM users WHERE phone_number = ?";

  private static final String EXISTS_BY_EMAIL = "SELECT 1 FROM users WHERE email = ?";

  @Override
  public void create(AbstractUserModel user) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

      setUserParameters(statement, user);
      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Creating user failed, no rows affected.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setId((UUID) generatedKeys.getObject(1));
        } else {
          throw new DaoException("Creating user failed, no ID obtained.");
        }
      }

      createUserSpecificRecord(connection, user);

    } catch (SQLException e) {
      throw new DaoException("Error creating user: " + user.getEmail(), e);
    }
  }

  @Override
  public Optional<AbstractUserModel> findByPhoneNumber(String phoneNumber) throws DaoException {
    return findUserByParameter(SELECT_BY_PHONE_NUMBER, phoneNumber);
  }

  @Override
  public boolean existsByPhoneNumber(String phoneNumber) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(EXISTS_BY_PHONE_NUMBER)) {

      statement.setString(1, phoneNumber);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new DaoException("Error checking if user exists by phone number: " + phoneNumber, e);
    }
  }

  @Override
  public Optional<AbstractUserModel> findByEmail(String email) throws DaoException {
    return findUserByParameter(SELECT_BY_EMAIL, email);
  }

  @Override
  public boolean existsByEmail(String email) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
    PreparedStatement statement = connection.prepareStatement(EXISTS_BY_EMAIL)) {

      statement.setString(1, email);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new DaoException("Error checking if user exists by email: " + email, e);
    }
  }

  @Override
  public UUID getCustomerRoleId() throws DaoException {
    return getRoleIdByName(AuthParameters.Roles.CUSTOMER);
  }

  @Override
  public UUID getEmployeeRoleId() throws DaoException {
    return getRoleIdByName(AuthParameters.Roles.EMPLOYEE);
  }

  private UUID getRoleIdByName(String roleName) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(
                 SELECT_ROLE_ID_BY_NAME)) {

      statement.setString(1, roleName);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return (UUID) resultSet.getObject("id");
        }
        throw new DaoException("Role not found: " + roleName);
      }
    } catch (SQLException e) {
      throw new DaoException("Error getting role id for: " + roleName, e);
    }
  }

  private Optional<AbstractUserModel> findUserByParameter(String sql, String parameter) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setString(1, parameter);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next() ? Optional.of(mapResultSetToUser(resultSet)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new DaoException("Error finding user by parameter: " + parameter, e);
    }
  }

  private void setUserParameters(PreparedStatement statement, AbstractUserModel user) throws SQLException {
    statement.setString(1, user.getName());
    statement.setString(2, user.getPhoneNumber());
    statement.setString(3, user.getEmail());
    statement.setString(4, user.getPassword());
    statement.setObject(5, user.getRoleId());
  }

  private AbstractUserModel mapResultSetToUser(ResultSet resultSet) throws SQLException {
    UUID id = (UUID) resultSet.getObject("id");
    String name = resultSet.getString("name");
    String phoneNumber = resultSet.getString("phone_number");
    String email = resultSet.getString("email");
    String password = resultSet.getString("password");
    UUID roleId = (UUID) resultSet.getObject("role_id");

    String username = resultSet.getString("username");
    String passportId = resultSet.getString("passport_id");

    AbstractUserModel user;

    if (username != null) {
      user = new Customer(name, phoneNumber, email, password, roleId, username);
    } else if (passportId != null) {
      user = new Employee(name, phoneNumber, email, password, roleId, passportId);
    } else {
      user = new AbstractUserModel(name, phoneNumber, email, password, roleId) {};
    }

    user.setId(id);
    return user;
  }

  private void createUserSpecificRecord(Connection connection, AbstractUserModel user) throws SQLException {

    if (user instanceof Customer customer) {

      try (PreparedStatement statement = connection.prepareStatement(INSERT_CUSTOMER)) {
        statement.setObject(1, user.getId());
        statement.setString(2, customer.getUsername());
        statement.executeUpdate();
      }
    } else if (user instanceof Employee employee) {

      try (PreparedStatement statement = connection.prepareStatement(INSERT_EMPLOYEE)) {
        statement.setObject(1, user.getId());
        statement.setString(2, employee.getPassportId());
        statement.executeUpdate();
      }
    }
  }
}