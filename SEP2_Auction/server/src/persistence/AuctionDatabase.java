package persistence;

import model.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;

import utility.persistence.MyDatabase;

public class AuctionDatabase implements AuctionPersistence
{
  private MyDatabase database;
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

  public AuctionDatabase() throws SQLException, ClassNotFoundException
  {
    this.database = new MyDatabase(DRIVER, URL, USER, PASSWORD);
    Class.forName(DRIVER);
  }

  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  @Override public synchronized Auction saveAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws SQLException
  {
    try (Connection connection = getConnection())
    {
      String sql =
          "INSERT INTO auction(title, description, reserve_price, buyout_price, minimum_bid_increment, current_bid, current_bidder, image_data, status, start_time, end_time, creator_email) \n"
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

      PreparedStatement statement = connection.prepareStatement(sql,
          PreparedStatement.RETURN_GENERATED_KEYS);

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
      // Time end = Time.valueOf(now.plusSeconds(auctionTime));

      statement.setTime(11, end);
      statement.setString(12, seller);

      statement.executeUpdate();
      ResultSet key = statement.getGeneratedKeys();

      if (key.next())
      {
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
            minimumIncrement, start, end, 0, "No bidder", seller, imageData,
            "ONGOING");
      }
      else
      {
        throw new SQLException("No key generated");
      }
    }
  }

  @Override public synchronized Auction getAuctionById(int id)
      throws SQLException
  {
    String sql =
        "SELECT *\n" + "FROM sprint1database.auction\n" + "WHERE id=?;";
    ArrayList<Object[]> results = database.query(sql, id);
    for (int i = 0; i < results.size(); i++)
    {
      try
      {
        Object[] row = results.get(i);
        String title = row[1].toString();
        String description = row[2].toString();
        int reservePrice = Integer.parseInt(row[3].toString());
        int buyoutPrice = Integer.parseInt(row[4].toString());
        int minimumIncrement = Integer.parseInt(row[5].toString());
        int currentBid = Integer.parseInt(row[6].toString());
        String currentBidder = null;
        if (row[7] != null)
        {
          currentBidder = row[7].toString();
        }
        String imagePath = row[8].toString();
        byte[] imageData = downloadImageFromRepository(imagePath);

        String status = row[9].toString();
        Time auctionStart = Time.valueOf(row[10].toString());
        Time auctionEnd = Time.valueOf(row[11].toString());
        String seller = row[12].toString();
        return new Auction(id, title, description, reservePrice, buyoutPrice,
            minimumIncrement, auctionStart, auctionEnd, currentBid,
            currentBidder, seller, imageData, status);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }

  private synchronized Auction getCardAuctionById(int id) throws SQLException
  {
    String sql = "SELECT title, current_bid, image_data, end_time\n"
        + "FROM sprint1database.auction\n" + "WHERE id=?;";
    ArrayList<Object[]> results = database.query(sql, id);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      String title = row[0].toString();
      int currentBid = Integer.parseInt(row[1].toString());
      String imagePath = row[2].toString();
      byte[] imageData = downloadImageFromRepository(imagePath);
      Time auctionEnd = Time.valueOf(row[3].toString());
      return new Auction(id, title, currentBid, auctionEnd, imageData);
    }
    return null;
  }

  @Override public synchronized AuctionList getOngoingAuctions()
      throws SQLException
  {
    //TODO: change sprint1database
    String sql = "SELECT ID FROM sprint1database.auction WHERE status='ONGOING';";
    return getAuctions(sql, null);
  }

  @Override public synchronized NotificationList getNotifications(
      String receiver) throws SQLException
  {
    String sql = "SELECT * FROM notification WHERE receiver=?;";
    ArrayList<Object[]> results = database.query(sql, receiver);
    NotificationList notifications = new NotificationList();
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      String content = row[2].toString();
      String dateTime = row[3].toString() + " " + row[4].toString();
      notifications.addNotification(
          new Notification(dateTime, content, receiver));
    }
    System.out.println("request for notifications in database");
    return notifications;
  }

  @Override public synchronized Notification saveNotification(String content,
      String receiver) throws SQLException
  {
    String sql = "INSERT INTO notification(receiver, content, date, time) VALUES (?, ?, ?, ?);";
    Date date = Date.valueOf(LocalDate.now());
    Time time = Time.valueOf(LocalTime.now());

    database.update(sql, receiver, content, date, time);
    return new Notification(date + " " + time, content, receiver);
  }

  @Override public synchronized Bid saveBid(String participantEmail,
      int bidAmount, int auctionId) throws SQLException
  {
    checkBid(bidAmount, participantEmail, auctionId);
    String sql = "INSERT INTO sprint1database.bid (participant_email, auction_id, bid_amount) VALUES (?, ?, ?)";
    database.update(sql, participantEmail, auctionId, bidAmount);
    Bid bid = new Bid(auctionId, participantEmail, bidAmount);
    updateCurrentBid(bid);
    return bid;
  }

  @Override public synchronized void markAsClosed(int id) throws SQLException
  {
    String sql = "UPDATE auction SET status='CLOSED'\n" + "WHERE ID=?;";
    database.update(sql, id);
  }

  @Override public synchronized Bid getCurrentBidForAuction(int auctionId)
      throws SQLException
  {
    String sql = "SELECT current_bid, current_bidder FROM auction WHERE ID=?;";
    ArrayList<Object[]> results = database.query(sql, auctionId);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      if (row[1] != null)
      {
        int currentBid = Integer.parseInt(row[0].toString());
        String currentBidder = row[1].toString();
        return new Bid(auctionId, currentBidder, currentBid);
      }
    }
    return null;
  }

  private void updateCurrentBid(Bid currentBid) throws SQLException
  {
    String sql = "UPDATE auction SET current_bid=?, current_bidder=? WHERE auction.ID=?;";
    database.update(sql, currentBid.getBidAmount(), currentBid.getBidder(),
        currentBid.getAuctionId());
  }

  @Override public synchronized User createUser(String firstname,
      String lastname, String email, String password, String repeatedPassword,
      String phone, LocalDate birthday) throws SQLException
  {
    String sqlUser =
        "INSERT INTO sprint1database.users(user_email, password, phone_number, first_name, last_name)  \n"
            + "VALUES(?,?,?,?,?);";
    String sqlParticipant = "INSERT INTO participant(user_email, birth_date) VALUES (?, ?);\n";

    checkEmail(email);
    checkPassword(password, repeatedPassword);
    checkPhone(phone);
    database.update(sqlUser, email, password, phone, firstname, lastname);
    Date date = Date.valueOf(birthday);
    database.update(sqlParticipant, email, date);
    return new User(firstname, lastname, email, password, phone, birthday);
  }

  @Override public synchronized String login(String email, String password)
      throws SQLException
  {
    if (!validateForLogin(email, password))
    {
      throw new SQLException("Credentials do not match");
    }
    if (isBanned(email))
      throw new SQLException(
          "Account closed. Reason: " + extractBanningReason(email));
    return email;
  }

  @Override public synchronized AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    String sql = "SELECT DISTINCT bid.auction_id\n" + "FROM bid\n"
        + "WHERE participant_email=?;";
    return getAuctions(sql, bidder);
  }

  @Override public synchronized AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    String sql = "SELECT ID from auction WHERE creator_email=?;";
    return getAuctions(sql, seller);
  }

  @Override public synchronized AuctionList getAllAuctions(
      String moderatorEmail) throws SQLException
  {
    String sql = "SELECT ID FROM sprint1database.auction;";
    return getAuctions(sql, null);
  }

  private AuctionList getAuctions(String sql, String user) throws SQLException
  {
    ArrayList<Object[]> results;
    if (user == null)
      results = database.query(sql);
    else
      results = database.query(sql, user);
    AuctionList auctions = new AuctionList();
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      int id = Integer.parseInt(row[0].toString());
      auctions.addAuction(getCardAuctionById(id));
    }
    System.out.println("request for auctions in database");
    return auctions;
  }

  @Override public synchronized ArrayList<User> getAllUsers()
      throws SQLException
  {
    String sql = "SELECT user_email, phone_number, first_name, last_name FROM users WHERE user_email!=?;\n";
    ArrayList<Object[]> queryResults = database.query(sql, MODERATOR_EMAIL);

    ArrayList<User> output = new ArrayList<>();
    String phone, firstName, lastName, email;

    for (Object[] userData : queryResults)
    {

      email = userData[0].toString();
      phone = userData[1].toString();
      firstName = userData[2].toString();
      lastName = userData[3].toString();

      output.add(new User(firstName, lastName, email, null, phone, null));
    }
    System.out.println("request for all users in database");
    return output;
  }

  @Override public synchronized void resetPassword(String userEmail,
      String oldPassword, String newPassword, String repeatPassword)
      throws SQLException
  {
    validateNewPassword(userEmail, oldPassword, newPassword, repeatPassword);
    String sql = "UPDATE users SET password=?\n" + "WHERE users.user_email=?;";
    database.update(sql, newPassword, userEmail);
  }

  @Override public synchronized User getUserInfo(String email)
      throws SQLException
  {
    User user = getUser(email);
    if (user != null)
    {
      if (email.equals(MODERATOR_EMAIL))
      {
        return new User(user.getFirstname(), user.getLastname(),
            getModeratorSpecificInfo(), null, user.getPhone(), null);
      }
      else
      {
        Date birthday = getParticipantInfo(email);
        return new User(user.getFirstname(), user.getLastname(), email, null,
            user.getPhone(), birthday.toLocalDate());
      }
    }
    return null;
  }

  @Override public synchronized User getModeratorInfo() throws SQLException
  {
    return getUserInfo(MODERATOR_EMAIL);
  }

  private Date getParticipantInfo(String email) throws SQLException
  {
    String sql = "SELECT participant.birth_date FROM participant WHERE user_email=?;\n";
    ArrayList<Object[]> results = database.query(sql, email);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      return Date.valueOf(row[0].toString());
    }
    return null;
  }

  private String getModeratorSpecificInfo() throws SQLException
  {
    String sql = "SELECT personal_email FROM moderator WHERE moderator_email=?;\n";
    ArrayList<Object[]> results = database.query(sql, MODERATOR_EMAIL);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      if (row[0] != null)
        return row[0].toString();
    }
    return null;
  }

  @Override public synchronized boolean isModerator(String email)
      throws SQLException
  {
    return isEmailIn(email, "moderator_email", "moderator");
  }

  @Override public synchronized User editInformation(String oldEmail,
      String firstname, String lastname, String email, String password,
      String phone, LocalDate birthday) throws SQLException
  {
    if (validateForLogin(oldEmail, password))
    {
      if (!oldEmail.equals(email))
        checkEmail(email);
      checkPassword(password, password);
      String isPhoneTaken = isPhoneInTheSystem(phone);
      if (isPhoneTaken != null)
        if (!isPhoneTaken.equals(oldEmail))
          throw new SQLException("This phone number is taken.");
      String sqlUser =
          "UPDATE users SET user_email=?, phone_number=?, first_name=?, last_name=?\n"
              + "WHERE user_email=?;";
      String sqlParticipantOrModerator;

      if (oldEmail.equals(MODERATOR_EMAIL))
      {
        database.update(sqlUser, oldEmail, phone, firstname, lastname,
            oldEmail);

        sqlParticipantOrModerator = "UPDATE moderator SET personal_email=?\n"
            + "WHERE moderator_email=?;";
        database.update(sqlParticipantOrModerator, email, oldEmail);
      }
      else
      {
        database.update(sqlUser, email, phone, firstname, lastname, oldEmail);
        sqlParticipantOrModerator =
            "UPDATE participant SET birth_date=?\n" + "WHERE user_email=?;";
        Date date = Date.valueOf(birthday);
        database.update(sqlParticipantOrModerator, date, email);
      }
      return new User(firstname, lastname, email, password, phone, birthday);
    }
    throw new SQLException("Wrong password");
  }

  @Override public synchronized void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    if (isBanned(participantEmail))
      throw new SQLException("This participant is already banned.");

    String sql = "INSERT INTO banned_participant(user_email, reason) VALUES (?, ?);\n";
    database.update(sql, participantEmail, reason);
    deleteAuctionsStartedBy(participantEmail);
    updateCurrentBidAndCurrentBidderAfterBan(participantEmail);
    deleteBids(participantEmail);
    throw new SQLException("Account linked to email " + participantEmail
        + " successfully banned.");
  }

  @Override public synchronized void deleteAuction(String moderatorEmail,
      int auctionId, String reason) throws SQLException
  {
    String sql = "DELETE FROM auction WHERE ID=?;";
    database.update(sql, auctionId);
  }

  private void deleteAuctionsStartedBy(String email) throws SQLException
  {
    String sql = "DELETE FROM auction WHERE creator_email=?;";
    database.update(sql, email);
  }

  @Override public void deleteAccount(String email, String password)
      throws SQLException
  {
    if (!validateForLogin(email, password))
    {
      throw new SQLException("Wrong password");
    }
    System.out.println("valid for login");

    String sql1 = "DELETE FROM participant WHERE user_email=?;";
    String sql2 = "DELETE FROM users WHERE user_email=?;";
    database.update(sql1, email);
    database.update(sql2, email);
    System.out.println("accounts deleted");

    deleteAuctionsStartedBy(email);
    System.out.println("auctions started deleted");
    updateCurrentBidAndCurrentBidderAfterBan(email);
    System.out.println("auctions bids updated");

    deleteBids(email);
    System.out.println("bids deleted");
  }

  private void updateCurrentBidAndCurrentBidderAfterBan(String email)
      throws SQLException
  {
    String sql1 = "WITH to_be_updated_auctions AS (\n"
        + "    SELECT a.id AS auction_id, MAX(b.bid_amount) AS new_bid_amount, b.participant_email AS new_bidder\n"
        + "    FROM auction a\n"
        + "    LEFT JOIN bid b ON a.id = b.auction_id AND b.participant_email != ?\n"
        + "    WHERE a.current_bidder = ?\n"
        + "    GROUP BY a.id, b.participant_email\n"
        + "    ORDER BY a.id, new_bid_amount DESC\n" + "),\n"
        + "ordered_auctions AS (\n"
        + "    SELECT auction_id, new_bid_amount, new_bidder,\n"
        + "           ROW_NUMBER() OVER (PARTITION BY auction_id ORDER BY new_bid_amount DESC) AS row\n"
        + "    FROM to_be_updated_auctions\n" + ")\n" + "UPDATE auction\n"
        + "SET current_bid = CASE\n"
        + "                      WHEN ordered_auctions.new_bid_amount IS NOT NULL THEN ordered_auctions.new_bid_amount\n"
        + "                      ELSE 0\n" + "    END,\n"
        + "    current_bidder = ordered_auctions.new_bidder\n"
        + "FROM ordered_auctions\n"
        + "WHERE auction.id = ordered_auctions.auction_id\n"
        + "AND (ordered_auctions.row = 1 OR ordered_auctions.new_bid_amount IS NULL);";
    database.update(sql1, email, email);
  }

  private void deleteBids(String email) throws SQLException
  {
    String sql2 = "DELETE FROM bid WHERE participant_email=?;";
    database.update(sql2, email);
  }

  private boolean isBanned(String email) throws SQLException
  {
    String sql = "SELECT COUNT(*) FROM banned_participant WHERE user_email=?;\n";
    int count = 0;
    ArrayList<Object[]> results = database.query(sql, email);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    if (isBanned(email))
    {
      String sql = "SELECT reason FROM banned_participant WHERE user_email=?;\n";
      ArrayList<Object[]> results = database.query(sql, email);
      for (int i = 0; i < results.size(); i++)
      {
        Object[] row = results.get(i);
        if (row[0] != null)
          return row[0].toString();
      }
    }
    return null;
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    if (!isBanned(participantEmail))
      throw new SQLException("This participant is not banned.");
    String sql = "DELETE FROM banned_participant WHERE user_email=?;\n";
    database.update(sql, participantEmail);
    throw new SQLException("Account linked to email " + participantEmail
        + " successfully unbanned.");
  }

  private User getUser(String email) throws SQLException
  {
    String sql = "SELECT phone_number, first_name, last_name FROM users WHERE user_email=?;\n";
    ArrayList<Object[]> results = database.query(sql, email);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      String phone = null;
      if (row[0] != null)
      {
        phone = row[0].toString();
      }
      String firstName = null;
      if (row[1] != null)
      {
        firstName = row[1].toString();
      }
      String lastName = null;
      if (row[2] != null)
      {
        lastName = row[2].toString();
      }
      return new User(firstName, lastName, email, null, phone, null);
    }
    return null;
  }

  private void validateNewPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    checkPassword(newPassword, repeatPassword);
    String sql = "SELECT users.password FROM users\n" + "WHERE user_email=?;";
    ArrayList<Object[]> result = database.query(sql, userEmail);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      String retrievedOldPassword = row[0].toString();
      if (!retrievedOldPassword.equals(oldPassword))
        throw new SQLException("The old password is incorrect.");
      if (oldPassword.equals(newPassword))
        throw new SQLException(
            "The new password and the old one are the same.");
    }
  }

  private boolean isEmailIn(String email, String field, String table)
      throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM " + table + " WHERE " + field + " =?";
    ArrayList<Object[]> result = database.query(sql, email);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  private String isPhoneInTheSystem(String phone) throws SQLException
  {
    int count = 0;
    String sql =
        "SELECT user_email, count(*) FROM users\n" + "WHERE phone_number=?\n"
            + "GROUP BY user_email;";
    ArrayList<Object[]> result = database.query(sql, phone);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      count = Integer.parseInt(row[1].toString());
      if (count > 0)
        return row[0].toString();
    }
    return null;
  }

  private boolean validateForLogin(String email, String password)
      throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM users WHERE user_email=? AND password=?;";
    ArrayList<Object[]> result = database.query(sql, email, password);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  public void setBuyer(int auctionId, String current_bider) throws SQLException
  {
    String sql = "UPDATE auction SET current_bidder = ? WHERE id = ?";
    database.update(sql, current_bider, auctionId);
  }
