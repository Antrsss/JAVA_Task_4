package by.zgirskaya.course.model.cart;

import by.zgirskaya.course.model.AbstractModel;

import java.util.Date;
import java.util.UUID;

public class Supply extends AbstractModel {
  private final UUID employeeId;
  private final UUID publisherId;
  private final Date date;
  private final Double supplyPrice;

  public Supply(UUID employeeId, UUID publisherId, Date date, Double supplyPrice) {
    this.employeeId = employeeId;
    this.publisherId = publisherId;
    this.date = date;
    this.supplyPrice = supplyPrice;
  }

  public UUID getEmployeeId() { return employeeId; }
  public UUID getPublisherId() { return publisherId; }
  public Date getDate() { return date; }
  public Double getSupplyPrice() { return supplyPrice; }
}
