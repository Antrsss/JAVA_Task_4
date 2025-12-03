package by.zgirskaya.course.dao.cart;

import by.zgirskaya.course.dao.BaseDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Item;

import java.util.List;
import java.util.UUID;

public interface ItemDao extends BaseDao<Item> {
  List<Item> getItemsByOrderId(UUID orderId) throws DaoException;

  void increaseItemCount(Item item) throws DaoException;
  void decreaseItemCount(Item item) throws DaoException;
  void deleteItemById(UUID itemId) throws DaoException;
}