/*
  @Override public void buyout(String bidder, int auctionId) throws SQLException
  {
    Auction auction = getAuctionById(auctionId);
    if (auction != null)
    {
      String sql = "UPDATE auction SET status='CLOSED', current_bidder=?, current_bid=buyout_price WHERE ID=?;";
      database.update(sql, bidder, auctionId);

      auction.setStatus("CLOSED");

      setBuyer(auctionId, bidder);
    }
    else
    {
      throw new SQLException("Auction does not exist or has already ended.");
    }
  }*/

  @Override public Bid buyout(String bidder, int auctionId) throws SQLException
  {
    Auction auction = getAuctionById(auctionId);
    if (auction != null)
    {
      if (auction.getCurrentBid() != 0)
        throw new SQLException("No buyout option. This auction has bids.");
      Bid bid = saveBid(bidder, auction.getPriceConstraint().getBuyoutPrice(),
          auctionId);
      String sql = "UPDATE auction SET status='CLOSED' WHERE ID=?;";
      database.update(sql, auctionId);

      return bid;
    }
    else
    {
      throw new SQLException("Auction does not exist or has already ended.");
    }
  }

  private void checkBid(int bidAmount, String participantEmail, int auctionId)
      throws SQLException
  {
    String retrieveSql =
        "SELECT auction.current_bid, auction.current_bidder, auction.reserve_price, auction.minimum_bid_increment, auction.status, auction.creator_email\n"
            + "FROM auction\n" + "WHERE auction.ID=?;";
    ArrayList<Object[]> results = database.query(retrieveSql, auctionId);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);

      int currentBid = Integer.parseInt(row[0].toString());
      String currentBidder = null;
      if (row[1] != null)
      {
        currentBidder = row[1].toString();
      }
      int reservePrice = Integer.parseInt(row[2].toString());
      int increment = Integer.parseInt(row[3].toString());
      String status = row[4].toString();
      String seller = row[5].toString();

      if (!status.equals("ONGOING"))
        throw new SQLException("The auction is closed.");
      if (participantEmail.equals(currentBidder))
        throw new SQLException("You are the current bidder.");
      if (participantEmail.equals(seller))
        throw new SQLException("You cannot bid for your item");
      if (currentBid > 0)
      {
        if (bidAmount <= currentBid + increment)
          throw new SQLException("Your bid is not high enough.");
      }
      if (bidAmount < reservePrice)
        throw new SQLException("Your bid must be at least the reserve price.");
    }
  }

  private void checkEmail(String email) throws SQLException
  {
    if (email.isEmpty())
    {
      throw new SQLException("Empty email.");
    }
    if (!email.contains("@"))
    {
      throw new SQLException("Email must be in 'name@domain' format.");
    }
    if (isEmailIn(email, "user_email", "users"))
    {
      throw new SQLException("Email is already in the system.");
    }
  }

  private void checkPassword(String password, String repeatedPassword)
      throws SQLException
  {
    if (password.isEmpty())
    {
      throw new SQLException("Empty password.");
    }
    if (password.length() < 4)
    {
      throw new SQLException(
          "The password must be at least 3 characters long.");
    }
    if (!password.equals(repeatedPassword))
      throw new SQLException("The passwords don't match.");
  }

  private void checkPhone(String phone) throws SQLException
  {
    if (isPhoneInTheSystem(phone) != null)
    {
      throw new SQLException("This phone number is already in the system.");
    }
  }

  private byte[] downloadImageFromRepository(String imagePath)
  {
    try
    {
      BufferedImage image = ImageIO.read(new File(imagePath));

      ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", outStreamObj);

      return outStreamObj.toByteArray();

    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  private String saveImageToRepository(byte[] imageBytes, String imageTitle)
  {
    String pathToImage = null;
    try
    {
      ByteArrayInputStream inStreamObj = new ByteArrayInputStream(imageBytes);
      BufferedImage newImage = ImageIO.read(inStreamObj);

      pathToImage = "server/images/" + imageTitle + ".jpg";
      ImageIO.write(newImage, "jpg", new File(pathToImage));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return pathToImage;
  }

}
