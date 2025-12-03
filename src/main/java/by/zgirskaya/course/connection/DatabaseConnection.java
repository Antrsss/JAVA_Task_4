package by.zgirskaya.course.connection;

import by.zgirskaya.course.exception.ServiceException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
  private static final DataSource dataSource;

  static {
    try {
      Context initContext = new InitialContext();
      Context envContext = (Context) initContext.lookup("java:/comp/env");
      dataSource = (DataSource) envContext.lookup("jdbc/postgres");
    } catch (Exception e) {
      throw new ServiceException("Error initializing database connection", e);
    }
  }

  private DatabaseConnection() {}

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
