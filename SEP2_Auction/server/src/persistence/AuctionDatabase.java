package persistence;

import model.domain.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * This class handles the database operations for auctions.
 */
public class AuctionDatabase extends DatabasePersistence implements AuctionPersistence {

  private static final String database = "mathias";

  /**
   * Constructs an AuctionDatabase object and initializes the database schema.
   *
   * @throws SQLException if a database access error occurs
   * @throws ClassNotFoundException if the database driver class is not found
   */
  public AuctionDatabase() throws SQLException, ClassNotFoundException {
    String sqlCreateSchema = "CREATE SCHEMA IF NOT EXISTS " + database + ";";
    super.getDatabase().update(sqlCreateSchema);
    String sqlCreateDomain1 = "CREATE DOMAIN email VARCHAR(200);";
    String sqlCreateDomain2 = "CREATE DOMAIN first_last_name VARCHAR(100);";
    super.getDatabase().update(sqlCreateDomain1);
    super.getDatabase().update(sqlCreateDomain2);
    createTables();
    insertModerator();
  }

  /**
   * Saves an auction to the database.
   *
   * @param title the title of the auction
   * @param description the description of the auction
   * @param reservePrice the reserve price of the auction
   * @param buyoutPrice the buyout price of the auction
   * @param minimumIncrement the minimum bid increment
   * @param auctionTime the duration of the auction in hours
   * @param imageData the image data of the auction item
   * @param seller the email of the seller
   * @return the saved Auction object
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized Auction saveAuction(String title, String description, int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller) throws SQLException {
    try (Connection connection = getConnection()) {
      String sql = "INSERT INTO auction(title, description, reserve_price, buyout_price, minimum_bid_increment, current_bid, current_bidder, image_data, status, start_time, end_time, creator_email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
      PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
      statement.setString(1, title);
      statement.setString(2, description);
      statement.setInt(3, reservePrice);
      statement.setInt(4, buyoutPrice);
      statement.setInt(5, minimumIncrement);
      statement.setInt(6, 0);
      statement.setString(7, null);
      statement.setString(8, "temp_path");
      statement.setString(9, "ONGOING");

      LocalTime now = LocalTime.now();
      Time start = Time.valueOf(now);
      statement.setTime(10, start);
      Time end = Time.valueOf(now.plusHours(auctionTime));
      statement.setTime(11, end);
      statement.setString(12, seller);

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

        return new Auction(id, title, description, reservePrice, buyoutPrice, minimumIncrement, start, end, 0, "No bidder", seller, imageData, "ONGOING");
      } else {
        throw new SQLException("No key generated");
      }
    }
  }

  /**
   * Retrieves an auction by its ID.
   *
   * @param id the ID of the auction
   * @return the Auction object, or null if not found
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized Auction getAuctionById(int id) throws SQLException {
    String sql = "SELECT * FROM auction WHERE id=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, id);
    for (Object[] row : results) {
      try {
        String title = row[1].toString();
        String description = row[2].toString();
        int reservePrice = Integer.parseInt(row[3].toString());
        int buyoutPrice = Integer.parseInt(row[4].toString());
        int minimumIncrement = Integer.parseInt(row[5].toString());
        int currentBid = Integer.parseInt(row[6].toString());
        String currentBidder = row[7] != null ? row[7].toString() : null;
        String imagePath = row[8].toString();
        byte[] imageData = downloadImageFromRepository(imagePath);
        String status = row[9].toString();
        Time auctionStart = Time.valueOf(row[10].toString());
        Time auctionEnd = Time.valueOf(row[11].toString());
        String seller = row[12].toString();
        return new Auction(id, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionStart, auctionEnd, currentBid, currentBidder, seller, imageData, status);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Saves a notification to the database.
   *
   * @param content the content of the notification
   * @param receiver the email of the receiver
   * @return the saved Notification object
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized Notification saveNotification(String content, String receiver) throws SQLException {
    String sql = "INSERT INTO notification(receiver, content, date, time) VALUES (?, ?, ?, ?);";
    Date date = Date.valueOf(LocalDate.now());
    Time time = Time.valueOf(LocalTime.now());
    super.getDatabase().update(sql, receiver, content, date, time);
    return new Notification(date + " " + time, content, receiver);
  }

  /**
   * Saves a bid to the database.
   *
   * @param participantEmail the email of the participant
   * @param bidAmount the amount of the bid
   * @param auctionId the ID of the auction
   * @return the saved Bid object
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized Bid saveBid(String participantEmail, int bidAmount, int auctionId) throws SQLException {
    String sql = "INSERT INTO bid (participant_email, auction_id, bid_amount) VALUES (?, ?, ?)";
    super.getDatabase().update(sql, participantEmail, auctionId, bidAmount);
    Bid bid = new Bid(auctionId, participantEmail, bidAmount);
    updateCurrentBid(bid);
    return bid;
  }

  /**
   * Marks an auction as closed.
   *
   * @param id the ID of the auction
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized void markAsClosed(int id) throws SQLException {
    String sql = "UPDATE auction SET status='CLOSED' WHERE ID=?;";
    super.getDatabase().update(sql, id);
  }

  /**
   * Retrieves the current bid for a specific auction.
   *
   * @param auctionId the ID of the auction
   * @return the current Bid object, or null if no bid exists
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized Bid getCurrentBidForAuction(int auctionId) throws SQLException {
    String sql = "SELECT current_bid, current_bidder FROM auction WHERE ID=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, auctionId);
    for (Object[] row : results) {
      if (row[1] != null) {
        int currentBid = Integer.parseInt(row[0].toString());
        String currentBidder = row[1].toString();
        return new Bid(auctionId, currentBidder, currentBid);
      }
    }
    return null;
  }

  /**
   * Retrieves user information by email.
   *
   * @param email the email of the user
   * @return the User object, or null if not found
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized User getUserInfo(String email) throws SQLException {
    User user = getUser(email);
    if (user != null) {
      if (email.equals(super.getModeratorEmail())) {
        return new User(user.getFirstname(), user.getLastname(), getModeratorSpecificInfo(), null, user.getPhone(), null);
      } else {
        Date birthday = getParticipantInfo(email);
        return new User(user.getFirstname(), user.getLastname(), email, null, user.getPhone(), birthday.toLocalDate());
      }
    }
    return null;
  }

  /**
   * Deletes an auction from the database.
   *
   * @param moderatorEmail the email of the moderator
   * @param auctionId the ID of the auction
   * @param reason the reason for deletion
   * @throws SQLException if a database access error occurs
   */
  @Override
  public synchronized void deleteAuction(String moderatorEmail, int auctionId, String reason) throws SQLException {
    String sql = "DELETE FROM auction WHERE ID=?;";
    super.getDatabase().update(sql, auctionId);
  }

