package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.service.SupplyService;
import by.zgirskaya.course.service.impl.SupplyServiceImpl;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class DeleteSupplyCommand implements Command {
  private final SupplyService supplyService = new SupplyServiceImpl();

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServiceException {

    String pathInfo = request.getPathInfo();
    String supplyIdStr = pathInfo.substring(DELETE_PATH.length());

    UUID supplyId = UUID.fromString(supplyIdStr);
    supplyService.deleteSupply(supplyId);

    response.sendRedirect(request.getContextPath() + PageParameter.Path.SUPPLIES_REDIRECT);
  }
}