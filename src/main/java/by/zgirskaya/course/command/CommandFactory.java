package by.zgirskaya.course.command;

import by.zgirskaya.course.command.impl.auth.LoginCommand;
import by.zgirskaya.course.command.impl.auth.LogoutCommand;
import by.zgirskaya.course.command.impl.auth.RegisterCommand;
import by.zgirskaya.course.command.impl.book.ListBooksCommand;
import by.zgirskaya.course.command.impl.book.ViewBookCommand;
import by.zgirskaya.course.command.impl.cart.AddToCartCommand;
import by.zgirskaya.course.command.impl.cart.CheckoutCommand;
import by.zgirskaya.course.command.impl.cart.RemoveFromCartCommand;
import by.zgirskaya.course.command.impl.cart.ViewCartCommand;
import by.zgirskaya.course.command.impl.order.ViewOrderDetailsCommand;
import by.zgirskaya.course.command.impl.order.ViewOrdersCommand;
import by.zgirskaya.course.command.impl.supply.CreateSupplyCommand;
import by.zgirskaya.course.command.impl.supply.DeleteSupplyCommand;
import by.zgirskaya.course.command.impl.supply.ListSuppliesCommand;
import by.zgirskaya.course.command.impl.supply.ShowNewSupplyFormCommand;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.http.HttpServletRequest;

public class CommandFactory {
  private static final String CREATE_ACTION = "create";
  private static final String NEW_ACTION = "new";
  private static final String DETAILS_ACTION = "details";

  private static final String ADD_TO_CART_ACTION = "addToCart";
  private static final String REMOVE_FROM_CART_ACTION = "removeFromCart";
  private static final String CHECKOUT_ACTION = "checkout";

  private static final String POST_REQUEST = "POST";
  private static final String GET_REQUEST = "GET";

  private static final String DELETE_PATH = "/delete/";
  private static final String VIEW_PATH = "/view/";

  private CommandFactory() {}

  public static Command createAuthCommand(HttpServletRequest request) {
    String servletPath = request.getServletPath();

    if (PageParameters.Path.LOGIN.equals(servletPath)) {
      return new LoginCommand();
    } else if (PageParameters.Path.LOGOUT.equals(servletPath)) {
      return new LogoutCommand();
    } else if (PageParameters.Path.REGISTER.equals(servletPath)) {
      return new RegisterCommand();
    }

    throw new IllegalArgumentException("Unknown authentication path: " + servletPath);
  }

  public static Command createSupplyCommand(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    String action = request.getParameter(AttributeParameters.ACTION);

    if (pathInfo != null && pathInfo.contains(DELETE_PATH)) {
      return new DeleteSupplyCommand();
    }

    if (GET_REQUEST.equalsIgnoreCase(request.getMethod())) {
      if (NEW_ACTION.equals(action)) {
        return new ShowNewSupplyFormCommand();
      } else {
        return new ListSuppliesCommand();
      }
    }

    if (POST_REQUEST.equalsIgnoreCase(request.getMethod())) {
      if (CREATE_ACTION.equals(action)) {
        return new CreateSupplyCommand();
      } else {
        return new ListSuppliesCommand();
      }
    }

    return new ListSuppliesCommand();
  }

  public static Command createBookCommand(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();

    if (pathInfo != null && pathInfo.contains(VIEW_PATH)) {
      return new ViewBookCommand();
    }

    return new ListBooksCommand();
  }

  public static Command createCartCommand(HttpServletRequest request) {
    String action = request.getParameter(AttributeParameters.ACTION);

    if (ADD_TO_CART_ACTION.equals(action)) {
      return new AddToCartCommand();
    } else if (REMOVE_FROM_CART_ACTION.equals(action)) {
      return new RemoveFromCartCommand();
    } else if (CHECKOUT_ACTION.equals(action)) {
      return new CheckoutCommand();
    }

    return new ViewCartCommand();
  }

  public static Command createOrderCommand(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    String action = request.getParameter(AttributeParameters.ACTION);

    if (pathInfo != null && pathInfo.startsWith("/view/")) {
      return new ViewOrderDetailsCommand();
    }

    if (DETAILS_ACTION.equals(action)) {
      return new ViewOrderDetailsCommand();
    }

    return new ViewOrdersCommand();
  }
}