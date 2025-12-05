package by.zgirskaya.course.servlet.book;

import by.zgirskaya.course.command.Command;
import by.zgirskaya.course.command.CommandFactory;
import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;

@WebServlet(name = "BookServlet", urlPatterns = {"/books", "/books/*"})
public class BookServlet extends HttpServlet {
  private static final Logger logger = LogManager.getLogger();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createBookCommand(request);
      command.execute(request, response);
    } catch (ServiceException | DaoException | ParseException e) {
      logger.error("Error processing GET book request", e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      Command command = CommandFactory.createBookCommand(request);
      command.execute(request, response);
    } catch (ServiceException | DaoException | ParseException e) {
      logger.error("Error processing POST book request", e);
    }
  }
}