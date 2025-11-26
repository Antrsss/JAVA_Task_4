package by.zgirskaya.course.task_4_web.model.user;

import java.util.UUID;

public class EmployeeModel extends AbstractUserModel {
    private final String passportId;

    public EmployeeModel(String name, String phoneNumber, String email, String password, UUID roleId, String passportId) {
        super(name, phoneNumber, email, password, roleId);
        this.passportId = passportId;
    }

    public String getPassportId() { return passportId; }
}
