package by.zgirskaya.course.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Order extends AbstractModel {
  private final UUID customerId;
  private final Timestamp purchaseDate;
  private Date deliveryDate;
  private final Double orderPrice;

  public Order(UUID id, UUID customerId, Timestamp purchaseDate, Double orderPrice) {
    setId(id);
    this.customerId = customerId;
    this.purchaseDate = purchaseDate;
    this.orderPrice = orderPrice;
  }

  public UUID getCustomerId() { return customerId; }
  public Timestamp getPurchaseDate() { return purchaseDate; }
  public Date getDeliveryDate() { return deliveryDate; }
  public Double getOrderPrice() { return orderPrice; }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }
}