  /**
   * Processes a buyout for an auction.
   *
   * @param bidder the email of the bidder
   * @param auctionId the ID of the auction
   * @return the Bid object representing the buyout
   * @throws SQLException if a database access error occurs or if the auction has bids
   */
  @Override
  public synchronized Bid buyout(String bidder, int auctionId) throws SQLException {
    Auction auction = getAuctionById(auctionId);
    if (auction != null) {
      if (auction.getCurrentBid() != 0) throw new SQLException("No buyout option. This auction has bids.");
      Bid bid = saveBid(bidder, auction.getPriceConstraint().getBuyoutPrice(), auctionId);
      String sql = "UPDATE auction SET status='CLOSED' WHERE ID=?;";
      super.getDatabase().update(sql, auctionId);
      return bid;
    } else {
      throw new SQLException("Auction does not exist or has already ended.");
    }
  }

  /**
   * Updates the current bid for an auction.
   *
   * @param currentBid the Bid object representing the current bid
   * @throws SQLException if a database access error occurs
   */
  private synchronized void updateCurrentBid(Bid currentBid) throws SQLException {
    String sql = "UPDATE auction SET current_bid=?, current_bidder=? WHERE auction.ID=?;";
    super.getDatabase().update(sql, currentBid.getBidAmount(), currentBid.getBidder(), currentBid.getAuctionId());
  }

