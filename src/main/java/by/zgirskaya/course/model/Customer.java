package by.zgirskaya.course.model;

import java.util.UUID;

public class Customer extends AbstractUserModel {
  private final String username;

  public Customer(String name, String phoneNumber, String email,
                  String password, UUID roleId, String username) {

    super(name, phoneNumber, email, password, roleId);
    this.username = username;
  }

  public String getUsername() { return username; }
}
