package by.zgirskaya.course.util;

public final class PageParameters {

  public static final class Title {
    public static final String HOME = "Home";
    public static final String LOGIN = "Login";
    public static final String REGISTER = "Register";

    private Title() {}
  }

  public static final class Jsp {
    public static final String TEMPLATE_CONTENT = "/WEB-INF/jsp/common/template.jsp";

    public static final String HOME_CONTENT = "/WEB-INF/jsp/home-content.jsp";
    public static final String LOGIN_CONTENT = "/WEB-INF/jsp/auth/login-content.jsp";
    public static final String REGISTER_CONTENT = "/WEB-INF/jsp/auth/register-content.jsp";

    public static final String SUPPLIES_CONTENT = "/WEB-INF/jsp/cart/supplies-content.jsp";
    public static final String SUPPLY_FORM_CONTENT = "/WEB-INF/jsp/cart/supply-form-content.jsp";

    public static final String CART_CONTENT = "/WEB-INF/jsp/cart/cart-content.jsp";
    public static final String ORDERS_CONTENT = "/WEB-INF/jsp/cart/orders-content.jsp";
    public static final String ORDER_FORM_CONTENT = "/WEB-INF/jsp/cart/order-form-content.jsp";

    public static final String ERROR_CONTENT = "/WEB-INF/jsp/common/error.jsp";

    private Jsp() {}
  }

  public static final class Path {
    public static final String HOME = "";
    public static final String ROOT = "/";
    public static final String LOGIN = "/auth/login";
    public static final String REGISTER = "/auth/register";
    public static final String LOGOUT = "/auth/logout";

    public static final String SUPPLIES = "/supplies/*";

    public static final String CART = "/cart";
    public static final String ORDERS = "/orders";
    public static final String NEW_ORDER = "/orders/new";

    public static final String LOGIN_REDIRECT = "/auth/login";
    public static final String SUPPLIES_REDIRECT = "/supplies";

    private Path() {}
  }

  private PageParameters() {}
}
