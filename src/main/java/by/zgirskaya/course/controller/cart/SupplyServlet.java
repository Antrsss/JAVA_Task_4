package by.zgirskaya.course.controller.cart;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.command.CommandFactory;
import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.util.AttributeParameter;
import by.zgirskaya.course.util.PageParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(PageParameter.Path.SUPPLIES)
public class SupplyServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createSupplyCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Invalid parameter format", e);
      request.setAttribute(AttributeParameter.ERROR, "Invalid parameter format: " + e.getMessage());
      request.getRequestDispatcher(PageParameter.Jsp.SUPPLY_FORM_CONTENT).forward(request, response);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createSupplyCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Invalid parameter format", e);
      request.setAttribute(AttributeParameter.ERROR, "Invalid parameter format: " + e.getMessage());
      request.getRequestDispatcher(PageParameter.Jsp.SUPPLY_FORM_CONTENT).forward(request, response);
    }
  }
}