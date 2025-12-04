package by.zgirskaya.course.service.cart.impl;

import by.zgirskaya.course.dao.cart.OrderDao;
import by.zgirskaya.course.dao.cart.impl.OrderDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Order;
import by.zgirskaya.course.service.cart.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
  private static final Logger logger = LogManager.getLogger();
  private final OrderDao orderDao = new OrderDaoImpl();

  @Override
  public List<Order> findOrdersByCustomerId(UUID customerId) throws ServiceException {
    logger.debug("Finding orders for customer: {}", customerId);

    if (customerId == null) {
      logger.warn("Attempted to find orders with null customer ID");
      throw new ServiceException("Customer ID is required");
    }

    try {
      List<Order> orders = orderDao.findOrdersByCustomerId(customerId);
      logger.debug("Found {} orders for customer: {}", orders.size(), customerId);

      return orders;

    } catch (DaoException e) {
      logger.error("Failed to find orders for customer: {}", customerId, e);
      throw new ServiceException("Failed to find orders: " + e.getMessage(), e);
    }
  }

  @Override
  public double getTotalOrdersAmountByCustomerId(UUID customerId) throws ServiceException {
    logger.debug("Calculating total orders amount for customer: {}", customerId);

    if (customerId == null) {
      throw new ServiceException("Customer ID is required");
    }

    try {
      List<Order> orders = orderDao.findOrdersByCustomerId(customerId);

      double totalAmount = orders.stream()
          .mapToDouble(Order::getOrderPrice)
          .sum();

      logger.debug("Total orders amount for customer {}: {}", customerId, totalAmount);

      return totalAmount;

    } catch (DaoException e) {
      logger.error("Failed to calculate total orders amount for customer: {}", customerId, e);
      throw new ServiceException("Failed to calculate total orders amount", e);
    }
  }

  @Override
  public int getOrderCountByCustomerId(UUID customerId) throws ServiceException {
    logger.debug("Getting order count for customer: {}", customerId);

    if (customerId == null) {
      throw new ServiceException("Customer ID is required");
    }

    try {
      List<Order> orders = orderDao.findOrdersByCustomerId(customerId);
      int count = orders.size();

      logger.debug("Order count for customer {}: {}", customerId, count);

      return count;

    } catch (DaoException e) {
      logger.error("Failed to get order count for customer: {}", customerId, e);
      throw new ServiceException("Failed to get order count", e);
    }
  }

  @Override
  public boolean hasActiveOrders(UUID customerId) throws ServiceException {
    logger.debug("Checking if customer has active orders: {}", customerId);

    if (customerId == null) {
      return false;
    }

    try {
      List<Order> orders = orderDao.findOrdersByCustomerId(customerId);

      boolean hasActive = orders.stream()
          .anyMatch(order -> order.getDeliveryDate().after(new java.util.Date()));

      logger.debug("Customer {} has active orders: {}", customerId, hasActive);

      return hasActive;

    } catch (DaoException e) {
      logger.error("Failed to check active orders for customer: {}", customerId, e);
      throw new ServiceException("Failed to check active orders", e);
    }
  }
}