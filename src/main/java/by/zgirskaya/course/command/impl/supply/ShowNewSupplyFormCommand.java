package by.zgirskaya.course.command.impl.supply;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ShowNewSupplyFormCommand implements Command {
  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setAttribute(AttributeParameters.TITLE, "Create New Supply");
    request.setAttribute(AttributeParameters.ACTION, CREATE_ACTION);
    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.SUPPLY_FORM_CONTENT);
    request.setAttribute(AttributeParameters.PAGE_TITLE, "New Supply");
    request.getRequestDispatcher(PageParameters.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }
}
