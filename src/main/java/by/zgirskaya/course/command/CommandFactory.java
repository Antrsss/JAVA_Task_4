package by.zgirskaya.course.command;

import by.zgirskaya.course.command.impl.order.*;
import by.zgirskaya.course.command.impl.supply.CreateSupplyCommand;
import by.zgirskaya.course.command.impl.supply.DeleteSupplyCommand;
import by.zgirskaya.course.command.impl.supply.ListSuppliesCommand;
import by.zgirskaya.course.command.impl.supply.ShowNewSupplyFormCommand;
import by.zgirskaya.course.util.AttributeParameters;
import jakarta.servlet.http.HttpServletRequest;

public class CommandFactory {
  private static final String CREATE_ACTION = "create";
  private static final String NEW_ACTION = "new";

  private static final String ADD_TO_CART_ACTION = "addToCart";
  private static final String REMOVE_FROM_CART_ACTION = "removeFromCart";
  private static final String CHECKOUT_ACTION = "checkout";
  private static final String CLEAR_CART_ACTION = "clearCart";

  private static final String POST_REQUEST = "POST";
  private static final String GET_REQUEST = "GET";
  private static final String DELETE_REQUEST = "DELETE";

  private static final String DELETE_PATH = "/delete/";
  private static final String NEW_PATH = "/new";

  private CommandFactory() {}

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

  public static Command createOrderCommand(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    String action = request.getParameter(AttributeParameters.ACTION);
    String method = request.getMethod();

    // Обработка GET запросов
    if (GET_REQUEST.equalsIgnoreCase(method)) {
      if (pathInfo != null) {
        if (pathInfo.contains("/cart")) {
          return new ViewCartCommand();
        } else if (pathInfo.contains(NEW_PATH)) {
          // Можно добавить команду для формы создания нового заказа
          // return new ShowNewOrderFormCommand();
          return new ViewCartCommand(); // временно
        } else {
          return new ViewOrdersCommand();
        }
      } else {
        return new ViewOrdersCommand();
      }
    }

    if (POST_REQUEST.equalsIgnoreCase(method)) {
      if (ADD_TO_CART_ACTION.equals(action)) {
        return new AddToOrderCommand();
      } else if (REMOVE_FROM_CART_ACTION.equals(action)) {
        return new RemoveFromOrderCommand();
      } else if (CHECKOUT_ACTION.equals(action)) {
        return new CheckoutOrderCommand();
      } else if (CLEAR_CART_ACTION.equals(action)) {
        return new ClearOrderCommand();
      } else if (CREATE_ACTION.equals(action)) {
        return new CreateOrderCommand();
      }
    }

    if (DELETE_REQUEST.equalsIgnoreCase(method)) {
      return new RemoveFromOrderCommand();
    }

    return new ViewCartCommand();
  }
}