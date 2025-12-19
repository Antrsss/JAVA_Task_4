package by.zgirskaya.course.util;

public final class PageParameter {

  public static final class Title {
    public static final String HOME = "Home";
    public static final String LOGIN = "Login";
    public static final String REGISTER = "Register";
    public static final String BOOK_CATALOG = "Book Catalog";

    private Title() {}
  }

  public static final class Jsp {
    public static final String TEMPLATE_CONTENT = "/WEB-INF/jsp/common/template.jsp";

    public static final String HOME_CONTENT = "/WEB-INF/jsp/home-content.jsp";
    public static final String LOGIN_CONTENT = "/WEB-INF/jsp/auth/login-content.jsp";
    public static final String REGISTER_CONTENT = "/WEB-INF/jsp/auth/register-content.jsp";

    public static final String SUPPLIES_CONTENT = "/WEB-INF/jsp/cart/supplies-content.jsp";
    public static final String SUPPLY_FORM_CONTENT = "/WEB-INF/jsp/cart/supply-form-content.jsp";

    public static final String ORDERS_CONTENT = "/WEB-INF/jsp/cart/orders-content.jsp";
    public static final String ORDER_DETAILS_CONTENT = "/WEB-INF/jsp/cart/order-details.jsp";

    public static final String CART_CONTENT = "/WEB-INF/jsp/cart/cart-content.jsp";

    public static final String BOOK_LIST_CONTENT = "/WEB-INF/jsp/book/list.jsp";
    public static final String BOOK_DETAILS_CONTENT = "/WEB-INF/jsp/book/view.jsp";

    public static final String ERROR_CONTENT = "/WEB-INF/jsp/common/error.jsp";

    private Jsp() {}
  }

  public static final class Path {
    public static final String HOME = "/controller/home";
    public static final String ROOT = "/controller";
    public static final String LOGIN = "/controller/auth/login";
    public static final String REGISTER = "/controller/auth/register";
    public static final String LOGOUT = "/controller/auth/logout";

    public static final String SUPPLIES = "/controller/supplies/";
    public static final String SUPPLIES_DELETE = "/controller/supplies/delete/";

    public static final String BOOKS = "/controller/books/";
    public static final String BOOKS_VIEW = "/controller/books/view";
    public static final String CART = "/controller/cart/";

    public static final String LOGIN_REDIRECT = "/controller/auth/login";
    public static final String SUPPLIES_REDIRECT = "/controller/supplies";
    public static final String BOOKS_REDIRECT = "/controller/books";
    public static final String CART_REDIRECT = "/controller/cart";
    public static final String ORDERS = "/controller/orders";
    public static final String ORDERS_VIEW = "/controller/orders/view";
    public static final String ORDER_ID_REDIRECT = "?orderId=";
    public static final String ORDER_CONFIRMATION_REDIRECT = "/controller/order/confirmation";
    public static final String ORDER_CONFIRMATION = "/controller/order/confirmation";

    private Path() {}
  }

  private PageParameter() {}
}
