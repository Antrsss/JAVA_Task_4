package by.zgirskaya.course.util;

public final class PageParameters {

  public static final class Title {
    public static final String HOME = "Home";
    public static final String LOGIN = "Login";
    public static final String REGISTER = "Register";

    private Title() {}
  }

  public static final class Jsp {
    public static final String TEMPLATE = "/WEB-INF/jsp/common/template.jsp";

    public static final String HOME = "/WEB-INF/jsp/home-content.jsp";
    public static final String LOGIN = "/WEB-INF/jsp/auth/login-content.jsp";
    public static final String REGISTER = "/WEB-INF/jsp/auth/register-content.jsp";

    private Jsp() {}
  }

  public static final class Path {
    public static final String HOME = "";
    public static final String ROOT = "/";
    public static final String LOGIN = "/auth/login";
    public static final String REGISTER = "/auth/register";
    public static final String LOGOUT = "/auth/logout";

    public static final String LOGIN_REDIRECT = "/auth/login";

    private Path() {}
  }

  private PageParameters() {}
}
