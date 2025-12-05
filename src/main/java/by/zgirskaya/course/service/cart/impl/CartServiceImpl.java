package by.zgirskaya.course.service.cart.impl;

import by.zgirskaya.course.dao.cart.CartDao;
import by.zgirskaya.course.dao.cart.impl.CartDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Cart;
import by.zgirskaya.course.service.cart.CartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class CartServiceImpl implements CartService {
  private static final Logger logger = LogManager.getLogger();
  private final CartDao cartDao = new CartDaoImpl();

  @Override
  public Cart getOrCreateCartForCustomer(UUID customerId) throws ServiceException {
    logger.debug("Getting or creating cart for customer ID: {}", customerId);

    if (customerId == null) {
      logger.warn("Attempted to get/create cart with null customer ID");
      throw new ServiceException("Customer ID is required");
    }

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
}