package by.zgirskaya.course.task_4_web.model.user;

import by.zgirskaya.course.task_4_web.model.AbstractModel;

public class RoleModel extends AbstractModel {
    private final String roleName;

    public RoleModel(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() { return roleName; }
}
