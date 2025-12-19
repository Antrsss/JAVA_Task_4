package by.zgirskaya.course.dao;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderDao extends BaseDao<Order> {
  List<Order> findOrdersByCustomerId(UUID customerId) throws DaoException;
  Order findOrderById(UUID id) throws DaoException;
}
