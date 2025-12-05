package by.zgirskaya.course.service.cart.impl;

import by.zgirskaya.course.dao.cart.OrderDao;
import by.zgirskaya.course.dao.cart.ItemDao;
import by.zgirskaya.course.dao.cart.impl.OrderDaoImpl;
import by.zgirskaya.course.dao.cart.impl.ItemDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Order;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.service.cart.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
  private static final Logger logger = LogManager.getLogger();
  private final OrderDao orderDao = new OrderDaoImpl();
  private final ItemDao itemDao = new ItemDaoImpl();

  @Override
  public double getTotalOrdersAmountByCustomerId(UUID customerId) throws ServiceException {
    logger.debug("Calculating total orders amount for customer: {}", customerId);

    if (customerId == null) {
      throw new ServiceException("Customer ID is required");
    }

    try {
      List<Order> orders = orderDao.findOrdersByCustomerId(customerId);

      double totalAmount = orders.stream()
          .mapToDouble(order -> order.getOrderPrice() != null ? order.getOrderPrice() : 0.0)
          .sum();

      logger.debug("Total orders amount for customer {}: {}", customerId, totalAmount);
      return totalAmount;

    } catch (DaoException e) {
      logger.error("Failed to calculate total orders amount for customer: {}", customerId, e);
      throw new ServiceException("Failed to calculate total orders amount", e);
    }
  }

  @Override
  public Order getOrCreateCurrentOrder(UUID customerId) throws ServiceException {
    logger.debug("Getting or creating current order for customer: {}", customerId);

    if (customerId == null) {
      throw new ServiceException("Customer ID is required");
    }

    try {
      Order currentOrder = orderDao.findCurrentOrderByCustomerId(customerId);

      if (currentOrder == null) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        currentOrder = new Order(customerId, now, null, 0.0);

        orderDao.create(currentOrder);
        logger.info("Created new order for customer: {}", customerId);
      }

      logger.debug("Current order for customer {}: {}", customerId, currentOrder.getId());
      return currentOrder;

    } catch (DaoException e) {
      logger.error("Failed to get or create current order for customer: {}", customerId, e);
      throw new ServiceException("Failed to get or create current order", e);
    }
  }

  @Override
  public Order getCurrentOrder(UUID customerId) throws ServiceException {
    logger.debug("Getting current order for customer: {}", customerId);

    if (customerId == null) {
      return null;
    }

    try {
      Order currentOrder = orderDao.findCurrentOrderByCustomerId(customerId);
      logger.debug("Found current order for customer {}: {}", customerId,
          currentOrder != null ? currentOrder.getId() : "null");

      return currentOrder;

    } catch (DaoException e) {
      logger.error("Failed to get current order for customer: {}", customerId, e);
      throw new ServiceException("Failed to get current order", e);
    }
  }

  @Override
  public List<Order> getCompletedOrders(UUID customerId) throws ServiceException {
    logger.debug("Getting completed orders for customer: {}", customerId);

    if (customerId == null) {
      throw new ServiceException("Customer ID is required");
    }

    try {
      List<Order> completedOrders = orderDao.findOrdersByCustomerIdAndStatus(customerId, "COMPLETED");

      logger.debug("Found {} completed orders for customer: {}", completedOrders.size(), customerId);
      return completedOrders;

    } catch (DaoException e) {
      logger.error("Failed to get completed orders for customer: {}", customerId, e);
      throw new ServiceException("Failed to get completed orders", e);
    }
  }

  @Override
  public Item addItemToOrder(UUID customerId, UUID bookId, int quantity) throws ServiceException {
    logger.debug("Adding item to order: customer={}, book={}, quantity={}",
        customerId, bookId, quantity);

    if (customerId == null || bookId == null) {
      throw new ServiceException("Customer ID and Book ID are required");
    }

    if (quantity <= 0) {
      throw new ServiceException("Quantity must be positive");
    }

    try {
      Order currentOrder = getOrCreateCurrentOrder(customerId);
      Item existingItem = itemDao.findByOrderIdAndBookId(currentOrder.getId(), bookId);

      if (existingItem != null) {
        existingItem.setQuantity(existingItem.getQuantity() + quantity);
        itemDao.update(existingItem);
        logger.debug("Updated existing item quantity: {}", existingItem.getId());

        updateOrderTotalPrice(currentOrder.getId());
        return existingItem;
      } else {
        // TODO: Получить цену книги из BookService
        double unitPrice = 10.0; // Временная заглушка

        Item newItem = new Item(currentOrder.getId(), bookId, quantity, unitPrice);

        itemDao.create(newItem);
        logger.debug("Created new item for order: {}", currentOrder.getId());

        updateOrderTotalPrice(currentOrder.getId());

        return newItem;
      }

    } catch (DaoException e) {
      logger.error("Failed to add item to order", e);
      throw new ServiceException("Failed to add item to order", e);
    }
  }

  @Override
  public boolean removeItemFromOrder(UUID customerId, UUID bookId) throws ServiceException {
    logger.debug("Removing item from order: customer={}, book={}", customerId, bookId);

    if (customerId == null || bookId == null) {
      throw new ServiceException("Customer ID and Book ID are required");
    }

    try {
      Order currentOrder = getCurrentOrder(customerId);

      if (currentOrder == null) {
        logger.warn("No current order found for customer: {}", customerId);
        return false;
      }

      Item itemToRemove = itemDao.findByOrderIdAndBookId(currentOrder.getId(), bookId);

      if (itemToRemove != null) {
        boolean deleted = itemDao.delete(itemToRemove.getId());

        if (deleted) {
          updateOrderTotalPrice(currentOrder.getId());
          logger.debug("Removed item from order: {}", itemToRemove.getId());
          return true;
        }
      }

      logger.debug("Item not found in order: bookId={}", bookId);
      return false;

    } catch (DaoException e) {
      logger.error("Failed to remove item from order", e);
      throw new ServiceException("Failed to remove item from order", e);
    }
  }

  @Override
  public List<Item> getOrderItems(UUID orderId) throws ServiceException {
    logger.debug("Getting items for order: {}", orderId);

    if (orderId == null) {
      throw new ServiceException("Order ID is required");
    }

    try {
      List<Item> items = itemDao.findItemsByOrderId(orderId);
      logger.debug("Found {} items for order: {}", items.size(), orderId);
      return items;

    } catch (DaoException e) {
      logger.error("Failed to get order items for order: {}", orderId, e);
      throw new ServiceException("Failed to get order items", e);
    }
  }

  @Override
  public boolean checkoutOrder(UUID customerId, Date deliveryDate) throws ServiceException {
    logger.debug("Checking out order for customer: {}, delivery date: {}",
        customerId, deliveryDate);

    if (customerId == null || deliveryDate == null) {
      throw new ServiceException("Customer ID and delivery date are required");
    }

    Date today = new Date();
    if (deliveryDate.before(today)) {
      throw new ServiceException("Delivery date cannot be in the past");
    }

    try {
      Order currentOrder = getCurrentOrder(customerId);

      if (currentOrder == null) {
        logger.warn("No current order found for customer: {}", customerId);
        throw new ServiceException("No active order found");
      }

      List<Item> items = getOrderItems(currentOrder.getId());
      if (items == null || items.isEmpty()) {
        throw new ServiceException("Cannot checkout empty order");
      }

      currentOrder.setOrderStatus("COMPLETED");
      currentOrder.setDeliveryDate(deliveryDate);

      boolean updated = orderDao.update(currentOrder);

      if (updated) {
        logger.info("Order {} checked out successfully for customer: {}",
            currentOrder.getId(), customerId);
        return true;
      }

      return false;

    } catch (DaoException e) {
      logger.error("Failed to checkout order for customer: {}", customerId, e);
      throw new ServiceException("Failed to checkout order", e);
    }
  }

  @Override
  public void clearOrder(UUID orderId) throws ServiceException {
    logger.debug("Clearing order: {}", orderId);

    if (orderId == null) {
      throw new ServiceException("Order ID is required");
    }

    try {
      by.zgirskaya.course.service.cart.ItemService itemService = new ItemServiceImpl();
      itemService.clearOrderItems(orderId);

      Order order = orderDao.findById(orderId);
      if (order != null) {
        order.setOrderPrice(0.0);
        orderDao.update(order);
      }

      logger.info("Order {} cleared successfully", orderId);

    } catch (DaoException e) {
      logger.error("Failed to clear order: {}", orderId, e);
      throw new ServiceException("Failed to clear order", e);
    }
  }

  private void updateOrderTotalPrice(UUID orderId) throws DaoException {
    by.zgirskaya.course.service.cart.ItemService itemService = new ItemServiceImpl();
    try {
      double totalPrice = itemService.getOrderTotalAmount(orderId);

      Order order = orderDao.findById(orderId);
      if (order != null) {
        order.setOrderPrice(totalPrice);
        orderDao.update(order);
      }
    } catch (ServiceException e) {
      throw new DaoException("Failed to update order total price", e);
    }
  }
}