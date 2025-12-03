package by.zgirskaya.course.dao.user;

import by.zgirskaya.course.dao.BaseDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.user.AbstractUserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserDao extends BaseDao<AbstractUserModel> {
  Optional<AbstractUserModel> findByPhoneNumber(String phoneNumber) throws DaoException;
  boolean existsByPhoneNumber(String phoneNumber) throws DaoException;

  Optional<AbstractUserModel> findByEmail(String email) throws DaoException;
  boolean existsByEmail(String email) throws DaoException;

  UUID findCustomerRoleId() throws DaoException;
  UUID findEmployeeRoleId() throws DaoException;

  default Optional<AbstractUserModel> findByIdentifier(String identifier) throws DaoException {
    Optional<AbstractUserModel> user = findByPhoneNumber(identifier);
    if (user.isPresent()) {
      return user;
    }

    return findByEmail(identifier);
  }
}
