package mediator;

import model.Auction;

import java.sql.*;
import java.util.ArrayList;

public class AuctionDatabase implements AuctionPersistence
{
  private static AuctionDatabase instance;
  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=jdbc";
  private static final String USER = "postgres";
  private static final String PASSWORD = "344692StupidPass";
  private  AuctionDatabase() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }
  public static synchronized AuctionDatabase getInstance() throws SQLException
  {
    if(instance==null)
    {
      instance=new AuctionDatabase();
    }
    return instance;
  }

  private Connection getConnection() throws SQLException
  {
   return DriverManager.getConnection(URL, USER, PASSWORD);
  }
  @Override public synchronized Auction saveAuction(int ID, String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime,
      String imagePath) throws SQLException
  {
    try(Connection connection=getConnection())
    {
      String sql="INSERT INTO auction(ID, title, description, reserve_price, buyout_price, auction_time, minimum_bid_increment, current_bid, current_bidder, image_path, status) \n"
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
      PreparedStatement statement=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
      //statement.setInt(1, ID);
      statement.setString(2, title);
      statement.setString(3, description);
      statement.setInt(4, reservePrice);
      statement.setInt(5, buyoutPrice);
      statement.setInt(6, auctionTime);
      statement.setInt(7, minimumIncrement);
      statement.setString(8, null);
      statement.setString(9, null);
      statement.setString(10, imagePath);
      statement.setString(11, "ON SALE");

      statement.executeUpdate();
      ResultSet keys=statement.getGeneratedKeys();
      if(keys.next())
      {
        statement.setInt(1, keys.getInt(1));
        keys.close();
        statement.close();
        return new Auction(keys.getInt(1), title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, 0, null, imagePath, "ON SALE");
      }
      else
      {
        throw new SQLException("No keys generated");
      }
    }

  }
  @Override public Auction getAuctionById(int id) throws SQLException
  {
    try(Connection connection=getConnection())
    {
      String sql="SELECT ID, title, description, reserve_price, buyout_price, auction_time, minimum_bid_increment, current_bid, current_bidder, status\n"
          + "FROM sprint1database.auction\n" + "WHERE id='?';";
      PreparedStatement statement=connection.prepareStatement(sql);
      statement.setInt(1, id);
      ResultSet resultSet=statement.executeQuery();
      if(resultSet.next())
      {
        String title=resultSet.getString("title");
        String description=resultSet.getString("description");
        int reservePrice=resultSet.getInt("reserve_price");
        int buyoutPrice=resultSet.getInt("buyout_price");
        int minimumIncrement=resultSet.getInt("minimum_bid_increment");
        int auctionTime=resultSet.getInt("auction_time");
        int currentBid=resultSet.getInt("current_bid");
        String currentBidder=resultSet.getString("current_bidder");
        String imagePath=resultSet.getString("image_path");
        String status=resultSet.getString("status");
        resultSet.close();
        statement.close();
        return new Auction(id, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, currentBid, currentBidder, imagePath, status);
      }
      else
      {
        return null;
      }
    }
  }

  @Override public ArrayList<Auction> loadOngoingAuctions() throws SQLException
  {
    try(Connection connection=getConnection())
    {
      String sql = "SELECT auction.ID, auction.title, auction.description, auction.reserve_price, "
          + "auction.buyout_price, auction.auction_time, auction.current_bid, auction.current_bidder, auction.status"
          + "FROM sprint1database.auction"
          + "WHERE auction.status='ON SALE';";

    }
    catch(Exception e)
    {
      //e.printStackTrace();
    }
    return null;
  }
  @Override public ArrayList<Auction> loadClosedAuctions() throws SQLException
  {
    return null;
  }

  @Override public void removeAuction(Auction auction) throws SQLException
  {

  }

  @Override public void clear() throws SQLException
  {

  }
}
