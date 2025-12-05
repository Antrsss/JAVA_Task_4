package by.zgirskaya.course.dao.cart;

import by.zgirskaya.course.dao.BaseDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Item;

import java.util.List;
import java.util.UUID;

public interface ItemDao extends BaseDao<Item> {
  boolean update(Item item) throws DaoException;
  boolean delete(UUID id) throws DaoException;

  Item findById(UUID itemId) throws DaoException;
  Item findItemByCartAndBook(UUID cartId, UUID bookId) throws DaoException;
  List<Item> findItemsByCartId(UUID cartId) throws DaoException;
  List<Item> findItemsByOrderId(UUID orderId) throws DaoException;
}