package by.zgirskaya.course.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
  private static final Logger logger = LogManager.getLogger();
  private static final DataSource dataSource;

  private static final String ENV_CONTEXT_NAME = "java:/comp/env";
  private static final String JDBC_DRIVER = "jdbc/postgres";

  static {
    try {
      Context initContext = new InitialContext();
      Context envContext = (Context) initContext.lookup(ENV_CONTEXT_NAME);
      dataSource = (DataSource) envContext.lookup(JDBC_DRIVER);
    } catch (Exception e) {
      logger.fatal("Error while initializing DataSource", e);
      throw new ExceptionInInitializerError("Failed to initialize DataSource: " + e.getMessage());
    }
  }

  private DatabaseConnection() {}

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
