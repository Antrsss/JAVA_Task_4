package by.zgirskaya.course.task_4_web.model.cart;

import by.zgirskaya.course.task_4_web.model.AbstractModel;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Order extends AbstractModel {
  private final UUID customerId;
  private final Timestamp purchaseDate;
  private final Date deliveryDate;
  private final Double orderPrice;

  public Order(UUID customerId, Timestamp purchaseDate, Date deliveryDate, Double orderPrice) {
    this.customerId = customerId;
    this.purchaseDate = purchaseDate;
    this.deliveryDate = deliveryDate;
    this.orderPrice = orderPrice;
  }

  public UUID getCustomerId() { return customerId; }
  public Timestamp getPurchaseDate() { return purchaseDate; }
  public Date getDeliveryDate() { return deliveryDate; }
  public Double getOrderPrice() { return orderPrice; }
}
