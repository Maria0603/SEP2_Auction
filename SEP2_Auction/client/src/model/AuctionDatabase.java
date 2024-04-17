package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuctionDatabase implements AuctionPersistence
{
  //private MyDatabase db;
  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
  private static final String USER = "postgres";
  private static final String PASSWORD = "344692StupidPass";
  public AuctionDatabase()
  {
    //this.db=new MyDatabse(DRIVER, URL, USER, PASSWORD);
  }

  public static Connection connectDatabase()
  {
    try
    {
      Class.forName("org.postgresql.Driver");
      String url="jdbc:postgresql://localhost:5432/postgres";
      String user="postgres";
      String pw="344692StupidPass";
      return  DriverManager.getConnection(url, user, pw);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public Auction loadOngoing() throws SQLException
  {
    try
    {
      String sql = "SELECT auction.ID, auction.title, auction.description, auction.reserve_price, "
          + "auction.buyout_price, auction.auction_time, auction.current_bid, auction.current_bidder, auction.status"
          + "FROM sprint1database.auction"
          + "WHERE auction.status='ON SALE';";

      //PreparedStatement statement = connectDatabase().prepareStatement(sql);

    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  @Override public Auction loadClosed() throws SQLException
  {
    return null;
  }

  @Override public synchronized void save(Auction auction) throws SQLException
  {
    String sql="INSERT INTO auction (ID, title, description, reserve_price, buyout_price, auction_time, minimum_bid_increment, current_bid, current_bidder, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try
    {
      PreparedStatement statement=connectDatabase().prepareStatement(sql);
      /*
      statement.setString(1, auction.getID());
      statement.setString(2, auction.getTitle());
      statement.setString(3, auction.getDescription());
      statement.setString(4, auction.getReservePrice());
      statement.setString(5, auction.getBuyoutPrice());
      statement.setString(6, auction.getAuctionTime());
      statement.setString(7, auction.getMinimumBidIncrement());
      statement.setString(8, null);
      statement.setString(9, null);
      statement.setString(10, "ON SALE");
       */
      int updates=statement.executeUpdate();
      System.out.println("Number of updates: " + updates);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

  }

  @Override public void remove(Auction auction) throws SQLException
  {

  }

  @Override public void clear() throws SQLException
  {

  }
}
