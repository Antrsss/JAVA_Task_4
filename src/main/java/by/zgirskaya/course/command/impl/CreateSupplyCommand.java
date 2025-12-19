package by.zgirskaya.course.command.impl;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.Item;
import by.zgirskaya.course.model.Supply;
import by.zgirskaya.course.model.AbstractUserModel;
import by.zgirskaya.course.service.ItemService;
import by.zgirskaya.course.service.SupplyService;
import by.zgirskaya.course.service.impl.ItemServiceImpl;
import by.zgirskaya.course.service.impl.SupplyServiceImpl;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.AuthParameter;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreateSupplyCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

  private final SupplyService supplyService = new SupplyServiceImpl();
  private final ItemService itemService = new ItemServiceImpl();
  private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException {

    Optional<AbstractUserModel> userOptional = getUserFromSession(request, response);
    if (userOptional.isEmpty()) {
      return;
    }
    AbstractUserModel currentUser = userOptional.get();
    HttpSession session = request.getSession();

    String userRole = (String) session.getAttribute(AttributeParameter.USER_ROLE);
    if (!AuthParameter.Roles.EMPLOYEE.equals(userRole)) {
      request.setAttribute(AttributeParameter.ERROR, "Only employees can create supplies");
      request.getRequestDispatcher(PageParameter.Jsp.ERROR_CONTENT).forward(request, response);
      return;
    }

    try {
      UUID employeeId = currentUser.getId();
      UUID publisherId = UUID.fromString(request.getParameter("publisherId"));
      Date date = dateFormat.parse(request.getParameter("date"));

      String[] bookIds = request.getParameterValues("bookIds[]");
      String[] quantities = request.getParameterValues("quantities[]");
      String[] prices = request.getParameterValues("prices[]");

      double totalSupplyPrice = calculateTotalSupplyPrice(quantities, prices);

      Supply supply = new Supply(employeeId, publisherId, date, totalSupplyPrice);
      Supply createdSupply = supplyService.createSupply(supply);

      logger.info("Supply created with ID: {}", createdSupply.getId());

      List<Item> createdItems = createSupplyItems(createdSupply.getId(), bookIds, quantities, prices);

      logger.info("Created {} items for supply ID: {}", createdItems.size(), createdSupply.getId());

      session.setAttribute(AttributeParameter.SUCCESS_MESSAGE,
          String.format("Supply created successfully with %d books", createdItems.size()));

      response.sendRedirect(request.getContextPath() + PageParameter.Path.SUPPLIES_REDIRECT);

    } catch (ParseException e) {
      logger.error("Invalid parameter format", e);
      request.setAttribute(AttributeParameter.ERROR, "Invalid parameter format: " + e.getMessage());
      request.getRequestDispatcher(PageParameter.Jsp.SUPPLY_FORM_CONTENT).forward(request, response);
    }
  }

  private double calculateTotalSupplyPrice(String[] quantities, String[] prices) {
    double total = 0.0;
    for (int i = 0; i < quantities.length; i++) {
      int quantity = Integer.parseInt(quantities[i]);
      double unitPrice = Double.parseDouble(prices[i]);
      total += quantity * unitPrice;
    }
    return total;
  }

  private List<Item> createSupplyItems(UUID supplyId, String[] bookIds, String[] quantities, String[] prices)
      throws ServiceException {

    List<Item> createdItems = new ArrayList<>();

    for (int i = 0; i < bookIds.length; i++) {
      UUID bookId = UUID.fromString(bookIds[i]);
      int quantity = Integer.parseInt(quantities[i]);
      double unitPrice = Double.parseDouble(prices[i]);

      Item item = itemService.addItemToCart(supplyId, bookId, quantity, unitPrice);
      createdItems.add(item);

      logger.debug("Item created: {} (Book: {}, Quantity: {}, Price: ${})",
          item.getId(), bookId, quantity, unitPrice);
    }

    return createdItems;
  }
}