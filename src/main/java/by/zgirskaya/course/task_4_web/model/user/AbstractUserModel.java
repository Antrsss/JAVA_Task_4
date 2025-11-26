package by.zgirskaya.course.task_4_web.model.user;

import by.zgirskaya.course.task_4_web.model.AbstractModel;

import java.util.Objects;
import java.util.UUID;

public class AbstractUserModel extends AbstractModel {
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final String password;
    private final UUID roleId;

    public AbstractUserModel(String name, String phoneNumber, String email, String password, UUID roleId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
    }

    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UUID getRoleId() { return roleId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUserModel that = (AbstractUserModel) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber, email, password, roleId);
    }

    @Override
    public String toString() {
        return "AbstractUserModel[" +
                "name=" + name +
                ", phoneNumber=" + phoneNumber +
                ", email=" + email +
                ", password=" + password +
                ", role_id=" + roleId +
                ']';
    }
}
