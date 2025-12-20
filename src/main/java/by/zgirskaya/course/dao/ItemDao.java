package by.zgirskaya.course.dao;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.Item;

import java.util.List;
import java.util.UUID;

public interface ItemDao extends BaseDao<Item> {
  void update(Item item) throws DaoException;
  void delete(UUID id) throws DaoException;

  Item findById(UUID itemId) throws DaoException;
  Item findItemByCartAndBook(UUID cartId, UUID bookId) throws DaoException;
  List<Item> findItemsByCartId(UUID cartId) throws DaoException;
  List<Item> findItemsByOrderId(UUID orderId) throws DaoException;
}