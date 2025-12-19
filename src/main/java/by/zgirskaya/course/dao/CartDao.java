package by.zgirskaya.course.dao;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.Cart;

import java.util.UUID;

public interface CartDao {
  Cart findCartByCustomerId(UUID customerId) throws DaoException;
  Cart createCartForCustomer(UUID customerId) throws DaoException;
  void updateCart(Cart cart) throws DaoException;
}
