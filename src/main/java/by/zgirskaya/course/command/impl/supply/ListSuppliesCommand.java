package by.zgirskaya.course.command.impl.supply;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Supply;
import by.zgirskaya.course.service.cart.SupplyService;
import by.zgirskaya.course.service.cart.impl.SupplyServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ListSuppliesCommand implements Command {
  private final SupplyService supplyService = new SupplyServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    List<Supply> supplies = supplyService.getAllSupplies();
    request.setAttribute(AttributeParameters.SUPPLIES, supplies);
    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.SUPPLIES_CONTENT);
    request.setAttribute(AttributeParameters.PAGE_TITLE, "Manage Supplies");
    request.getRequestDispatcher(PageParameters.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }
}
