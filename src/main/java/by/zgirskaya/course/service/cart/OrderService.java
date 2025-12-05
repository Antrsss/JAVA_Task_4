package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.model.cart.Order;

import java.util.List;
import java.util.UUID;

import java.util.Date;

public interface OrderService {
  double getTotalOrdersAmountByCustomerId(UUID customerId) throws ServiceException;
  Order getOrCreateCurrentOrder(UUID customerId) throws ServiceException;
  Order getCurrentOrder(UUID customerId) throws ServiceException;
  List<Order> getCompletedOrders(UUID customerId) throws ServiceException;
  Item addItemToOrder(UUID customerId, UUID bookId, int quantity) throws ServiceException;
  boolean removeItemFromOrder(UUID customerId, UUID bookId) throws ServiceException;
  List<Item> getOrderItems(UUID orderId) throws ServiceException;
  boolean checkoutOrder(UUID customerId, Date deliveryDate) throws ServiceException;
  void clearOrder(UUID orderId) throws ServiceException;
}