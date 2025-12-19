package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Supply;
import by.zgirskaya.course.service.SupplyService;
import by.zgirskaya.course.service.impl.SupplyServiceImpl;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
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

    List<Supply> supplies = supplyService.findAllSupplies();

    request.setAttribute(AttributeParameter.SUPPLIES, supplies);
    request.setAttribute(AttributeParameter.CONTENT_PAGE, PageParameter.Jsp.SUPPLIES_CONTENT);
    request.setAttribute(AttributeParameter.PAGE_TITLE, "Manage Supplies");
    request.getRequestDispatcher(PageParameter.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }
}
