package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Cart;
import java.util.UUID;

public interface CartService {
  Cart getOrCreateCartForCustomer(UUID customerId) throws ServiceException;
  double calculateCartTotal(UUID cartId) throws ServiceException;
}
