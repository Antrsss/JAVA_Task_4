package by.zgirskaya.course.task_4_web.dao;

import by.zgirskaya.course.task_4_web.exception.DaoException;

import java.util.List;
import java.util.UUID;

public interface BaseDao<T> {
  void create(T t) throws DaoException;
  T getById(UUID id) throws DaoException;
  void update(T t) throws DaoException;
  void delete(UUID id) throws DaoException;
  List<T> getAll() throws DaoException;
}
