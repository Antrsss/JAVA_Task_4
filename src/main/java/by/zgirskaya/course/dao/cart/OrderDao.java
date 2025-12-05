package by.zgirskaya.course.dao.cart;

import by.zgirskaya.course.dao.BaseDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Order;

import java.util.List;
import java.util.UUID;

public interface OrderDao extends BaseDao<Order> {
  List<Order> findOrdersByCustomerId(UUID customerId) throws DaoException;
  Order findById(UUID id) throws DaoException;
  boolean update(Order order) throws DaoException;
  boolean delete(UUID id) throws DaoException;
}
