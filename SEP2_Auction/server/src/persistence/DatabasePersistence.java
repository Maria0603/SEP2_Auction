package persistence;

import utility.persistence.MyDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabasePersistence
{
  private MyDatabase database=new MyDatabase(DRIVER, URL, USER, PASSWORD);
  // link the database; to be changed as the database is expanding
  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=sprint1database";
  private static final String USER = "postgres";

  // so the moderator just logs in, and then they can change their password
  private static final String MODERATOR_EMAIL = "bob@bidhub";
  private static final String MODERATOR_TEMPORARY_PASSWORD = "1234";
  //private static final String PASSWORD = "1706";
  private static final String PASSWORD = "344692StupidPass";
  // private static final String PASSWORD = "2031";

  public DatabasePersistence() throws SQLException, ClassNotFoundException
  {
      Class.forName(DRIVER);
  }

  public Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
  public MyDatabase getDatabase()
  {
    return database;
  }
  public String getModeratorEmail()
  {
    return MODERATOR_EMAIL;
  }
}
