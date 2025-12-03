package by.zgirskaya.course.dao.cart;

import by.zgirskaya.course.dao.BaseDao;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.cart.Supply;

import java.util.List;
import java.util.UUID;

public interface SupplyDao extends BaseDao<Supply> {
  void updateSupply(Supply supply) throws DaoException;
  void deleteSupply(UUID id) throws DaoException;

  List<Supply> getAllSupplies() throws DaoException;
}