  /**
   * Retrieves the birth date of a participant.
   *
   * @param email the email of the participant
   * @return the birth date as a Date object
   * @throws SQLException if a database access error occurs
   */
  private synchronized Date getParticipantInfo(String email) throws SQLException {
    String sql = "SELECT participant.birth_date FROM participant WHERE user_email=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (Object[] row : results) {
      return Date.valueOf(row[0].toString());
    }
    return null;
  }

  /**
   * Retrieves specific information about a moderator.
   *
   * @return the personal email of the moderator
   * @throws SQLException if a database access error occurs
   */
  private synchronized String getModeratorSpecificInfo() throws SQLException {
    String sql = "SELECT personal_email FROM moderator WHERE moderator_email=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, super.getModeratorEmail());
    for (Object[] row : results) {
      if (row[0] != null) return row[0].toString();
    }
    return null;
  }

  /**
   * Retrieves user information by email.
   *
   * @param email the email of the user
   * @return the User object, or null if not found
   * @throws SQLException if a database access error occurs
   */
  private synchronized User getUser(String email) throws SQLException {
    String sql = "SELECT phone_number, first_name, last_name FROM \"user\" WHERE user_email=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (Object[] row : results) {
      String phone = row[0] != null ? row[0].toString() : null;
      String firstName = row[1] != null ? row[1].toString() : null;
      String lastName = row[2] != null ? row[2].toString() : null;
      return new User(firstName, lastName, email, null, phone, null);
    }
    return null;
  }

  /**
   * Checks if a bid is valid before saving it.
   *
   * @param bidAmount the amount of the bid
   * @param participantEmail the email of the participant
   * @param auctionId the ID of the auction
   * @throws SQLException if the bid is not valid or a database access error occurs
   */
  public synchronized void checkBid(int bidAmount, String participantEmail, int auctionId) throws SQLException {
    String retrieveSql = "SELECT auction.current_bid, auction.current_bidder, auction.reserve_price, auction.minimum_bid_increment, auction.status, auction.creator_email FROM auction WHERE auction.ID=?;";
    ArrayList<Object[]> results = super.getDatabase().query(retrieveSql, auctionId);
    for (Object[] row : results) {
      int currentBid = Integer.parseInt(row[0].toString());
      String currentBidder = row[1] != null ? row[1].toString() : null;
      int reservePrice = Integer.parseInt(row[2].toString());
      int increment = Integer.parseInt(row[3].toString());
      String status = row[4].toString();
      String seller = row[5].toString();

      if (!status.equals("ONGOING")) throw new SQLException("The auction is closed.");
      if (participantEmail.equals(currentBidder)) throw new SQLException("You are the current bidder.");
      if (participantEmail.equals(seller)) throw new SQLException("You cannot bid for your item");
      if (currentBid > 0 && bidAmount <= currentBid + increment) throw new SQLException("Your bid is not high enough.");
      if (bidAmount < reservePrice) throw new SQLException("Your bid must be at least the reserve price.");
    }
  }

  /**
   * Downloads an image from the repository.
   *
   * @param imagePath the path to the image
   * @return the image data as a byte array
   */
  private byte[] downloadImageFromRepository(String imagePath) {
    try {
      BufferedImage image = ImageIO.read(new File(imagePath));
      ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", outStreamObj);
      return outStreamObj.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Saves an image to the repository.
   *
   * @param imageBytes the image data as a byte array
   * @param imageTitle the title of the image
   * @return the path to the saved image
   */
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

  /**
   * Creates the necessary tables in the database.
   *
   * @throws SQLException if a database access error occurs
   */
  private void createTables() throws SQLException {
    String sqlTableUser = "CREATE TABLE IF NOT EXISTS \"user\" ( user_email email PRIMARY KEY, password VARCHAR(255) NOT NULL, phone_number VARCHAR(20), first_name first_last_name, last_name first_last_name );";
    super.getDatabase().update(sqlTableUser);

    String sqlTableParticipant = "CREATE TABLE IF NOT EXISTS participant ( user_email email PRIMARY KEY, birth_date DATE NOT NULL, FOREIGN KEY (user_email) references \"user\" (user_email) ON UPDATE CASCADE ON DELETE CASCADE );";
    super.getDatabase().update(sqlTableParticipant);

    String sqlTableAuction = "CREATE TABLE IF NOT EXISTS auction ( ID SERIAL PRIMARY KEY, title VARCHAR(80) CHECK (length(title) > 5) NOT NULL, description VARCHAR(1400) CHECK (length(description) > 20) NOT NULL, reserve_price INTEGER NOT NULL, buyout_price INTEGER NOT NULL, minimum_bid_increment INTEGER NOT NULL, current_bid INTEGER, current_bidder email, image_data VARCHAR(250) NOT NULL, status VARCHAR(7) CHECK (status IN ('ONGOING', 'CLOSED')) NOT NULL, start_time TIME NOT NULL, end_time TIME NOT NULL, creator_email email NOT NULL, FOREIGN KEY (creator_email) references participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (current_bidder) references participant (user_email) ON UPDATE CASCADE );";
    super.getDatabase().update(sqlTableAuction);

    String sqlTableBid = "CREATE TABLE IF NOT EXISTS bid ( bid_id SERIAL PRIMARY KEY, participant_email email NOT NULL, auction_id INTEGER NOT NULL, bid_amount INTEGER CHECK (bid_amount > 0) NOT NULL, FOREIGN KEY (participant_email) REFERENCES participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (auction_id) REFERENCES auction (id) ON DELETE CASCADE );";
    super.getDatabase().update(sqlTableBid);

    String sqlTableNotification = "CREATE TABLE IF NOT EXISTS notification ( notification_id SERIAL PRIMARY KEY, receiver email, content VARCHAR(1000), date DATE, time TIME, FOREIGN KEY (receiver) REFERENCES participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE );";
    super.getDatabase().update(sqlTableNotification);

    String sqlTableBannedParticipant = "CREATE TABLE IF NOT EXISTS banned_participant ( user_email email, reason VARCHAR(600), FOREIGN KEY (user_email) references participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE );";
    super.getDatabase().update(sqlTableBannedParticipant);

    String sqlTableModerator = "CREATE TABLE IF NOT EXISTS moderator ( moderator_email email PRIMARY KEY, personal_email email, FOREIGN KEY (moderator_email) REFERENCES \"user\" (user_email) );";
    super.getDatabase().update(sqlTableModerator);
  }

  /**
   * Inserts a default moderator into the database.
   *
   * @throws SQLException if a database access error occurs
   */
  private void insertModerator() throws SQLException {
    String sqlInsertUser = "INSERT INTO \"user\"(user_email, password, phone_number, first_name, last_name) VALUES ('bob@bidhub', '1234', null, null, null);";
    super.getDatabase().update(sqlInsertUser);
    String sqlInsertModerator = "INSERT INTO moderator (moderator_email, personal_email) VALUES ('bob@bidhub', null);";
    super.getDatabase().update(sqlInsertModerator);
  }
}