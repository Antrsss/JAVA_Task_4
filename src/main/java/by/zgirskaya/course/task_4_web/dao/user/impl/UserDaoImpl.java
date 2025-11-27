package by.zgirskaya.course.task_4_web.dao.user.impl;

import by.zgirskaya.course.task_4_web.connection.DatabaseConnection;
import by.zgirskaya.course.task_4_web.dao.user.UserDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.user.AbstractUserModel;
import by.zgirskaya.course.task_4_web.model.user.Customer;
import by.zgirskaya.course.task_4_web.model.user.Employee;
import by.zgirskaya.course.task_4_web.model.user.Role;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDaoImpl implements UserDao {

  private static final String INSERT_USER = """
        INSERT INTO users (name, phone_number, email, password, role_id) 
        VALUES (?, ?, ?, ?, ?)
        """;

  private static final String SELECT_BY_ID = """
        SELECT u.*, c.username, e.passport_id, r.role_name 
        FROM users u 
        LEFT JOIN customers c ON u.id = c.user_id 
        LEFT JOIN employees e ON u.id = e.user_id 
        LEFT JOIN roles r ON u.role_id = r.id 
        WHERE u.id = ?
        """;

  private static final String UPDATE_USER = """
        UPDATE users SET name = ?, phone_number = ?, email = ?, password = ?, role_id = ? 
        WHERE id = ?
        """;

  private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

  private static final String SELECT_BY_EMAIL = """
        SELECT u.*, c.username, e.passport_id, r.role_name 
        FROM users u 
        LEFT JOIN customers c ON u.id = c.user_id 
        LEFT JOIN employees e ON u.id = e.user_id 
        LEFT JOIN roles r ON u.role_id = r.id 
        WHERE u.email = ?
        """;

  private static final String SELECT_ROLE_ID_BY_NAME = "SELECT id FROM roles WHERE role_name = ?";


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
  public AbstractUserModel getById(UUID id) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {

      statement.setObject(1, id);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToUser(resultSet);
        } else {
          throw new DaoException("User not found with id: " + id);
        }
      }
    } catch (SQLException e) {
      throw new DaoException("Error getting user by id: " + id, e);
    }
  }

  @Override
  public void update(AbstractUserModel user) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {

      setUserParameters(statement, user);
      statement.setObject(6, user.getId());

      int affectedRows = statement.executeUpdate();
      if (affectedRows == 0) {
        throw new DaoException("Updating user failed, no rows affected: " + user.getId());
      }

      // Обновляем специфичные данные
      updateUserSpecificRecord(connection, user);

    } catch (SQLException e) {
      throw new DaoException("Error updating user: " + user.getId(), e);
    }
  }

  @Override
  public void delete(UUID id) throws DaoException {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {

      statement.setObject(1, id);
      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        throw new DaoException("Deleting user failed, no rows affected: " + id);
      }
    } catch (SQLException e) {
      throw new DaoException("Error deleting user: " + id, e);
    }
  }

  @Override
  public List<AbstractUserModel> getAll() throws DaoException {
    throw new DaoException("Method getAll() is not implemented for UserDao");
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
    return getRoleIdByName("customer");
  }

  @Override
  public UUID getEmployeeRoleId() throws DaoException {
    return getRoleIdByName("employee");
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

  // Вспомогательные методы

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
      // Базовый пользователь без специфичных полей
      user = new AbstractUserModel(name, phoneNumber, email, password, roleId) {};
    }

    user.setId(id);
    return user;
  }

  private void createUserSpecificRecord(Connection connection, AbstractUserModel user) throws SQLException {
    if (user instanceof Customer customer) {
      try (PreparedStatement statement = connection.prepareStatement(
              "INSERT INTO customers (user_id, username) VALUES (?, ?)")) {
        statement.setObject(1, user.getId());
        statement.setString(2, customer.getUsername());
        statement.executeUpdate();
      }
    } else if (user instanceof Employee employee) {
      try (PreparedStatement statement = connection.prepareStatement(
              "INSERT INTO employees (user_id, passport_id) VALUES (?, ?)")) {
        statement.setObject(1, user.getId());
        statement.setString(2, employee.getPassportId());
        statement.executeUpdate();
      }
    }
  }

  private void updateUserSpecificRecord(Connection connection, AbstractUserModel user) throws SQLException {
    if (user instanceof Customer customer) {
      try (PreparedStatement statement = connection.prepareStatement(
              "UPDATE customers SET username = ? WHERE user_id = ?")) {
        statement.setString(1, customer.getUsername());
        statement.setObject(2, user.getId());
        statement.executeUpdate();
      }
    } else if (user instanceof Employee employee) {
      try (PreparedStatement statement = connection.prepareStatement(
              "UPDATE employees SET passport_id = ? WHERE user_id = ?")) {
        statement.setString(1, employee.getPassportId());
        statement.setObject(2, user.getId());
        statement.executeUpdate();
      }
    }
  }
}