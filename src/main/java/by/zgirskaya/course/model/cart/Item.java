package by.zgirskaya.course.model.cart;

import by.zgirskaya.course.model.AbstractModel;

import java.util.UUID;

public class Item extends AbstractModel {
  private final UUID orderId;
  private final UUID bookId;
  private final int quantity;
  private final Double totalPrice;

  public Item(UUID orderId, UUID bookId, int quantity, Double totalPrice) {
    this.orderId = orderId;
    this.bookId = bookId;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
  }

  public UUID getOrderId() { return orderId; }
  public UUID getBookId() { return bookId; }
  public int getQuantity() { return quantity; }
  public Double getTotalPrice() { return totalPrice; }
}
