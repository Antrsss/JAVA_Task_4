package by.zgirskaya.course.task_4_web.model.user;

import java.util.UUID;

public class Employee extends AbstractUserModel {
  private final String passportId;

  public Employee(String name, String phoneNumber, String email,
                  String password, UUID roleId, String passportId) {

    super(name, phoneNumber, email, password, roleId);
    this.passportId = passportId;
  }

  public String getPassportId() { return passportId; }
}
