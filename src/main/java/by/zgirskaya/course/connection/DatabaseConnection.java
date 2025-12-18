package by.zgirskaya.course.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
  private static final Logger logger = LogManager.getLogger();
  private static final DataSource dataSource;

  private static final String JNDI_ENVIRONMENT_CONTEXT = "java:/comp/env";
  private static final String JNDI_DATASOURCE_NAME = "jdbc/postgres";

  static {
    try {
      Context initContext = new InitialContext();
      Context envContext = (Context) initContext.lookup(JNDI_ENVIRONMENT_CONTEXT);
      dataSource = (DataSource) envContext.lookup(JNDI_DATASOURCE_NAME);
    } catch (NamingException e) {
      logger.fatal("Error while initializing DataSource", e);
      throw new ExceptionInInitializerError("Failed to initialize DataSource: " + e.getMessage());
    }
  }

  private DatabaseConnection() {}

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
