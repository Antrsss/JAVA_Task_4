package by.zgirskaya.course.service.auth.impl;

import by.zgirskaya.course.dao.user.UserDao;
import by.zgirskaya.course.dao.user.impl.UserDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.model.user.Customer;
import by.zgirskaya.course.model.user.Employee;
import by.zgirskaya.course.service.auth.AuthService;
import by.zgirskaya.course.util.AuthParameter;
import by.zgirskaya.course.util.AuthValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class AuthServiceImpl implements AuthService {
  private static final Logger logger = LogManager.getLogger();
  private static final UserDao userDao = new UserDaoImpl();

  public Optional<AbstractUserModel> authenticateUser(String identifier, String password) throws ServiceException {
    logger.info("Authentication attempt for identifier: {}", identifier);

    if (!AuthValidator.validateNotEmpty(identifier)) {
      logger.warn("Authentication failed - empty identifier");
      return Optional.empty();
    }

    try {
      return userDao.findByIdentifier(identifier)
          .filter(user -> password.equals(user.getPassword()));

    } catch (DaoException e) {
      logger.error("Database error during authentication for identifier: {}", identifier, e);
      throw new ServiceException("Authentication failed due to database error", e);
    }
  }

  public AbstractUserModel registerUser(String name, String identifier, String role, String password,
                                        String username, String passportId) throws ServiceException {
    logger.info("Registration attempt - Name: {}, Identifier: {}, Role: {}", name, identifier, role);

    if (!AuthValidator.validateRegistrationInput(name, identifier, password, role, username, passportId)) {
      throw new ServiceException("Invalid registration input");
    }

    String phoneNumber = null;
    String email = null;

    try {
      if (AuthValidator.isValidEmail(identifier)) {
        email = identifier;
        if (userDao.existsByEmail(email)) {
          throw new ServiceException("User with this email already exists");
        }
      } else if (AuthValidator.isValidPhoneNumber(identifier)) {
        phoneNumber = identifier;
        if (userDao.existsByPhoneNumber(phoneNumber)) {
          throw new ServiceException("User with this phone number already exists");
        }
      } else {
        throw new ServiceException("Registration attempt - Invalid email or phone number format");
      }

      UUID roleId;
      AbstractUserModel user;

      if (AuthParameter.Roles.EMPLOYEE.equals(role)) {
        logger.debug("Creating employee user");
        roleId = userDao.findEmployeeRoleId();
        user = new Employee(name, phoneNumber, email, password, roleId, passportId);
      } else {
        logger.debug("Creating customer user");
        roleId = userDao.findCustomerRoleId();
        user = new Customer(name, phoneNumber, email, password, roleId, username);
      }

      userDao.create(user);
      return user;

    } catch (DaoException e) {
      logger.error("Registration failed for identifier: {}", identifier, e);
      throw new ServiceException("Registration failed: " + e.getMessage(), e);
    }
  }

  @Override
  public String findRoleById(UUID roleId) throws ServiceException {
    logger.debug("Finding role name by ID: {}", roleId);

    try {
      String roleName = userDao.getRoleNameById(roleId);
      logger.debug("Found role name: {} for ID: {}", roleName, roleId);
      return roleName;
    } catch (DaoException e) {
      logger.error("Error finding role by ID: {}", roleId, e);
      throw new ServiceException("Error finding role by ID: " + roleId, e);
    }
  }
}