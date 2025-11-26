package by.zgirskaya.course.task_4_web.model.user;

import by.zgirskaya.course.task_4_web.model.AbstractModel;

public class Role extends AbstractModel {
  private final String roleName;

  public Role(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleName() { return roleName; }
}
