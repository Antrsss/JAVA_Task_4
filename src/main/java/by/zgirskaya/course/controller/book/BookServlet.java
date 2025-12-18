package by.zgirskaya.course.controller.book;

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

@WebServlet(PageParameter.Path.BOOKS)
public class BookServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
      ServletException, IOException {
    try {
      Command command = CommandFactory.createBookCommand(request);
      command.execute(request, response);
    } catch (ServiceException e) {
      logger.error("Error executing ListBooksCommand", e);
      request.setAttribute(AttributeParameter.ERROR_MESSAGE, "Unable to load book catalog. Please try again later.");
      request.getRequestDispatcher(PageParameter.Jsp.ERROR_CONTENT).forward(request, response);
    }
  }
}