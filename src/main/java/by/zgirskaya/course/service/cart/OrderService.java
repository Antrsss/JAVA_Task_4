package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Cart;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.model.cart.Order;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderService {
  List<Order> getCompletedOrders(UUID customerId) throws ServiceException;
  List<Item> getOrderItems(UUID orderId) throws ServiceException;
  Order getOrderById(UUID orderId) throws ServiceException;
  UUID createOrderFromCart(Cart cart, List<Item> items, Date deliveryDate) throws ServiceException;
}