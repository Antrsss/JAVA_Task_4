package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Item;

import java.util.List;
import java.util.UUID;

public interface ItemService {
  List<Item> findItemsByOrderId(UUID orderId) throws ServiceException;
  List<Item> findItemsByCartId(UUID cartId) throws ServiceException;
  Item addItemToCart(UUID cartId, UUID bookId, int quantity, double unitPrice) throws ServiceException;
  void removeItemFromCart(UUID itemId) throws ServiceException;
  void clearCart(UUID cartId) throws ServiceException;
}