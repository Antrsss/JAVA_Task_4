package by.zgirskaya.course.model;

import java.util.UUID;

public class Item extends AbstractModel {
  private UUID cartId;
  private UUID orderId;
  private UUID bookId;
  private int quantity;
  private Double totalPrice;
  private Double unitPrice;

  public Item() {}

  public Item(UUID id, UUID cartId, UUID orderId, UUID bookId, int quantity, Double unitPrice) {
    setId(id);
    this.cartId = cartId;
    this.orderId = orderId;
    this.bookId = bookId;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = calculateTotalPrice();
  }

  private Double calculateTotalPrice() {
    if (unitPrice == null) return 0.0;
    return unitPrice * quantity;
  }

  public UUID getCartId() { return cartId; }
  public UUID getOrderId() { return orderId; }
  public UUID getBookId() { return bookId; }
  public int getQuantity() { return quantity; }
  public Double getTotalPrice() { return totalPrice; }
  public Double getUnitPrice() { return unitPrice; }

  public void setBookId(UUID bookId) { this.bookId = bookId; }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
    if (this.unitPrice != null) {
      this.totalPrice = this.unitPrice * quantity;
    }
  }

  public void setOrderId(UUID orderId) { this.orderId = orderId; }

  public void setCartId(UUID cartId) { this.cartId = cartId; }
}