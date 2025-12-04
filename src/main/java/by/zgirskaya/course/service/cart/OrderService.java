package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
  List<Order> findOrdersByCustomerId(UUID customerId) throws ServiceException;
  double getTotalOrdersAmountByCustomerId(UUID customerId) throws ServiceException;
  int getOrderCountByCustomerId(UUID customerId) throws ServiceException;
  boolean hasActiveOrders(UUID customerId) throws ServiceException;
}