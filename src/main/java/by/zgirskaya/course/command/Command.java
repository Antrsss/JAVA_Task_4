package by.zgirskaya.course.command;

import by.zgirskaya.course.exception.DaoException;
import by.zgirskaya.course.exception.ServiceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;

public interface Command {
  String CREATE_ACTION = "create";
  String DELETE_PATH = "/delete/";

  void execute(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, ServiceException, DaoException, ParseException;
}