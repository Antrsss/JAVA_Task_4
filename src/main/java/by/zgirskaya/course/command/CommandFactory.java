package by.zgirskaya.course.command;

import by.zgirskaya.course.command.impl.supply.CreateSupplyCommand;
import by.zgirskaya.course.command.impl.supply.DeleteSupplyCommand;
import by.zgirskaya.course.command.impl.supply.ListSuppliesCommand;
import by.zgirskaya.course.command.impl.supply.ShowNewSupplyFormCommand;
import by.zgirskaya.course.util.AttributeParameters;
import jakarta.servlet.http.HttpServletRequest;

public class CommandFactory {
  private static final String CREATE_ACTION = "create";
  private static final String NEW_ACTION = "new";
  private static final String DELETE_PATH = "/delete/";
  private static final String POST_REQUEST = "POST";
  private static final String GET_REQUEST = "GET";

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
}