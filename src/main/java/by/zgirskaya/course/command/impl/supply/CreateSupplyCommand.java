package by.zgirskaya.course.command.impl.supply;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.cart.Item;
import by.zgirskaya.course.model.cart.Supply;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.service.cart.ItemService;
import by.zgirskaya.course.service.cart.SupplyService;
import by.zgirskaya.course.service.cart.impl.ItemServiceImpl;
import by.zgirskaya.course.service.cart.impl.SupplyServiceImpl;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.AuthParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateSupplyCommand implements Command {
  private static final Logger logger = LogManager.getLogger();
  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

  private final SupplyService supplyService = new SupplyServiceImpl();
  private final ItemService itemService = new ItemServiceImpl();
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

    try {
      UUID employeeId = currentUser.getId();
      UUID publisherId = UUID.fromString(request.getParameter("publisherId"));
      Date date = dateFormat.parse(request.getParameter("date"));

      String[] bookIds = request.getParameterValues("bookIds[]");
      String[] quantities = request.getParameterValues("quantities[]");
      String[] prices = request.getParameterValues("prices[]");

      if (bookIds == null || quantities == null || prices == null ||
          bookIds.length != quantities.length || bookIds.length != prices.length) {
        throw new ServiceException("Invalid book data provided");
      }

      double totalSupplyPrice = calculateTotalSupplyPrice(quantities, prices);

      Supply supply = new Supply(employeeId, publisherId, date, totalSupplyPrice);
      Supply createdSupply = supplyService.createSupply(supply);

      logger.info("Supply created with ID: {}", createdSupply.getId());

      List<Item> createdItems = createSupplyItems(createdSupply.getId(), bookIds, quantities, prices);

      logger.info("Created {} items for supply ID: {}", createdItems.size(), createdSupply.getId());

      session.setAttribute("successMessage",
          String.format("Supply created successfully with %d books", createdItems.size()));

      response.sendRedirect(request.getContextPath() + PageParameters.Path.SUPPLIES_REDIRECT);

    } catch (IllegalArgumentException e) {
      logger.error("Invalid parameter format", e);
      request.setAttribute(AttributeParameters.ERROR, "Invalid parameter format: " + e.getMessage());
      request.getRequestDispatcher(PageParameters.Jsp.SUPPLY_FORM_CONTENT).forward(request, response);
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
      try {
        UUID bookId = UUID.fromString(bookIds[i]);
        int quantity = Integer.parseInt(quantities[i]);
        double unitPrice = Double.parseDouble(prices[i]);

        if (quantity <= 0) {
          throw new ServiceException("Quantity must be positive for book at position " + (i + 1));
        }

        if (unitPrice <= 0) {
          throw new ServiceException("Price must be positive for book at position " + (i + 1));
        }

        Item item = itemService.addItemToOrder(supplyId, bookId, quantity, unitPrice);
        createdItems.add(item);

        logger.debug("Item created: {} (Book: {}, Quantity: {}, Price: ${})",
            item.getId(), bookId, quantity, unitPrice);

      } catch (NumberFormatException e) {
        throw new ServiceException("Invalid number format for book at position " + (i + 1));
      }
    }

    return createdItems;
  }
}