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

  public static final class Item {
    public static final String ID = "id";
    public static final String ORDER_ID = "order_id";
    public static final String BOOK_ID = "book_id";
    public static final String QUANTITY = "quantity";
    public static final String TOTAL_PRICE = "total_price";

    private Item() {}
  }

  public static final class Order {
    public static final String ID = "id";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String PURCHASE_DATE = "purchase_date";
    public static final String DELIVERY_DATE = "delivery_date";
    public static final String ORDER_PRICE = "order_price";

    private Order() {}
  }

  public static final class Supply {
    public static final String ID = "id";
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String PUBLISHER_ID = "publisher_id";
    public static final String DATE = "date";
    public static final String SUPPLY_PRICE = "supply_price";

    private Supply() {}
  }

  private TableColumns() {}
}
