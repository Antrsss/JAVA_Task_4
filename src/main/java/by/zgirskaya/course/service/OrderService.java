package by.zgirskaya.course.service;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Cart;
import by.zgirskaya.course.model.Item;
import by.zgirskaya.course.model.Order;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderService {
  List<Order> findOrdersByCustomerId(UUID customerId) throws ServiceException;
  List<Item> findOrderItems(UUID orderId) throws ServiceException;
  Order findOrderById(UUID orderId) throws ServiceException;
  UUID createOrderFromCart(Cart cart, List<Item> items, Date deliveryDate) throws ServiceException;
}