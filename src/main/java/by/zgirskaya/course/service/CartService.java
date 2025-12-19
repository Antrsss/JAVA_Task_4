package by.zgirskaya.course.service;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Cart;
import java.util.UUID;

public interface CartService {
  Cart findOrCreateCartForCustomer(UUID customerId) throws ServiceException;
  double calculateCartTotal(UUID cartId) throws ServiceException;
}
