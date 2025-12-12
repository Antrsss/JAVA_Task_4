package by.zgirskaya.course.dao.cart;

import by.zgirskaya.course.dao.BaseDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Order;

import java.util.List;
import java.util.UUID;

public interface OrderDao extends BaseDao<Order> {
  List<Order> findOrdersByCustomerId(UUID customerId) throws DaoException;
  Order findOrderById(UUID id) throws DaoException;
}
