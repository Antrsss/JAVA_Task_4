package by.zgirskaya.course.service;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Supply;

import java.util.List;
import java.util.UUID;

public interface BaseService<T> {
  boolean exists(UUID id) throws ServiceException;

  T create(T t) throws ServiceException;
  Supply update(T t) throws ServiceException;
  void delete(UUID id) throws ServiceException;

  List<Supply> getAll() throws ServiceException;
}
