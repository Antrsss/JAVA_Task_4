package by.zgirskaya.course.service.cart.impl;

import by.zgirskaya.course.dao.book.BookDao;
import by.zgirskaya.course.dao.book.impl.BookDaoImpl;
import by.zgirskaya.course.dao.cart.OrderDao;
import by.zgirskaya.course.dao.cart.ItemDao;
import by.zgirskaya.course.dao.cart.impl.OrderDaoImpl;
import by.zgirskaya.course.dao.cart.impl.ItemDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.book.Book;
import by.zgirskaya.course.model.cart.Cart;
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
  private final BookDao bookDao = new BookDaoImpl();

  @Override
  public List<Order> findOrdersByCustomerId(UUID customerId) throws ServiceException {
    logger.debug("Getting completed orders for customer: {}", customerId);

    try {
      List<Order> completedOrders = orderDao.findOrdersByCustomerId(customerId);

      logger.debug("Found {} completed orders for customer: {}", completedOrders.size(), customerId);
      return completedOrders;

    } catch (DaoException e) {
      logger.error("Failed to get completed orders for customer: {}", customerId, e);
      throw new ServiceException("Failed to get completed orders", e);
    }
  }

  @Override
  public List<Item> getOrderItems(UUID orderId) throws ServiceException {
    logger.debug("Getting items for order: {}", orderId);

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
  public Order getOrderById(UUID orderId) throws ServiceException {
    logger.debug("Getting order by ID: {}", orderId);

    if (orderId == null) {
      throw new ServiceException("Order ID is required");
    }

    try {
      Order order = orderDao.findOrderById(orderId);
      logger.debug("Found order: {}", orderId);
      return order;

    } catch (DaoException e) {
      logger.error("Failed to get order by ID: {}", orderId, e);
      throw new ServiceException("Failed to get order", e);
    }
  }

  @Override
  public UUID createOrderFromCart(Cart cart, List<Item> items, Date deliveryDate) throws ServiceException {
    logger.debug("Creating order from cart: {} with {} items", cart.getId(), items.size());

    double totalAmount = calculateOrderTotal(items);

    UUID orderId;

    try {
      orderId = UUID.randomUUID();
      Timestamp purchaseDate = new Timestamp(System.currentTimeMillis());

      Order order = new Order(orderId, cart.getCustomerId(), purchaseDate, totalAmount);
      order.setDeliveryDate(deliveryDate);

      logger.debug("ORDER 1: {}", totalAmount);
      orderDao.create(order);
      logger.info("Order created: {} for customer {} with price {}", order.getId(), order.getCustomerId(), order.getOrderPrice());

      transferItemsToOrder(items, orderId);
      logger.info("Transferred {} items to order {}", items.size(), orderId);

      checkBookAvailability(items);

      logger.info("Order {} successfully created and processing", orderId);

      logger.debug("ORDER_PRICE: {}", orderDao.findOrderById(orderId).getOrderPrice());
      return orderId;

    } catch (DaoException e) {
      logger.error("Failed to create order from cart: {}", cart.getId(), e);
      throw new ServiceException("Failed to create order: " + e.getMessage(), e);
    } catch (Exception e) {
      logger.error("Unexpected error creating order from cart: {}", cart.getId(), e);
      throw new ServiceException("Unexpected error creating order", e);
    }
  }

  private double calculateOrderTotal(List<Item> items) {
    double total = 0.0;

    for (Item item : items) {
      total += item.getTotalPrice();
    }

    logger.debug("Calculated order total: {}", total);
    return total;
  }

  private void transferItemsToOrder(List<Item> items, UUID orderId) throws ServiceException {
    logger.debug("Transferring {} items to order: {}", items.size(), orderId);

    try {
      for (Item item : items) {
        logger.debug("ITEMS_TO_ORDER: unit_price: {}, total_price: {}", item.getUnitPrice(), item.getTotalPrice());
        item.setOrderId(orderId);
        itemDao.update(item);
        logger.debug("Transferred item {} to order {}", item.getId(), orderId);
      }
    } catch (DaoException e) {
      throw new ServiceException("Transferring item to order failed!");
    }
  }

  private void checkBookAvailability(List<Item> items) throws ServiceException {
    for (Item item : items) {
      try {
        Book book = bookDao.findBookById(item.getBookId());
        int availableQuantity = book.getQuantity();
        if (availableQuantity < item.getQuantity()) {
          logger.warn("Insufficient stock for book: {}, requested: {}, available: {}",
              item.getBookId(), item.getQuantity(), availableQuantity);
        }
      } catch (DaoException e) {
        logger.error("Failed to check availability for book: {}", item.getBookId(), e);
        throw new ServiceException("Unexpected error checking book count", e);
      }
    }
  }
}