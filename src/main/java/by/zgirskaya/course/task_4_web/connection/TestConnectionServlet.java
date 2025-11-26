package by.zgirskaya.course.task_4_web.connection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/test-connection")
public class TestConnectionServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    try (Connection conn = DatabaseConnection.getConnection()) {
      out.println("<h1>Database connection successful!</h1>");
      out.println("<p>Database: " + conn.getMetaData().getDatabaseProductName() + "</p>");
      out.println("<p>Version: " + conn.getMetaData().getDatabaseProductVersion() + "</p>");
    } catch (SQLException e) {
      out.println("<h1>Database connection failed!</h1>");
      out.println("<p>Error: " + e.getMessage() + "</p>");
    }
  }
}