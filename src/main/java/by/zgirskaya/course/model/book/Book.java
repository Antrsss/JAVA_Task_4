package by.zgirskaya.course.model.book;

import by.zgirskaya.course.model.AbstractModel;

import java.util.UUID;

public class Book extends AbstractModel {
  private UUID publisherId;
  private UUID discountId;
  private String title;
  private Double price;
  private Integer quantity;

  public Book(UUID id, UUID publisherId, UUID discountId, String title, Double price, Integer quantity) {
    setId(id);
    this.publisherId = publisherId;
    this.discountId = discountId;
    this.title = title;
    this.price = price;
    this.quantity = quantity;
  }

  public String getTitle() {
    return title;
  }

  public Double getPrice() {
    return price;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}