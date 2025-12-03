package by.zgirskaya.course.util;

public final class AuthParameters {

  public static final class Roles {
    public static final String EMPLOYEE = "employee";
    public static final String CUSTOMER = "customer";

    private Roles() {}
  }

  public static final class Parameters {
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    public static final String USERNAME = "username";
    public static final String PASSPORT_ID = "passportId";

    public static final String IDENTIFIER = "identifier";

    private Parameters() {}
  }

  public static final class Validation {
    public static final String PHONE_OR_EMAIL_REQUIRED = "Phone number or email is required";
    public static final String PHONE_OR_EMAIL_EXISTS = "User with this phone number or email already exists";
    public static final String PASSWORD_REQUIRED = "Password is required";

    private Validation() {}
  }

  private AuthParameters() {}
}