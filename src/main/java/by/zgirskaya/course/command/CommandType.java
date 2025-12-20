package by.zgirskaya.course.command;

import by.zgirskaya.course.command.impl.*;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {
  LOGIN(PageParameter.Path.LOGIN, new LoginCommand()),
  LOGOUT(PageParameter.Path.LOGOUT, new LogoutCommand()),
  REGISTER(PageParameter.Path.REGISTER, new RegisterCommand()),

  HOME(PageParameter.Path.HOME, new HomeCommand()),
  ROOT(PageParameter.Path.ROOT, new HomeCommand()),

  BOOKS(PageParameter.Path.BOOKS, new ListBooksCommand()),
  BOOK_VIEW(PageParameter.Path.BOOKS_VIEW, new ViewBookCommand()),

  CART(PageParameter.Path.CART, new ViewCartCommand()),
  ORDERS(PageParameter.Path.ORDERS, new ViewOrdersCommand()),
  ORDER_VIEW(PageParameter.Path.ORDERS_VIEW, new ViewOrderDetailsCommand()),
  ORDER_CONFIRMATION(PageParameter.Path.ORDER_CONFIRMATION, new ViewOrderDetailsCommand()),

  SUPPLIES(PageParameter.Path.SUPPLIES, new ListSuppliesCommand()),
  SUPPLY_DELETE(PageParameter.Path.SUPPLIES_DELETE, new DeleteSupplyCommand());

  private static final Map<String, CommandType> pathMap = new HashMap<>();

  private final String path;
  private final Command command;

  static {
    for (CommandType type : values()) {
      pathMap.put(type.path, type);
    }
  }

  CommandType(String path, Command command) {
    this.path = path;
    this.command = command;
  }

  public Command getCommand() {
    return command;
  }

  public static Command createCommand(HttpServletRequest request, String path) {
    if (path.endsWith("/") && path.length() > 1) {
      path = path.substring(0, path.length() - 1);
    }

    CommandType commandType = pathMap.get(path);

    if (commandType != null) {
      return commandType.getCommand();
    }

    if (path.startsWith(PageParameter.Path.BOOKS_VIEW)) {
      return new ViewBookCommand();
    }

    if (path.startsWith(PageParameter.Path.ORDERS_VIEW)) {
      return new ViewOrderDetailsCommand();
    }

    if (path.startsWith(PageParameter.Path.SUPPLIES_DELETE)) {
      return new DeleteSupplyCommand();
    }

    String action = request.getParameter("action");

    if (path.startsWith(PageParameter.Path.CART) && action != null) {
      switch (action) {
        case "addToCart":
          return new AddToCartCommand();
        case "removeFromCart":
          return new RemoveFromCartCommand();
        case "checkout":
          return new CheckoutCommand();
      }
    }

    if (PageParameter.Path.SUPPLIES.equals(path) && action != null) {
      switch (action) {
        case "new":
          return new ShowNewSupplyFormCommand();
        case "create":
          return new CreateSupplyCommand();
      }
    }

    if (path.startsWith(PageParameter.Path.LOGIN)) {
      return new LoginCommand();
    }

    return new HomeCommand();
  }
}