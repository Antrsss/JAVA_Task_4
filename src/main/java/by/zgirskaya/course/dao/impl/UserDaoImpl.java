package by.zgirskaya.course.dao.impl;

import by.zgirskaya.course.connection.DatabaseConnection;
import by.zgirskaya.course.dao.UserDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.AbstractUserModel;
import by.zgirskaya.course.model.Customer;
import by.zgirskaya.course.model.Employee;
import by.zgirskaya.course.util.AuthParameter;
import by.zgirskaya.course.util.TableColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDaoImpl implements UserDao {
  private static final Logger logger = LogManager.getLogger();

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

  private static final String SELECT_ROLE_NAME_BY_ID = "SELECT role_name FROM roles WHERE id = ?";

  private static final String EXISTS_BY_PHONE_NUMBER = "SELECT 1 FROM users WHERE phone_number = ?";

  private static final String EXISTS_BY_EMAIL = "SELECT 1 FROM users WHERE email = ?";

  @Override
  public void create(AbstractUserModel user) throws DaoException {
    logger.debug("Attempting to create user with email: {}", user.getEmail());

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

      setUserParameters(statement, user);
      int affectedRows = statement.executeUpdate();

      if (affectedRows == 0) {
        logger.error("Creating user failed - no rows affected for email: {}", user.getEmail());
        throw new DaoException("Creating user failed, no rows affected.");
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          UUID generatedId = (UUID) generatedKeys.getObject(1);
          user.setId(generatedId);
          logger.debug("Generated ID obtained for user {}: {}", user.getEmail(), generatedId);
        } else {
          logger.error("Creating user failed - no ID obtained for email: {}", user.getEmail());
          throw new DaoException("Creating user failed, no ID obtained.");
        }
      }

      createUserSpecificRecord(connection, user);
      logger.info("User created successfully: {} (ID: {})", user.getEmail(), user.getId());

    } catch (SQLException e) {
      logger.error("SQL error creating user with email: {}", user.getEmail(), e);
      throw new DaoException("Error creating user: " + user.getEmail(), e);
    }
  }

  @Override
  public UUID findCustomerRoleId() throws DaoException {
    logger.debug("Finding customer role ID");
    return findRoleIdByName(AuthParameter.Roles.CUSTOMER);
  }

  @Override
  public UUID findEmployeeRoleId() throws DaoException {
    logger.debug("Finding employee role ID");
    return findRoleIdByName(AuthParameter.Roles.EMPLOYEE);
  }

  @Override
  public String getRoleNameById(UUID roleId) throws DaoException {
    logger.debug("Getting role name by ID: {}", roleId);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(SELECT_ROLE_NAME_BY_ID)) {

      statement.setObject(1, roleId);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          String roleName = resultSet.getString(TableColumn.Role.ROLE_NAME);
          logger.debug("Found role name: {} for ID: {}", roleName, roleId);
          return roleName;
        } else {
          logger.error("Role not found for ID: {}", roleId);
          throw new DaoException("Role not found for ID: " + roleId);
        }
      }
    } catch (SQLException e) {
      logger.error("Error getting role name by ID: {}", roleId, e);
      throw new DaoException("Error getting role name by ID: " + roleId, e);
    }
  }

  @Override
  public boolean existsByPhoneNumber(String phoneNumber) throws DaoException {
    logger.debug("Checking if user exists by phone number: {}", phoneNumber);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(EXISTS_BY_PHONE_NUMBER)) {

      statement.setString(1, phoneNumber);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      logger.error("Error checking if user exists by phone number: {}", phoneNumber, e);
      throw new DaoException("Error checking if user exists by phone number: " + phoneNumber, e);
    }
  }

  @Override
  public boolean existsByEmail(String email) throws DaoException {
    logger.debug("Checking if user exists by email: {}", email);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(EXISTS_BY_EMAIL)) {

      statement.setString(1, email);
      try (ResultSet resultSet = statement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      logger.error("Error checking if user exists by email: {}", email, e);
      throw new DaoException("Error checking if user exists by email: " + email, e);
    }
  }

  @Override
  public Optional<AbstractUserModel> findByPhoneNumber(String phoneNumber) throws DaoException {
    logger.debug("Finding user by phone number: {}", phoneNumber);
    return findUserByParameter(SELECT_BY_PHONE_NUMBER, phoneNumber);
  }

  @Override
  public Optional<AbstractUserModel> findByEmail(String email) throws DaoException {
    logger.debug("Finding user by email: {}", email);
    return findUserByParameter(SELECT_BY_EMAIL, email);
  }

  private UUID findRoleIdByName(String roleName) throws DaoException {
    logger.debug("Finding role ID by name: {}", roleName);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(
             SELECT_ROLE_ID_BY_NAME)) {

      statement.setString(1, roleName);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          UUID roleId = (UUID) resultSet.getObject(TableColumn.Role.ID);
          logger.debug("Found role ID for {}: {}", roleName, roleId);
          return roleId;
        }
        logger.error("Role not found: {}", roleName);
        throw new DaoException("Role not found: " + roleName);
      }
    } catch (SQLException e) {
      logger.error("Error getting role id for: {}", roleName, e);
      throw new DaoException("Error getting role id for: " + roleName, e);
    }
  }

  private Optional<AbstractUserModel> findUserByParameter(String sql, String parameter) throws DaoException {
    logger.debug("Executing user query: {} with parameter: {}", sql, parameter);

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setString(1, parameter);
      try (ResultSet resultSet = statement.executeQuery()) {
        boolean found = resultSet.next();
        logger.debug("User found for parameter {}: {}", parameter, found);

        if (found) {
          AbstractUserModel user = mapResultSetToUser(resultSet);
          logger.debug("Mapped user: {} (ID: {})", user.getEmail(), user.getId());
          return Optional.of(user);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      logger.error("Error finding user by parameter: {}", parameter, e);
      throw new DaoException("Error finding user by parameter: " + parameter, e);
    }
  }

  private void setUserParameters(PreparedStatement statement, AbstractUserModel user) throws SQLException {
    logger.debug("Setting parameters for user: {}", user.getEmail());

    statement.setString(1, user.getName());
    statement.setString(2, user.getPhoneNumber());
    statement.setString(3, user.getEmail());
    statement.setString(4, user.getPassword());
    statement.setObject(5, user.getRoleId());
  }

  private AbstractUserModel mapResultSetToUser(ResultSet resultSet) throws SQLException {
    logger.debug("Mapping ResultSet to User object");

    UUID id = (UUID) resultSet.getObject(TableColumn.User.ID);
    String name = resultSet.getString(TableColumn.User.NAME);
    String phoneNumber = resultSet.getString(TableColumn.User.PHONE_NUMBER);
    String email = resultSet.getString(TableColumn.User.EMAIL);
    String password = resultSet.getString(TableColumn.User.PASSWORD);
    UUID roleId = (UUID) resultSet.getObject(TableColumn.User.ROLE_ID);

    String username = resultSet.getString(TableColumn.User.USERNAME);
    String passportId = resultSet.getString(TableColumn.User.PASSPORT_ID);

    AbstractUserModel user;

    if (username != null) {
      user = new Customer(name, phoneNumber, email, password, roleId, username);
      logger.debug("Mapped as Customer with username: {}", username);
    } else if (passportId != null) {
      user = new Employee(name, phoneNumber, email, password, roleId, passportId);
      logger.debug("Mapped as Employee with passport ID: {}", passportId);
    } else {
      user = new AbstractUserModel(name, phoneNumber, email, password, roleId) {};
      logger.debug("Mapped as generic AbstractUserModel");
    }

    user.setId(id);
    return user;
  }

  private void createUserSpecificRecord(Connection connection, AbstractUserModel user) throws SQLException {
    logger.debug("Creating user-specific record for: {} (ID: {})", user.getEmail(), user.getId());

    if (user instanceof Customer customer) {
      logger.debug("Creating customer record with username: {}", customer.getUsername());

      try (PreparedStatement statement = connection.prepareStatement(INSERT_CUSTOMER)) {
        statement.setObject(1, user.getId());
        statement.setString(2, customer.getUsername());
        statement.executeUpdate();
        logger.debug("Customer record created successfully");
      }
    } else if (user instanceof Employee employee) {
      logger.debug("Creating employee record with passport ID: {}", employee.getPassportId());

      try (PreparedStatement statement = connection.prepareStatement(INSERT_EMPLOYEE)) {
        statement.setObject(1, user.getId());
        statement.setString(2, employee.getPassportId());
        statement.executeUpdate();
        logger.debug("Employee record created successfully");
      }
    }
  }
}