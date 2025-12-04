package by.zgirskaya.course.service.cart.impl;

import by.zgirskaya.course.dao.cart.ItemDao;
import by.zgirskaya.course.dao.cart.impl.ItemDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.service.cart.ItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ItemServiceImpl implements ItemService {
  private static final Logger logger = LogManager.getLogger();
  private final ItemDao itemDao = new ItemDaoImpl();

  @Override
  public List<Item> findItemsByOrderId(UUID orderId) throws ServiceException {
    logger.debug("Getting items for order: {}", orderId);

    if (orderId == null) {
      logger.warn("Attempted to get items with null order ID");
      throw new ServiceException("Order ID is required");
    }

    try {
      List<Item> items = itemDao.findItemsByOrderId(orderId);
      logger.debug("Found {} items for order: {}", items.size(), orderId);

      return items;

    } catch (DaoException e) {
      logger.error("Failed to get items for order: {}", orderId, e);
      throw new ServiceException("Failed to get items: " + e.getMessage(), e);
    }
  }

  @Override
  public Item increaseItemCount(UUID itemId) throws ServiceException {
    logger.debug("Increasing item count: {}", itemId);

    if (itemId == null) {
      throw new ServiceException("Item ID is required");
    }

    try {
      Item item = itemDao.findItemById(itemId)
          .orElseThrow(() -> new ServiceException("Item with ID " + itemId + " not found"));

      itemDao.increaseItemCount(item);

      Item updatedItem = itemDao.findItemById(itemId)
          .orElseThrow(() -> new ServiceException("Failed to retrieve updated item"));

      logger.info("Item count increased: {} (New quantity: {}, Total: {})",
          itemId, updatedItem.getQuantity(), updatedItem.getTotalPrice());

      return updatedItem;

    } catch (DaoException e) {
      logger.error("Failed to increase item count: {}", itemId, e);
      throw new ServiceException("Failed to increase item count: " + e.getMessage(), e);
    }
  }

  @Override
  public Item decreaseItemCount(UUID itemId) throws ServiceException {
    logger.debug("Decreasing item count: {}", itemId);

    if (itemId == null) {
      throw new ServiceException("Item ID is required");
    }

    try {
      Item item = itemDao.findItemById(itemId)
          .orElseThrow(() -> new ServiceException("Item with ID " + itemId + " not found"));

      itemDao.decreaseItemCount(item);

      Optional<Item> updatedItem = itemDao.findItemById(itemId);

      if (updatedItem.isPresent()) {
        logger.info("Item count decreased: {} (New quantity: {}, Total: {})",
            itemId, updatedItem.get().getQuantity(), updatedItem.get().getTotalPrice());
        return updatedItem.get();
      } else {
        logger.info("Item deleted after decreasing count: {}", itemId);
        return null;
      }

    } catch (DaoException e) {
      logger.error("Failed to decrease item count: {}", itemId, e);
      throw new ServiceException("Failed to decrease item count: " + e.getMessage(), e);
    }
  }

  @Override
  public void deleteItemById(UUID itemId) throws ServiceException {
    logger.debug("Deleting item by ID: {}", itemId);

    if (itemId == null) {
      throw new ServiceException("Item ID is required");
    }

    try {
      itemDao.deleteItemById(itemId);
      logger.info("Item deleted successfully: {}", itemId);

    } catch (DaoException e) {
      logger.error("Failed to delete item: {}", itemId, e);
      throw new ServiceException("Failed to delete item: " + e.getMessage(), e);
    }
  }

  @Override
  public Item addItemToOrder(UUID orderId, UUID bookId, int quantity, double price) throws ServiceException {
    logger.info("Adding item to order: {} (Book: {}, Quantity: {}, Price: {})",
        orderId, bookId, quantity, price);

    try {
      List<Item> existingItems = findItemsByOrderId(orderId);
      Optional<Item> existingItem = existingItems.stream()
          .filter(item -> item.getBookId().equals(bookId))
          .findFirst();

      if (existingItem.isPresent()) {
        logger.debug("Item already exists in order, increasing quantity");
        Item item = existingItem.get();
        for (int i = 1; i < quantity; i++) {
          itemDao.increaseItemCount(item);
        }
        return itemDao.findItemById(item.getId())
            .orElseThrow(() -> new ServiceException("Failed to retrieve updated item"));
      } else {
        double totalPrice = price * quantity;
        Item newItem = new Item(orderId, bookId, quantity, totalPrice);

        itemDao.create(newItem);

        logger.info("New item added to order: {} (ID: {})", orderId, newItem.getId());
        return newItem;
      }

    } catch (DaoException e) {
      logger.error("Failed to add item to order: {}", orderId, e);
      throw new ServiceException("Failed to add item to order: " + e.getMessage(), e);
    }
  }

  @Override
  public double getOrderTotalAmount(UUID orderId) throws ServiceException {
    logger.debug("Calculating total amount for order: {}", orderId);

    if (orderId == null) {
      throw new ServiceException("Order ID is required");
    }

    try {
      List<Item> items = findItemsByOrderId(orderId);

      double totalAmount = items.stream()
          .mapToDouble(Item::getTotalPrice)
          .sum();

      logger.debug("Total amount for order {}: {}", orderId, totalAmount);

      return totalAmount;

    } catch (ServiceException e) {
      logger.error("Failed to calculate total amount for order: {}", orderId, e);
      throw new ServiceException("Failed to calculate total amount", e);
    }
  }

  @Override
  public int getOrderTotalQuantity(UUID orderId) throws ServiceException {
    logger.debug("Calculating total quantity for order: {}", orderId);

    if (orderId == null) {
      throw new ServiceException("Order ID is required");
    }

    try {
      List<Item> items = findItemsByOrderId(orderId);

      int totalQuantity = items.stream()
          .mapToInt(Item::getQuantity)
          .sum();

      logger.debug("Total quantity for order {}: {}", orderId, totalQuantity);

      return totalQuantity;

    } catch (ServiceException e) {
      logger.error("Failed to calculate total quantity for order: {}", orderId, e);
      throw new ServiceException("Failed to calculate total quantity", e);
    }
  }

  @Override
  public void clearOrderItems(UUID orderId) throws ServiceException {
    logger.info("Clearing all items from order: {}", orderId);

    if (orderId == null) {
      throw new ServiceException("Order ID is required");
    }

    try {
      List<Item> items = findItemsByOrderId(orderId);

      for (Item item : items) {
        deleteItemById(item.getId());
      }

      logger.info("Cleared {} items from order: {}", items.size(), orderId);

    } catch (ServiceException e) {
      logger.error("Failed to clear items from order: {}", orderId, e);
      throw new ServiceException("Failed to clear order items", e);
    }
  }
}