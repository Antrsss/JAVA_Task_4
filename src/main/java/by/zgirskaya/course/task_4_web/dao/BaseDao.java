package by.zgirskaya.course.task_4_web.dao;

import by.zgirskaya.course.task_4_web.exception.DaoException;

import java.util.List;
import java.util.UUID;

public interface BaseDao<T> {
  void create(T t) throws DaoException;
}
