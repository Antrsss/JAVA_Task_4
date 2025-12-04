package by.zgirskaya.course.command.impl.supply;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Supply;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.SupplyService;
import by.zgirskaya.course.service.cart.impl.SupplyServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.AuthParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CreateSupplyCommand implements Command {
  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

  private final SupplyService supplyService = new SupplyServiceImpl();
  private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException, ParseException {

    HttpSession session = request.getSession(false);
    if (session == null) {
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return;
    }

    AbstractUserModel currentUser = (AbstractUserModel) session.getAttribute(AttributeParameters.USER);
    if (currentUser == null) {
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return;
    }

    String userRole = (String) session.getAttribute(AttributeParameters.USER_ROLE);
    if (!AuthParameters.Roles.EMPLOYEE.equals(userRole)) {
      request.setAttribute(AttributeParameters.ERROR, "Only employees can create supplies");
      request.getRequestDispatcher(PageParameters.Jsp.ERROR_CONTENT).forward(request, response);
      return;
    }

    UUID employeeId = currentUser.getId();
    UUID publisherId = UUID.fromString(request.getParameter("publisherId"));
    Date date = dateFormat.parse(request.getParameter("date"));
    double supplyPrice = Double.parseDouble(request.getParameter("supplyPrice"));

    Supply supply = new Supply(employeeId, publisherId, date, supplyPrice);
    supplyService.createSupply(supply);

    response.sendRedirect(request.getContextPath() + PageParameters.Path.SUPPLIES_REDIRECT);
  }
}