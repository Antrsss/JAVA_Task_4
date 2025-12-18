package by.zgirskaya.course.command.impl.supply;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ShowNewSupplyFormCommand implements Command {
  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setAttribute(AttributeParameter.TITLE, "Create New Supply");
    request.setAttribute(AttributeParameter.ACTION, CREATE_ACTION);
    request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.SUPPLY_FORM_CONTENT);
    request.setAttribute(AttributeParameter.PAGE_TITLE, "New Supply");
    request.getRequestDispatcher(PageParameter.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }
}
