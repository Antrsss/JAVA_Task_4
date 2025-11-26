package by.zgirskaya.course.task_4_web.model.user;

import by.zgirskaya.course.task_4_web.model.AbstractModel;

import java.util.UUID;

public class CustomerModel extends AbstractUserModel {
    private final String username;

    public CustomerModel(String name, String phoneNumber, String email, String password, UUID roleId, String username) {
        super(name, phoneNumber, email, password, roleId);
        this.username = username;
    }

    public String getUsername() { return username; }
}
