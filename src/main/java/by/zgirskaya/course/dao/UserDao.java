package by.zgirskaya.course.dao;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.AbstractUserModel;

import java.util.Optional;
import java.util.UUID;

public interface UserDao extends BaseDao<AbstractUserModel> {
  UUID findCustomerRoleId() throws DaoException;
  UUID findEmployeeRoleId() throws DaoException;
  String getRoleNameById(UUID roleId) throws DaoException;

  boolean existsByPhoneNumber(String phoneNumber) throws DaoException;
  boolean existsByEmail(String email) throws DaoException;

  Optional<AbstractUserModel> findByPhoneNumber(String phoneNumber) throws DaoException;
  Optional<AbstractUserModel> findByEmail(String email) throws DaoException;

  default Optional<AbstractUserModel> findByIdentifier(String identifier) throws DaoException {
    Optional<AbstractUserModel> user = findByPhoneNumber(identifier);
    if (user.isPresent()) {
      return user;
    }

    return findByEmail(identifier);
  }
}
