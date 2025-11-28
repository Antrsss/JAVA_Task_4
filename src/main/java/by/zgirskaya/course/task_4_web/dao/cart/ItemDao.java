package by.zgirskaya.course.task_4_web.dao.cart;

import by.zgirskaya.course.task_4_web.dao.BaseDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.cart.Item;

import java.util.List;
import java.util.UUID;

public interface ItemDao extends BaseDao<Item> {
  List<Item> getItemsByOrderId(UUID orderId) throws DaoException;

  void increaseItemCount(Item item) throws DaoException;
  void decreaseItemCount(Item item) throws DaoException;
  void deleteItemById(UUID itemId) throws DaoException;
}
