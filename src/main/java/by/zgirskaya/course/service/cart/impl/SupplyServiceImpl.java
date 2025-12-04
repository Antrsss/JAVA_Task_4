package by.zgirskaya.course.service.cart.impl;

import by.zgirskaya.course.dao.cart.SupplyDao;
import by.zgirskaya.course.dao.cart.impl.SupplyDaoImpl;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Supply;
import by.zgirskaya.course.service.BaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SupplyServiceImpl implements BaseService<Supply> {
  private static final Logger logger = LogManager.getLogger();
  private static final SupplyDao supplyDao = new SupplyDaoImpl();

  @Override
  public boolean exists(UUID id) throws ServiceException {
    if (id == null) {
      return false;
    }

    try {
      Optional<Supply> supply = supplyDao.findSupplyById(id);
      return supply.isPresent();

    } catch (DaoException e) {
      logger.error("Failed to check if supply exists: {}", id, e);
      throw new ServiceException("");
    }
  }

  @Override
  public Supply create(Supply supply) throws ServiceException {
    logger.info("Creating new supply (Employee: {}, Publisher: {})",
        supply.getEmployeeId(), supply.getPublisherId());

    try {
      supplyDao.create(supply);
      logger.info("Supply created successfully: {} (Employee: {}, Publisher: {})",
          supply.getId(), supply.getEmployeeId(), supply.getPublisherId());

      return supply;

    } catch (DaoException e) {
      logger.error("Failed to create supply", e);
      throw new ServiceException("Failed to create supply: " + e.getMessage(), e);
    }
  }

  @Override
  public Supply update(Supply supply) throws ServiceException {
    logger.info("Updating supply: {}", supply.getId());

    if (supply.getId() == null) {
      throw new ServiceException("Supply ID is required for update");
    }

    try {
      if (!exists(supply.getId())) {
        throw new ServiceException("Supply with ID " + supply.getId() + " not found");
      }

      supplyDao.updateSupply(supply);
      logger.info("Supply updated successfully: {}", supply.getId());

      return supply;

    } catch (DaoException e) {
      logger.error("Failed to update supply: {}", supply.getId(), e);
      throw new ServiceException("Failed to update supply: " + e.getMessage(), e);
    }
  }

  @Override
  public void delete(UUID id) throws ServiceException {
    logger.info("Deleting supply: {}", id);

    if (id == null) {
      throw new ServiceException("Supply ID is required for deletion");
    }

    try {
      if (!exists(id)) {
        throw new ServiceException("Supply with ID " + id + " not found");
      }

      supplyDao.deleteSupply(id);
      logger.info("Supply deleted successfully: {}", id);

    } catch (DaoException e) {
      logger.error("Failed to delete supply: {}", id, e);
      throw new ServiceException("Failed to delete supply: " + e.getMessage(), e);
    }
  }

  @Override
  public List<Supply> getAll() throws ServiceException {
    logger.debug("Getting all supplies");

    try {
      List<Supply> supplies = supplyDao.getAllSupplies();
      logger.debug("Retrieved {} supplies", supplies.size());

      return supplies;

    } catch (DaoException e) {
      logger.error("Failed to get all supplies", e);
      throw new ServiceException("Failed to get supplies: " + e.getMessage(), e);
    }
  }
}