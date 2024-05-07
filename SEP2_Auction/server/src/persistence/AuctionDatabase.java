package persistence;

import model.Auction;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import model.AuctionList;
import model.Bid;
import utility.persistence.MyDatabase;

public class AuctionDatabase implements AuctionPersistence {
  private MyDatabase database;
  // link the database; to be changed as the database is expanding
  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=bidtable";
  private static final String USER = "postgres";

  // private static final String PASSWORD = "1706";
  // private static final String PASSWORD = "344692StupidPass";
  private static final String PASSWORD = "0000";

  public AuctionDatabase() throws SQLException, ClassNotFoundException {
    this.database = new MyDatabase(DRIVER, URL, USER, PASSWORD);
    Class.forName(DRIVER);
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  @Override
  public synchronized Auction saveAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException {
    try (Connection connection = getConnection()) {
      checkAuctionTime(auctionTime);
      String sql = "INSERT INTO auction(title, description, reserve_price, buyout_price, minimum_bid_increment, current_bid, current_bidder, image_data, status, start_time, end_time) \n"
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

      PreparedStatement statement = connection.prepareStatement(sql,
          PreparedStatement.RETURN_GENERATED_KEYS);

      statement.setString(1, checkTitle(title));
      statement.setString(2, checkDescription(description));
      statement.setInt(3, checkReservePrice(reservePrice));
      statement.setInt(4, checkBuyoutPrice(buyoutPrice, reservePrice));
      statement.setInt(5, checkMinimumIncrement(minimumIncrement));
      statement.setInt(6, checkCurrentBid(0));
      statement.setString(7, checkCurrentBidder("No bidder"));
      statement.setString(8, "temp_path");
      statement.setString(9, "ONGOING");

      LocalTime now = LocalTime.now();
      Time start = Time.valueOf(now);
      statement.setTime(10, start);
      Time end = Time.valueOf(now.plusHours(auctionTime));
      statement.setTime(11, end);

      statement.executeUpdate();
      ResultSet key = statement.getGeneratedKeys();

      if (key.next()) {
        int id = key.getInt("id");
        key.close();
        statement.close();

        sql = "UPDATE auction SET image_data = ? WHERE id = ?;";

        PreparedStatement updateStatement = connection.prepareStatement(sql);

        updateStatement.setString(1, saveImageToRepository(imageData, id + ""));
        updateStatement.setInt(2, id);

        updateStatement.executeUpdate();
        updateStatement.close();

        return new Auction(id, title, description, reservePrice, buyoutPrice,
            minimumIncrement, start, end, 0, "No bidder", imageData, "ONGOING");
      } else {
        throw new SQLException("No key generated");
      }
    }
  }

  @Override
  public synchronized Auction getAuctionById(int id)
      throws SQLException {

    try (Connection connection = getConnection()) {
      String sql = "SELECT *\n"
          + "FROM sprint1database.auction\n"
          + "WHERE id=?;";
      ArrayList<Object[]> results = database.query(sql, id);
      for (int i = 0; i < results.size(); i++) {
        try {
          Object[] row = results.get(i);
          String title = row[1].toString();
          String description = row[2].toString();
          int reservePrice = Integer.parseInt(row[3].toString());
          int buyoutPrice = Integer.parseInt(row[4].toString());
          int minimumIncrement = Integer.parseInt(row[5].toString());
          int currentBid = Integer.parseInt(row[6].toString());
          String currentBidder = row[7].toString();

          String imagePath = row[8].toString();
          byte[] imageData = downloadImageFromRepository(imagePath);

          String status = row[9].toString();
          Time auctionStart = Time.valueOf(row[10].toString());
          Time auctionEnd = Time.valueOf(row[11].toString());
          return new Auction(id, title, description, reservePrice, buyoutPrice,
              minimumIncrement, auctionStart, auctionEnd, currentBid,
              currentBidder, imageData, status);
        } catch (Exception e) {
          // maybe throw exception with "No auction with this id", but for testing:
          e.printStackTrace();
        }

      }
      return null;
      // without jar
      /*
       * PreparedStatement statement =
       * connection.prepareStatement(sql);statement.setInt(1, id);ResultSet resultSet
       * = statement.executeQuery();if (resultSet.next())
       * {String title = resultSet.getString("title");String description =
       * resultSet.getString("description");int reservePrice =
       * resultSet.getInt("reserve_price");int buyoutPrice =
       * resultSet.getInt("buyout_price");int minimumIncrement =
       * resultSet.getInt("minimum_bid_increment");int currentBid =
       * resultSet.getInt("current_bid");String currentBidder =
       * resultSet.getString("current_bidder");byte[] imageData =
       * resultSet.getBytes("image_data");String status =
       * resultSet.getString("status");Time auctionStart =
       * resultSet.getTime("start_time");Time auctionEnd =
       * resultSet.getTime("end_time");
       * resultSet.close();statement.close();return new Auction(id, title,
       * description, reservePrice, buyoutPrice,minimumIncrement, auctionStart,
       * auctionEnd, currentBid, currentBidder, imageData,status);}else{return null;}
       */
    }
  }

  private byte[] downloadImageFromRepository(String imagePath) {
    try {
      BufferedImage image = ImageIO.read(new File(imagePath));

      ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", outStreamObj);

      byte[] byteArray = outStreamObj.toByteArray();
      return byteArray;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String saveImageToRepository(byte[] imageBytes, String imageTitle) {
    String pathToImage = null;
    try {
      ByteArrayInputStream inStreamObj = new ByteArrayInputStream(imageBytes);
      BufferedImage newImage = ImageIO.read(inStreamObj);

      pathToImage = "server/images/" + imageTitle + ".jpg";
      ImageIO.write(newImage, "jpg", new File(pathToImage));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return pathToImage;
  }

  @Override
  public void markAsClosed(int id) throws SQLException {
    try (Connection connection = getConnection()) {
      String sql = "UPDATE auction SET status='CLOSED'\n" + "WHERE ID=?;";
      database.update(sql, id);
      /*
       * PreparedStatement statement =
       * connection.prepareStatement(sql);statement.setInt(1,
       * id);statement.executeUpdate();
       */
    }
  }

  @Override
  public AuctionList getOngoingAuctions() throws SQLException {
    String sql = "SELECT * FROM auction WHERE status='ONGOING';";
    ArrayList<Object[]> results = database.query(sql);
    AuctionList auctions = new AuctionList();
    for (int i = 0; i < results.size(); i++) {
      Object[] row = results.get(i);
      int id = Integer.parseInt(row[0].toString());
      String title = row[1].toString();
      String description = row[2].toString();
      int reservePrice = Integer.parseInt(row[3].toString());
      int buyoutPrice = Integer.parseInt(row[4].toString());
      int minimumIncrement = Integer.parseInt(row[5].toString());
      int currentBid = Integer.parseInt(row[6].toString());
      String currentBidder = row[7].toString();
      byte[] imageData = row[8].toString().getBytes();
      String status = row[9].toString();
      Time auctionStart = Time.valueOf(row[10].toString());
      Time auctionEnd = Time.valueOf(row[11].toString());
      auctions.addAuction(new Auction(id, title, description, reservePrice, buyoutPrice,
          minimumIncrement, auctionStart, auctionEnd, currentBid,
          currentBidder, imageData, status));
    }
    return auctions;
  }

  private int checkCurrentBid(int bid) {
    // Ensuring bid is non-negative
    if (bid < 0) {
      throw new IllegalArgumentException("Bid amount cannot be negative");
    }
    if (bid < checkCurrentBid(bid)){
      throw new IllegalArgumentException("Bid has to be higher than the current bid");
    }
    return bid;
  }

  private String checkCurrentBidder(String bidder) {
    // Ensuring bidder is not null or empty
    if (bidder == null || bidder.isEmpty()) {
      throw new IllegalArgumentException("Bidder cannot be null or empty");
    }
    return bidder;
  }

  private String checkTitle(String title) throws SQLException {
    int maxTitleLength = 80;
    int minTitleLength = 5;
    if (title.length() > maxTitleLength)
      throw new SQLException("The title is too long!");
    else if (title.length() < minTitleLength)
      throw new SQLException("The title is too short!");
    return title;
  }

  private String checkDescription(String description) throws SQLException {
    int maxDescriptionLength = 1400, minDescriptionLength = 20;
    if (description.length() > maxDescriptionLength)
      throw new SQLException("The description is too long!");
    else if (description.length() < minDescriptionLength)
      throw new SQLException("The description is too short!");
    return description;
  }

  private int checkReservePrice(int reservePrice) throws SQLException {
    if (reservePrice <= 0)
      throw new SQLException("The reserve price must be a positive number!");
    return reservePrice;
  }

  private int checkBuyoutPrice(int buyoutPrice, int reservePrice)
      throws SQLException {
    if (buyoutPrice <= reservePrice)
      throw new SQLException(
          "The buyout price must be greater than the reserve price!");
    return buyoutPrice;
  }

  private int checkMinimumIncrement(int minimumIncrement) throws SQLException {
    if (minimumIncrement < 1)
      throw new SQLException("The minimum bid increment must be at least 1!");
    return minimumIncrement;
  }

  private void checkAuctionTime(int auctionTime) throws SQLException {
    // to be updated when the moderator adds the time interval
    if (auctionTime <= 0 || auctionTime > 24 * 3600)
      throw new SQLException("The auction time can be at most 24 hours!");
  }

  @Override
  public Bid saveBid(int auctionId, String participantEmail, double bidAmount) throws SQLException {
    try (Connection connection = getConnection()) {
      String sql = "INSERT INTO Bid (participant_email, auction_id, bid_amount, bid_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
      PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, participantEmail);
      statement.setInt(2, auctionId);
      statement.setDouble(3, bidAmount);
      statement.executeUpdate();

      ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        int bidId = generatedKeys.getInt(1);
        Bid bid = new Bid(bidId, auctionId, participantEmail, bidAmount, null); // Timestamp will be null for now
        return bid;
      } else {
        throw new SQLException("Failed to save bid, no ID obtained.");
      }
    }
  }

  @Override
  public List<Bid> getBidsForAuction(int auctionId) throws SQLException {
    List<Bid> bids = new ArrayList<>();
    try (Connection connection = getConnection()) {
      String sql = "SELECT * FROM Bid WHERE auction_id = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, auctionId);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        int bidId = resultSet.getInt("bid_id");
        String participantEmail = resultSet.getString("participant_email");
        double bidAmount = resultSet.getDouble("bid_amount");
        Timestamp bidTime = resultSet.getTimestamp("bid_time");

        Bid bid = new Bid(bidId, auctionId, participantEmail, bidAmount, bidTime.toLocalDateTime());
        bids.add(bid);
      }
    }
    return bids;
  }


}
