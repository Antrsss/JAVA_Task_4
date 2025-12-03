package by.zgirskaya.course.dao;

import by.zgirskaya.course.exception.DaoException;

public interface BaseDao<T> {
  void create(T t) throws DaoException;
}
