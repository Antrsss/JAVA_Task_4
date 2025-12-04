package by.zgirskaya.course.util;

public final class TableColumns {

  public static final class User {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ROLE_ID = "role_id";

    public static final String USERNAME = "username";
    public static final String PASSPORT_ID = "passport_id";

    private User() {}
  }

  public static final class Role {
    public static final String ID = "id";

    private Role() {}
  }

  private TableColumns() {}
}
