package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Cart;
import by.zgirskaya.course.model.cart.Item;

import java.util.List;
import java.util.UUID;

public interface CartService {
  Cart getOrCreateCartForCustomer(UUID customerId) throws ServiceException;
}
