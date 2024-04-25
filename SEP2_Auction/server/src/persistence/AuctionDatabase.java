package persistence;

import model.Auction;

import java.sql.*;
import java.util.ArrayList;

public class AuctionDatabase implements AuctionPersistence
{
  private static AuctionDatabase instance;
  //link the database; to be changed as the database is expanding
  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=sprint1database";
  private static final String USER = "postgres";

  // private static final String PASSWORD = "1706"; - Maria's password
  private static final String PASSWORD = "344692StupidPass";

  private AuctionDatabase() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized AuctionDatabase getInstance() throws SQLException
  {
    if (instance == null)
    {
      instance = new AuctionDatabase();
    }
    return instance;
  }

  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }


//TODO: changes in logic
// save auction should take byte[] as parameter, convert it to .jpg image, and save it
// to images folder. Image !path! goes to DB
  @Override public synchronized Auction saveAuction(int ID, String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException
  {
    try (Connection connection = getConnection())
    {
      String sql =
          "INSERT INTO auction1(title, description, reserve_price, buyout_price, auction_time, minimum_bid_increment, current_bid, current_bidder, image_data, status) \n"
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
      PreparedStatement statement = connection.prepareStatement(sql,
          PreparedStatement.RETURN_GENERATED_KEYS);

      //google says it is a good idea to have the validation logic in both database and model
      statement.setString(1, checkTitle(title));
      statement.setString(2, checkDescription(description));
      statement.setInt(3, checkReservePrice(reservePrice));
      statement.setInt(4, checkBuyoutPrice(buyoutPrice, reservePrice));
      statement.setInt(5, checkAuctionTime(auctionTime));
      statement.setInt(6, checkMinimumIncrement(minimumIncrement));
      statement.setInt(7, checkCurrentBid(0));
      statement.setString(8, checkCurrentBidder(null));
      //TODO: new method
      // String saveImageToRepository(byte[] imageData) - converts bytes to .jpg file, returns path to an image

      //TODO: change method ro setString() - save only path in repository
      statement.setBytes(9, checkImageData(imageData));
      statement.setString(10, "ON SALE");

      statement.executeUpdate();
      ResultSet keys = statement.getGeneratedKeys();
      //we extract the generated id
      if (keys.next())
      {
        int id = keys.getInt("id");
        keys.close();
        statement.close();
        //and we create the auction with it
        return new Auction(id, title, description, reservePrice, buyoutPrice,
            minimumIncrement, auctionTime, 0, null, imageData, "ON SALE");
      }
      else
      {
        throw new SQLException("No keys generated");
      }
    }
  }

  @Override public synchronized Auction getAuctionById(int id)
      throws SQLException
  {
    try (Connection connection = getConnection())
    {
      String sql =
          "SELECT ID, title, description, reserve_price, buyout_price, auction_time, minimum_bid_increment, current_bid, current_bidder, image_data, status\n"
              + "FROM sprint1database.auction1\n"
              + "WHERE id=?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next())
      {
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        int reservePrice = resultSet.getInt("reserve_price");
        int buyoutPrice = resultSet.getInt("buyout_price");
        int minimumIncrement = resultSet.getInt("minimum_bid_increment");
        int auctionTime = resultSet.getInt("auction_time");
        int currentBid = resultSet.getInt("current_bid");
        String currentBidder = resultSet.getString("current_bidder");
        //TODO: new method, change SQL request
        // byte[] uploadImageFromRepository(String imagePath) - converts image from repository to bytes

        byte[] imageData = resultSet.getBytes("image_data");
        String status = resultSet.getString("status");
        resultSet.close();
        statement.close();
        return new Auction(id, title, description, reservePrice, buyoutPrice,
            minimumIncrement, auctionTime, currentBid, currentBidder, imageData,
            status);
      }
      else
      {
        return null;
      }
    }
  }

  @Override public void updateTime(int id, int seconds) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      String sql = "UPDATE auction1 SET auction_time=?\n" + "WHERE ID=?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, seconds);
      statement.setInt(2, id);
      statement.executeUpdate();
    }
  }
  @Override public void markAsClosed(int id) throws SQLException
  {
    try (Connection connection = getConnection())
    {
      String sql = "UPDATE auction1 SET status='CLOSED'\n" + "WHERE ID=?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, id);
      statement.executeUpdate();
    }
  }

  private byte[] checkImageData(byte[] imageData) throws SQLException
  {
    if (imageData == null)
      throw new SQLException("Please upload an image.");
    return imageData;
  }

  private int checkCurrentBid(int bid)
  {
    //logic for bid
    return bid;
  }

  private String checkCurrentBidder(String bidder)
  {
    //logic for bidder
    return bidder;
  }

  private String checkStatus(String status) throws SQLException
  {
    if (status.equals("ON SALE") || status.equals("CLOSED"))
      return status;
    throw new SQLException("Invalid status");
  }

  private String checkTitle(String title) throws SQLException
  {
    int maxTitleLength = 80;
    int minTitleLength = 5;
    if (title.length() > maxTitleLength)
      throw new SQLException("The title is too long!");
    else if (title.length() < minTitleLength)
      throw new SQLException("The title is too short!");
    return title;
  }

  private String checkDescription(String description) throws SQLException
  {
    int maxDescriptionLength = 1400, minDescriptionLength = 20;
    if (description.length() > maxDescriptionLength)
      throw new SQLException("The description is too long!");
    else if (description.length() < minDescriptionLength)
      throw new SQLException("The description is too short!");
    return description;
  }

  private int checkReservePrice(int reservePrice) throws SQLException
  {
    if (reservePrice <= 0)
      throw new SQLException("The reserve price must be a positive number!");
    return reservePrice;
  }

  private int checkBuyoutPrice(int buyoutPrice, int reservePrice)
      throws SQLException
  {
    if (buyoutPrice <= reservePrice)
      throw new SQLException(
          "The buyout price must be greater than the reserve price!");
    return buyoutPrice;
  }

  private int checkMinimumIncrement(int minimumIncrement) throws SQLException
  {
    if (minimumIncrement < 1)
      throw new SQLException("The minimum bid increment must be at least 1!");
    return minimumIncrement;
  }

  private int checkAuctionTime(int auctionTime) throws SQLException
  {
    //to be updated when the moderator adds the time interval
    if (auctionTime <= 0 || auctionTime > 24 * 3600)
      throw new SQLException("The auction time can be at most 24 hours!");

    /////////////////////////////////////////////////////////////////////////////////
    //correct line:
    return auctionTime;
    ////////////////////////////////////////////////////////////////////////////////
    //for testing purposes:
    //this.auctionTime=auctionTime/3600;
  }

}
