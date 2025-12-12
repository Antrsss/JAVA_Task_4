package by.zgirskaya.course.service.cart.impl;

import by.zgirskaya.course.dao.cart.CartDao;
import by.zgirskaya.course.dao.cart.ItemDao;
import by.zgirskaya.course.dao.cart.impl.CartDaoImpl;
import by.zgirskaya.course.dao.cart.impl.ItemDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Cart;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.service.cart.CartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

public class CartServiceImpl implements CartService {
  private static final Logger logger = LogManager.getLogger();
  private final CartDao cartDao = new CartDaoImpl();
  private final ItemDao itemDao = new ItemDaoImpl();

  @Override
  public Cart findOrCreateCartForCustomer(UUID customerId) throws ServiceException {
    logger.debug("Getting or creating cart for customer ID: {}", customerId);

    try {
      Cart cart = cartDao.findCartByCustomerId(customerId);

      if (cart == null) {
        logger.info("No cart found for customer ID: {}, creating new cart", customerId);
        cart = cartDao.createCartForCustomer(customerId);
      } else {
        logger.debug("Found existing cart for customer ID {}: {}", customerId, cart.getId());
        cartDao.updateCart(cart);
      }

      return cart;

    } catch (DaoException e) {
      logger.error("Error getting/creating cart for customer ID: {}", customerId, e);
      throw new ServiceException("Error getting/creating cart: " + e.getMessage(), e);
    }
  }

  @Override
  public double calculateCartTotal(UUID cartId) throws ServiceException {
    logger.debug("Calculating cart total for customer ID: {}", cartId);

    try {
      List<Item> items = itemDao.findItemsByCartId(cartId);

      if (items == null || items.isEmpty()) {
        logger.debug("No items in cart for customer ID: {}, returning 0", cartId);
        return 0.0;
      }

      double total = 0.0;
      for (Item item : items) {
        if (item != null && item.getTotalPrice() != null) {
          total += item.getTotalPrice();
        }
      }

      logger.debug("Calculated cart total for customer ID {}: {}", cartId, total);
      return total;

    } catch (DaoException e) {
      logger.error("Error calculating cart total for customer ID: {}", cartId, e);
      throw new ServiceException("Error calculating cart total: " + e.getMessage(), e);
    }
  }
}