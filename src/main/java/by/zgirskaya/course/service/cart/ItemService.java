package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Item;

import java.util.List;
import java.util.UUID;

public interface ItemService {
  List<Item> findItemsByOrderId(UUID orderId) throws ServiceException;
  Item increaseItemCount(UUID itemId) throws ServiceException;
  Item decreaseItemCount(UUID itemId) throws ServiceException;
  void deleteItemById(UUID itemId) throws ServiceException;
  Item addItemToOrder(UUID orderId, UUID bookId, int quantity, double price) throws ServiceException;
  double getOrderTotalAmount(UUID orderId) throws ServiceException;
  int getOrderTotalQuantity(UUID orderId) throws ServiceException;
  void clearOrderItems(UUID orderId) throws ServiceException;
}