package by.zgirskaya.course.task_4_web.dao;

import by.zgirskaya.course.task_4_web.exception.DaoException;

public interface BaseDao<T> {
  void create(T t) throws DaoException;
}
