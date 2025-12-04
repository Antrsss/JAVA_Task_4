package by.zgirskaya.course.servlet.cart;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Supply;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.SupplyService;
import by.zgirskaya.course.service.cart.impl.SupplyServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.AuthParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@WebServlet(PageParameters.Path.SUPPLIES)
public class SupplyServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();
  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

  private final SupplyService supplyService = new SupplyServiceImpl();
  private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String pathInfo = request.getPathInfo();
    String action = request.getParameter(AttributeParameters.ACTION);

    try {
      if (pathInfo != null && pathInfo.contains("/delete/")) {
        handleDeleteSupply(request, response);
      } else if ("new".equals(action)) {
        showNewSupplyForm(request, response);
      } else {
        listSupplies(request, response);
      }
    } catch (ServiceException | DaoException e) {
      logger.error("Error processing supply request", e);
      request.setAttribute(AttributeParameters.ERROR, "An error occurred: " + e.getMessage());
      request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.SUPPLIES_CONTENT);
      request.setAttribute(AttributeParameters.PAGE_TITLE, "Supplies - Error");
      request.getRequestDispatcher(PageParameters.Jsp.TEMPLATE_CONTENT).forward(request, response);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String action = request.getParameter(AttributeParameters.ACTION);

    try {
      if ("create".equals(action)) {
        createSupply(request, response);
      } else {
        listSupplies(request, response);
      }
    } catch (ServiceException | DaoException | ParseException e) {
      logger.error("Error processing supply form submission", e);
      request.setAttribute(AttributeParameters.ERROR, "An error occurred: " + e.getMessage());
      request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.SUPPLIES_CONTENT);
      request.setAttribute(AttributeParameters.PAGE_TITLE, "Supply Form - Error");
      request.getRequestDispatcher(PageParameters.Jsp.TEMPLATE_CONTENT).forward(request, response);
    }
  }

  private void listSupplies(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException, DaoException {

    List<Supply> supplies = supplyService.getAllSupplies();
    request.setAttribute(AttributeParameters.SUPPLIES, supplies);
    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.SUPPLIES_CONTENT);
    request.setAttribute(AttributeParameters.PAGE_TITLE, "Manage Supplies");
    request.getRequestDispatcher(PageParameters.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }

  private void showNewSupplyForm(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setAttribute(AttributeParameters.TITLE, "Create New Supply");
    request.setAttribute(AttributeParameters.ACTION, "create");
    request.setAttribute(AttributeParameters.CONTENT_PAGE, PageParameters.Jsp.SUPPLY_FORM_CONTENT);
    request.setAttribute(AttributeParameters.PAGE_TITLE, "New Supply");
    request.getRequestDispatcher(PageParameters.Jsp.TEMPLATE_CONTENT).forward(request, response);
  }

  private void createSupply(HttpServletRequest request, HttpServletResponse response)
      throws ServiceException, DaoException, IOException, ParseException {

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

    try {
      String userRole = (String) session.getAttribute(AttributeParameters.USER_ROLE);
      if (!AuthParameters.Roles.EMPLOYEE.equals(userRole)) {
        request.setAttribute(AttributeParameters.ERROR, "Only employees can create supplies");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
      }

      UUID employeeId = currentUser.getId();

      UUID publisherId = UUID.fromString(request.getParameter("publisherId"));
      Date date = dateFormat.parse(request.getParameter("date"));
      double supplyPrice = Double.parseDouble(request.getParameter("supplyPrice"));

      Supply supply = new Supply(employeeId, publisherId, date, supplyPrice);
      supplyService.createSupply(supply);

      response.sendRedirect(request.getContextPath() + PageParameters.Path.SUPPLIES_REDIRECT);
    } catch (ServletException e) {
      logger.error("Error processing POST request for path: {}", PageParameters.Path.SUPPLIES, e);
    }
  }

  private void handleDeleteSupply(HttpServletRequest request, HttpServletResponse response)
      throws ServiceException, DaoException, IOException {

    String pathInfo = request.getPathInfo();
    String supplyIdStr = pathInfo.substring("/delete/".length());

    UUID supplyId = UUID.fromString(supplyIdStr);
    supplyService.deleteSupply(supplyId);

    response.sendRedirect(request.getContextPath() + PageParameters.Path.SUPPLIES_REDIRECT);
  }
}