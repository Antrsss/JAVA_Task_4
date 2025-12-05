package by.zgirskaya.course.dao.cart;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartDao {
  Cart findCartByCustomerId(UUID customerId) throws DaoException;
  Optional<Cart> findCartById(UUID cartId) throws DaoException;
  Cart createCartForCustomer(UUID customerId) throws DaoException;
  boolean updateCart(Cart cart) throws DaoException;
  boolean deleteCart(UUID cartId) throws DaoException;
}
