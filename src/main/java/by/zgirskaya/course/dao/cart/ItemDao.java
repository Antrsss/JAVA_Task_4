package by.zgirskaya.course.dao.cart;

import by.zgirskaya.course.dao.BaseDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemDao extends BaseDao<Item> {
  Optional<Item> findById(UUID id) throws DaoException;
  boolean update(Item item) throws DaoException;
  boolean delete(UUID id) throws DaoException;

  List<Item> findItemsByOrderId(UUID orderId) throws DaoException;
  Item findByOrderIdAndBookId(UUID orderId, UUID bookId) throws DaoException;
  void deleteItemsByOrderId(UUID orderId) throws DaoException;
}