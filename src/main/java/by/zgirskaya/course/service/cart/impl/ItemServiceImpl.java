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
  public List<Item> findItemsByCartId(UUID cartId) throws ServiceException {
    logger.debug("Getting items for cart: {}", cartId);

    if (cartId == null) {
      logger.warn("Attempted to get items with null cart ID");
      throw new ServiceException("Cart ID is required");
    }

    try {
      List<Item> items = itemDao.findItemsByCartId(cartId);
      logger.debug("Found {} items for cart: {}", items.size(), cartId);
      return items;

    } catch (DaoException e) {
      logger.error("Failed to get items for cart: {}", cartId, e);
      throw new ServiceException("Failed to get items: " + e.getMessage(), e);
    }
  }

  @Override
  public Item addItemToCart(UUID cartId, UUID bookId, int quantity, double unitPrice) throws ServiceException {
    logger.debug("Adding item to cart: cartId={}, bookId={}, quantity={}",
        cartId, bookId, quantity);

    if (cartId == null) {
      logger.warn("Attempted to add item to cart with null cart ID");
      throw new ServiceException("Cart ID is required");
    }

    if (bookId == null) {
      logger.warn("Attempted to add item to cart with null book ID");
      throw new ServiceException("Book ID is required");
    }

    if (quantity <= 0) {
      logger.warn("Attempted to add item with invalid quantity: {}", quantity);
      throw new ServiceException("Quantity must be positive");
    }

    if (unitPrice <= 0) {
      logger.warn("Attempted to add item with invalid unitPrice: {}", quantity);
      throw new ServiceException("UnitPrice must be positive");
    }

    try {
      Item existingItem = itemDao.findItemByCartAndBook(cartId, bookId);

      if (existingItem != null) {
        logger.debug("Item already exists in cart, updating quantity");
        existingItem.setQuantity(existingItem.getQuantity() + quantity);
        itemDao.update(existingItem);
        logger.info("Updated quantity for item {} in cart {}, new quantity: {}",
            bookId, cartId, existingItem.getQuantity());
      } else {
        logger.debug("Creating new item in cart");
        Item newItem = new Item(UUID.randomUUID(), cartId, null, bookId, quantity, unitPrice);

        itemDao.create(newItem);
        logger.info("Added new item {} to cart {}, quantity: {}, unitPrice: {}",
            bookId, cartId, quantity, unitPrice);

        return newItem;
      }
      return existingItem;

    } catch (DaoException e) {
      logger.error("Failed to add item to cart: cartId={}, bookId={}", cartId, bookId, e);
      throw new ServiceException("Failed to add item to cart: " + e.getMessage(), e);
    }
  }

  @Override
  public void removeItemFromCart(UUID itemId) throws ServiceException {
    logger.debug("Removing item from cart: {}", itemId);

    if (itemId == null) {
      logger.warn("Attempted to remove item with null ID");
      throw new ServiceException("Item ID is required");
    }

    try {
      Item item = itemDao.findById(itemId);

      if (item == null) {
        logger.warn("Item not found with ID: {}", itemId);
        throw new ServiceException("Item not found with ID: " + itemId);
      }

      itemDao.delete(itemId);
      logger.info("Item removed successfully from cart: {} (bookId: {})",
          itemId, item.getBookId());

    } catch (DaoException e) {
      logger.error("Failed to remove item from cart: {}", itemId, e);
      throw new ServiceException("Failed to remove item from cart: " + e.getMessage(), e);
    }
  }

  @Override
  public void clearCart(UUID cartId) throws ServiceException {
    logger.debug("Clearing cart: {}", cartId);

    if (cartId == null) {
      logger.warn("Attempted to clear cart with null ID");
      throw new ServiceException("Cart ID is required");
    }

    try {
      List<Item> items = findItemsByCartId(cartId);
      logger.debug("Found {} items to remove from cart: {}", items.size(), cartId);

      for (Item item : items) {
        itemDao.delete(item.getId());
        logger.debug("Removed item: {} from cart: {}", item.getId(), cartId);
      }

      logger.info("Cart cleared successfully: {} items removed from cart: {}",
          items.size(), cartId);

    } catch (DaoException e) {
      logger.error("Failed to clear cart: {}", cartId, e);
      throw new ServiceException("Failed to clear cart: " + e.getMessage(), e);
    }
  }
}