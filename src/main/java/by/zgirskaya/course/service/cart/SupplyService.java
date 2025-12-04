package by.zgirskaya.course.service.cart;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Supply;

import java.util.List;
import java.util.UUID;

public interface SupplyService {
  boolean isSupplyExists(UUID id) throws ServiceException;

  Supply createSupply(Supply supply) throws ServiceException;
  Supply updateSupply(Supply supply) throws ServiceException;
  void deleteSupply(UUID id) throws ServiceException;

  List<Supply> getAllSupplies() throws ServiceException;
}
