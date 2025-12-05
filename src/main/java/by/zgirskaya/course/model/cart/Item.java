package by.zgirskaya.course.model.cart;

import by.zgirskaya.course.model.AbstractModel;

import java.util.UUID;

public class Item extends AbstractModel {
  private UUID orderId;
  private UUID bookId;
  private int quantity;
  private Double totalPrice;
  private Double unitPrice;

  public Item(UUID orderId, UUID bookId, int quantity, Double unitPrice) {
    this.orderId = orderId;
    this.bookId = bookId;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = unitPrice * quantity;
  }

  public UUID getOrderId() { return orderId; }
  public UUID getBookId() { return bookId; }
  public int getQuantity() { return quantity; }
  public Double getTotalPrice() { return totalPrice; }
  public Double getUnitPrice() { return unitPrice; }

  public void setOrderId(UUID orderId) { this.orderId = orderId; }
  public void setBookId(UUID bookId) { this.bookId = bookId; }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
    if (this.unitPrice != null) {
      this.totalPrice = this.unitPrice * quantity;
    }
  }

  public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

  public void setUnitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
    if (this.quantity > 0) {
      this.totalPrice = unitPrice * this.quantity;
    }
  }
}