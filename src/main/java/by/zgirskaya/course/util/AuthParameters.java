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

  private AuthParameters() {}
}