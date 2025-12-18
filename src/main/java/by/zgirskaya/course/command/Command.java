package by.zgirskaya.course.command;

import by.zgirskaya.course.exception.ServiceException;
import by.zgirskaya.course.model.user.AbstractUserModel;
import by.zgirskaya.course.util.AttributeParameters;
import by.zgirskaya.course.util.PageParameters;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface Command {
  String CREATE_ACTION = "create";
  String DELETE_PATH = "/delete/";
  String VIEW_PATH = "/view/";

  void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServiceException, IOException, ServletException;

  default UUID getCustomerIdFromSession(HttpSession session, AbstractUserModel user) {
    Object customerIdObj = session.getAttribute(AttributeParameters.CUSTOMER_ID);

    if (customerIdObj != null) {
      if (customerIdObj instanceof UUID uuid) {
        return uuid;
      } else if (customerIdObj instanceof String string) {
        return UUID.fromString(string);
      }
    }

    return user.getId();
  }

  default Optional<AbstractUserModel> getUserFromSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);
    if (session == null) {
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return Optional.empty();
    }

    AbstractUserModel currentUser = (AbstractUserModel) session.getAttribute(AttributeParameters.USER);
    if (currentUser == null) {
      response.sendRedirect(request.getContextPath() + PageParameters.Path.LOGIN_REDIRECT);
      return Optional.empty();
    }

    return Optional.of(currentUser);
  }
}