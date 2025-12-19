package by.zgirskaya.course.dao;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.model.Supply;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplyDao extends BaseDao<Supply> {
  Optional<Supply> findSupplyById(UUID id) throws DaoException;
  void deleteSupply(UUID id) throws DaoException;
  List<Supply> getAllSupplies() throws DaoException;
}
