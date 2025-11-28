package by.zgirskaya.course.task_4_web.dao.cart;

import by.zgirskaya.course.task_4_web.dao.BaseDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.cart.Order;

import java.util.List;
import java.util.UUID;

public interface OrderDao extends BaseDao<Order> {
  List<Order> findOrdersByCustomerId(UUID customerId) throws DaoException;
}
