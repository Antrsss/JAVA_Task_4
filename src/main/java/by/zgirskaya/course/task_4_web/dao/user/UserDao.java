package by.zgirskaya.course.task_4_web.dao.user;

import by.zgirskaya.course.task_4_web.dao.BaseDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.user.AbstractUserModel;

import java.util.Optional;

public interface UserDao extends BaseDao<AbstractUserModel> {
  Optional<AbstractUserModel> findByEmail(String email) throws DaoException;
  boolean existsByEmail(String email) throws DaoException;
}
