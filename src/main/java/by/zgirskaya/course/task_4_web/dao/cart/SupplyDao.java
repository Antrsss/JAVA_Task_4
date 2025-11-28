package by.zgirskaya.course.task_4_web.dao.cart;

import by.zgirskaya.course.task_4_web.dao.BaseDao;
import by.zgirskaya.course.task_4_web.exception.DaoException;
import by.zgirskaya.course.task_4_web.model.cart.Supply;

import java.util.List;
import java.util.UUID;

public interface SupplyDao extends BaseDao<Supply> {
  void updateSupply(Supply supply) throws DaoException;
  void deleteSupply(UUID id) throws DaoException;

  List<Supply> getAllSupplies() throws DaoException;
}
