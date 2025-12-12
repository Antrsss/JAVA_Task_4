package by.zgirskaya.course.model.cart;

import by.zgirskaya.course.model.AbstractModel;

import java.sql.Timestamp;
import java.util.UUID;

public class Cart extends AbstractModel {
  private final UUID customerId;
  private final Timestamp createdAt;
  private Timestamp updatedAt;

  public Cart(UUID id, UUID customerId, Timestamp createdAt, Timestamp updatedAt) {
    setId(id);
    this.customerId = customerId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getCustomerId() { return customerId; }

  public Timestamp getCreatedAt() { return createdAt; }

  public Timestamp getUpdatedAt() { return updatedAt; }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }
}