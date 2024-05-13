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

  //so the moderator just logs in, and then they can change their password
  private static final String MODERATOR_EMAIL = "bob@bidhub";
  private static final String MODERATOR_TEMPORARY_PASSWORD = "1234";

  // private static final String PASSWORD = "1706";
  private static final String PASSWORD = "344692StupidPass";

  //private static final String PASSWORD = "2031";

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
      checkAuctionTime(auctionTime);
      String sql =
          "INSERT INTO auction(title, description, reserve_price, buyout_price, minimum_bid_increment, current_bid, current_bidder, image_data, status, start_time, end_time, creator_email) \n"
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

      PreparedStatement statement = connection.prepareStatement(sql,
          PreparedStatement.RETURN_GENERATED_KEYS);

      statement.setString(1, checkTitle(title));
      statement.setString(2, checkDescription(description));
      statement.setInt(3, checkReservePrice(reservePrice));
      statement.setInt(4, checkBuyoutPrice(buyoutPrice, reservePrice));
      statement.setInt(5, checkMinimumIncrement(minimumIncrement));
      statement.setInt(6, 0);
      statement.setString(7, null);
      statement.setString(8, "temp_path");
      statement.setString(9, "ONGOING");

      LocalTime now = LocalTime.now();
      Time start = Time.valueOf(now);
      statement.setTime(10, start);
      Time end = Time.valueOf(now.plusHours(auctionTime));
      //Time end = Time.valueOf(now.plusSeconds(auctionTime));

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
        String seller = row[12].toString(); //the correct line
        //String seller = null;
        return new Auction(id, title, description, reservePrice, buyoutPrice,
            minimumIncrement, auctionStart, auctionEnd, currentBid,
            currentBidder, seller, imageData, status);
      }
      catch (Exception e)
      {
        // maybe throw exception with "No auction with this id", but for testing:
        e.printStackTrace();
      }
    }
    System.out.println("request for auction by ID in database");
    return null;
  }

  private Auction getCardAuctionById(int id) throws SQLException
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

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    String sql = "SELECT ID FROM sprint1database.auction WHERE status='ONGOING';";
    return getAuctions(sql, null);
  }

  @Override public NotificationList getNotifications(String receiver)
      throws SQLException
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

  @Override public Notification saveNotification(String content,
      String receiver) throws SQLException
  {
    String sql = "INSERT INTO notification(receiver, content, date, time) VALUES (?, ?, ?, ?);";
    Date date = Date.valueOf(LocalDate.now());
    Time time = Time.valueOf(LocalTime.now());

    database.update(sql, receiver, content, date, time);
    return new Notification(date + " " + time, content, receiver);
  }

  @Override public Bid saveBid(String participantEmail, int bidAmount,
      int auctionId) throws SQLException
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
      checkBid(bidAmount, participantEmail, currentBid, currentBidder,
          reservePrice, increment, status, seller);
    }
    String sql = "INSERT INTO sprint1database.bid (participant_email, auction_id, bid_amount) VALUES (?, ?, ?)";
    database.update(sql, participantEmail, auctionId, bidAmount);
    Bid bid = new Bid(auctionId, participantEmail, bidAmount);
    updateCurrentBid(bid);
    return bid;
  }

  @Override public void markAsClosed(int id) throws SQLException
  {
    String sql = "UPDATE auction SET status='CLOSED'\n" + "WHERE ID=?;";
    database.update(sql, id);
  }

  @Override public Bid getCurrentBidForAuction(int auctionId)
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

  @Override public User createUser(String firstname, String lastname,
      String email, String password, String repeatedPassword, String phone,
      LocalDate birthday) throws SQLException
  {

    String sqlUser =
        "INSERT INTO sprint1database.users(user_email, password, phone_number, first_name, last_name)  \n"
            + "VALUES(?,?,?,?,?);";
    String sqlParticipant = "INSERT INTO participant(user_email, birth_date) VALUES (?, ?);\n";

    checkFirstName(firstname);
    checkLastName(lastname);
    checkEmail(email);
    checkPassword(password, repeatedPassword);
    checkPhone(phone);
    ageValidation(birthday);
    database.update(sqlUser, email, password, phone, firstname, lastname);
    Date date = Date.valueOf(birthday);
    database.update(sqlParticipant, email, date);
    return new User(firstname, lastname, email, password, phone, birthday);
  }

  @Override public String login(String email, String password)
      throws SQLException
  {
    if (!validateForLogin(email, password))
    {
      throw new SQLException("Credentials do not match");
    }
    return email;
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    String sql = "SELECT DISTINCT bid.auction_id\n" + "FROM bid\n"
        + "WHERE participant_email=?;";
    return getAuctions(sql, bidder);
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    String sql = "SELECT ID from auction WHERE creator_email=?;";
    return getAuctions(sql, seller);
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

  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    validateNewPassword(userEmail, oldPassword, newPassword, repeatPassword);
    String sql = "UPDATE users SET password=?\n" + "WHERE users.user_email=?;";
    database.update(sql, newPassword, userEmail);
  }

  @Override public User getUserInfo(String email) throws SQLException
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
        Date birthday=getParticipantInfo(email);
        return new User(user.getFirstname(), user.getLastname(), email, null,
              user.getPhone(), birthday.toLocalDate());
        }
      }
    return null;
  }
  @Override public User getModeratorInfo() throws SQLException
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

  @Override public boolean isModerator(String email) throws SQLException
  {
    return isEmailIn(email, "moderator_email", "moderator");
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
    if (validateForLogin(oldEmail, password))
    {
      checkFirstName(firstname);
      checkLastName(lastname);
      if (!oldEmail.equals(email))
        checkEmail(email);
      checkPassword(password, password);
      ageValidation(birthday);
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

  private void checkBid(int bidAmount, String participantEmail, int currentBid,
      String currentBidder, int reservePrice, int increment, String status,
      String seller) throws SQLException
  {
    if (!status.equals("ONGOING"))
      throw new SQLException("The auction is closed.");
    if (participantEmail.equals(MODERATOR_EMAIL))
      throw new SQLException("The moderator cannot place bids.");
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

  private void checkAuctionTime(int auctionTime) throws SQLException
  {
    // to be updated when the moderator adds the time interval
    if (auctionTime <= 0 || auctionTime > 24)
      throw new SQLException("The auction time can be at most 24 hours!");
  }

  private void checkFirstName(String firstname) throws SQLException
  {
    if (firstname.isEmpty())
    {
      throw new SQLException("Empty first name.");
    }
    if (firstname.length() < 4)
    {
      throw new SQLException(
          "The first name must be at least 3 characters long.");
    }
  }

  private void checkLastName(String lastname) throws SQLException
  {
    if (lastname.isEmpty())
    {
      throw new SQLException("Empty last name.");
    }
    if (lastname.length() < 4)
    {
      throw new SQLException(
          "The last name must be at least 3 characters long.");
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
    if (phone.length() < 4)
    {
      throw new SQLException("Invalid phone number.");
    }
    if (isPhoneInTheSystem(phone) != null)
    {
      throw new SQLException("This phone number is already in the system.");
    }
  }

  private void ageValidation(LocalDate birthday) throws SQLException
  {
    if (birthday != null)
    {
      LocalDate currentDate = LocalDate.now();
      Period period = Period.between(birthday, currentDate);
      int age = period.getYears();
      if (age < 18)
        throw new SQLException("You must be over 18 years old.");
    }
  }

  private byte[] downloadImageFromRepository(String imagePath)
  {
    try
    {
      BufferedImage image = ImageIO.read(new File(imagePath));

      ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", outStreamObj);

      byte[] byteArray = outStreamObj.toByteArray();
      return byteArray;

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
