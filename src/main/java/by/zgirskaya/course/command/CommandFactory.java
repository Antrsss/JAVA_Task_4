package by.zgirskaya.course.command;

import by.zgirskaya.course.command.impl.supply.CreateSupplyCommand;
import by.zgirskaya.course.command.impl.supply.DeleteSupplyCommand;
import by.zgirskaya.course.command.impl.supply.ListSuppliesCommand;
import by.zgirskaya.course.command.impl.supply.ShowNewSupplyFormCommand;
import by.zgirskaya.course.util.AttributeParameters;
import jakarta.servlet.http.HttpServletRequest;

public class CommandFactory {
  public static Command createSupplyCommand(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    String action = request.getParameter(AttributeParameters.ACTION);

    if (pathInfo != null && pathInfo.contains("/delete/")) {
      return new DeleteSupplyCommand();
    }

    if ("GET".equalsIgnoreCase(request.getMethod())) {
      if ("new".equals(action)) {
        return new ShowNewSupplyFormCommand();
      } else {
        return new ListSuppliesCommand();
      }
    }

    if ("POST".equalsIgnoreCase(request.getMethod())) {
      if ("create".equals(action)) {
        return new CreateSupplyCommand();
      } else {
        return new ListSuppliesCommand();
      }
    }

    return new ListSuppliesCommand();
  }
}