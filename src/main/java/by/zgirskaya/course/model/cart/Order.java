package by.zgirskaya.course.model.cart;

import by.zgirskaya.course.model.AbstractModel;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Order extends AbstractModel {
  private UUID customerId;
  private Timestamp purchaseDate;
  private Date deliveryDate;
  private Double orderPrice;
  private String orderStatus;

  public Order(UUID customerId, Timestamp purchaseDate, Date deliveryDate, Double orderPrice) {
    this.customerId = customerId;
    this.purchaseDate = purchaseDate;
    this.deliveryDate = deliveryDate;
    this.orderPrice = orderPrice;
    this.orderStatus = "IN_CART";
  }

  public UUID getCustomerId() { return customerId; }
  public Timestamp getPurchaseDate() { return purchaseDate; }
  public Date getDeliveryDate() { return deliveryDate; }
  public Double getOrderPrice() { return orderPrice; }
  public String getOrderStatus() { return orderStatus; }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public void setOrderPrice(Double orderPrice) {
    this.orderPrice = orderPrice;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }
}
