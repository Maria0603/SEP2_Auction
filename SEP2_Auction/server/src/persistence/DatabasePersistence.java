package persistence;

import utility.persistence.MyDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The DatabasePersistence class provides an abstract base for managing database connections and accessing the MyDatabase utility.
 */
public abstract class DatabasePersistence
{
  private MyDatabase database = new MyDatabase(DRIVER, URL, USER, PASSWORD);

  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=run5";
  private static final String USER = "postgres";

  // so the moderator just logs in, and then they can change their password
  private static final String MODERATOR_EMAIL = "bob@bidhub";
  private static final String MODERATOR_TEMPORARY_PASSWORD = "12345678";
  // private static final String PASSWORD = "1706";
   private static final String PASSWORD = "344692StupidPass";
  // private static final String PASSWORD = "2031";
  //private static final String PASSWORD = "0000";

  /**
   * Constructs a DatabasePersistence object and initializes the database driver.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the database driver class cannot be found.
   */
  public DatabasePersistence() throws SQLException, ClassNotFoundException
  {
    Class.forName(DRIVER);
  }

  /**
   * Retrieves a connection to the database.
   *
   * @return a Connection object for the database.
   * @throws SQLException if there is a database access error.
   */
  public Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  /**
   * Retrieves the MyDatabase utility instance.
   *
   * @return the MyDatabase utility instance.
   */
  public MyDatabase getDatabase()
  {
    return database;
  }

  /**
   * Retrieves the email address of the moderator.
   *
   * @return the email address of the moderator.
   */
  public String getModeratorEmail()
  {
    return MODERATOR_EMAIL;
  }
}
