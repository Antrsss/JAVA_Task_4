package by.zgirskaya.course.task_4_web.util;

public final class AuthParameters {

  public static final class Paths {
    public static final String ROOT = "/";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";

    private Paths() {}
  }

  public static final class Jsp {
    public static final String LOGIN = "/WEB-INF/jsp/auth/login-content.jsp";
    public static final String REGISTER = "/WEB-INF/jsp/auth/register-content.jsp";

    private Jsp() {}
  }

  public static final class Pages {
    public static final String LOGIN_TITLE = "Login";
    public static final String REGISTER_TITLE = "Register";

    private Pages() {}
  }

  public static final class Attributes {
    public static final String ERROR = "error";
    public static final String USER = "user";
    public static final String USER_ROLE = "userRole";

    private Attributes() {}
  }

  public static final class Roles {
    public static final String EMPLOYEE = "Employee";
    public static final String CUSTOMER = "Customer";

    private Roles() {}
  }

  public static final class Parameters {
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phoneNumber";
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

    private Validation() {}
  }

  private AuthParameters() {}
}