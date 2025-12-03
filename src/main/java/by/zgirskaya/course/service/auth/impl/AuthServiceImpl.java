package by.zgirskaya.course.service.auth.impl;

import by.zgirskaya.course.dao.user.UserDao;
import by.zgirskaya.course.dao.user.impl.UserDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.model.user.Customer;
import by.zgirskaya.course.model.user.Employee;
import by.zgirskaya.course.service.auth.AuthService;
import by.zgirskaya.course.util.AuthParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class AuthServiceImpl implements AuthService {
  private static final Logger logger = LogManager.getLogger();
  private static final UserDao userDao = new UserDaoImpl();

  public Optional<AbstractUserModel> authenticate(String identifier, String password) throws ServiceException {
    logger.info("Authentication attempt for identifier: {}", identifier);

    if (identifier == null || identifier.isBlank()) {
      logger.warn("Authentication failed - empty identifier");
      return Optional.empty();
    }

    try {
      Optional<AbstractUserModel> userOptional = userDao.findByIdentifier(identifier);

      if (userOptional.isEmpty()) {
        logger.warn("Authentication failed - user not found: {}", identifier);
        return Optional.empty();
      }

      AbstractUserModel user = userOptional.get();

      if (!password.equals(user.getPassword())) {
        logger.warn("Authentication failed - invalid password for user: {}", identifier);
        return Optional.empty();
      }

      logger.info("Authentication successful for user: {} (ID: {})", identifier, user.getId());
      return Optional.of(user);

    } catch (DaoException e) {
      logger.error("Database error during authentication for identifier: {}", identifier, e);
      throw new ServiceException("Authentication failed due to database error", e);
    }
  }

  public AbstractUserModel registerUser(String name, String identifier, String role, String password,
                                        String username, String passportId) throws ServiceException {
    logger.info("Registration attempt - Name: {}, Identifier: {}, Role: {}", name, identifier, role);

    validateRegistrationInput(name, identifier, password, role, username, passportId);

    try {
      String phoneNumber = null;
      String email = null;

      if (identifier.contains("@")) {
        email = identifier;
        if (userDao.existsByEmail(email)) {
          throw new ServiceException(AuthParameters.Validation.PHONE_OR_EMAIL_EXISTS);
        }
      } else {
        phoneNumber = identifier;
        if (userDao.existsByPhoneNumber(phoneNumber)) {
          throw new ServiceException(AuthParameters.Validation.PHONE_OR_EMAIL_EXISTS);
        }
      }

      UUID roleId;
      AbstractUserModel user;

      if (AuthParameters.Roles.EMPLOYEE.equals(role)) {
        logger.debug("Creating employee user");
        roleId = userDao.getEmployeeRoleId();
        user = new Employee(name, phoneNumber, email, password, roleId, passportId);
      } else {
        logger.debug("Creating customer user");
        roleId = userDao.getCustomerRoleId();
        user = new Customer(name, phoneNumber, email, password, roleId, username);
      }

      userDao.create(user);

      String logIdentifier = phoneNumber != null ? phoneNumber : email;
      logger.info("User successfully registered: {} (ID: {})", logIdentifier, user.getId());

      return user;

    } catch (DaoException e) {
      logger.error("Registration failed for identifier: {}", identifier, e);
      throw new ServiceException("Registration failed: " + e.getMessage(), e);
    }
  }

  public boolean checkIdentifierExists(String identifier) throws ServiceException {
    if (identifier == null || identifier.isBlank()) {
      return false;
    }

    try {
      if (identifier.contains("@")) {
        return userDao.existsByEmail(identifier);
      } else {
        return userDao.existsByPhoneNumber(identifier);
      }
    } catch (DaoException e) {
      logger.error("Error checking identifier existence: {}", identifier, e);
      throw new ServiceException("Error checking identifier availability", e);
    }
  }

  public Optional<AbstractUserModel> findUserByIdentifier(String identifier) throws ServiceException {
    if (identifier == null || identifier.isBlank()) {
      return Optional.empty();
    }

    try {
      return userDao.findByIdentifier(identifier);
    } catch (DaoException e) {
      logger.error("Error finding user by identifier: {}", identifier, e);
      throw new ServiceException("Error finding user", e);
    }
  }

  private void validateRegistrationInput(String name, String identifier, String password,
                                         String role, String username, String passportId) throws ServiceException {
    if (name == null || name.isBlank()) {
      throw new ServiceException("Name is required");
    }

    if (identifier == null || identifier.isBlank()) {
      throw new ServiceException(AuthParameters.Validation.PHONE_OR_EMAIL_REQUIRED);
    }

    if (password == null || password.isBlank()) {
      throw new ServiceException("Password is required");
    }

    if (role == null || role.isBlank()) {
      throw new ServiceException("Role is required");
    }

    if (AuthParameters.Roles.EMPLOYEE.equals(role)) {
      if (passportId == null || passportId.isBlank()) {
        throw new ServiceException("Passport ID is required for employees");
      }
    } else {
      if (username == null || username.isBlank()) {
        throw new ServiceException("Username is required for customers");
      }
    }
  }
}