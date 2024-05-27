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

public class AuctionDatabase extends DatabasePersistence
    implements AuctionPersistence
{
  public AuctionDatabase() throws SQLException, ClassNotFoundException
  {
    String sqlCreateSchema="CREATE SCHEMA IF NOT EXISTS database_sep_group7;";
    super.getDatabase().update(sqlCreateSchema);
    String sqlCreateDomain1="CREATE DOMAIN email VARCHAR(200);";
    String sqlCreateDomain2="CREATE DOMAIN first_last_name VARCHAR(100);";
    super.getDatabase().update(sqlCreateDomain1);
    super.getDatabase().update(sqlCreateDomain2);
    createTables();
    insertModerator();
  }
  private void createTables() throws SQLException
  {
    String sqlTableUser="CREATE TABLE IF NOT EXISTS \"user\"\n" + "(\n"
        + "    user_email   email PRIMARY KEY,\n"
        + "    password     VARCHAR(255) NOT NULL,\n"
        + "    phone_number VARCHAR(20),\n"
        + "    first_name   first_last_name,\n"
        + "    last_name    first_last_name\n" + ");";
    super.getDatabase().update(sqlTableUser);

    String sqlTableParticipant="CREATE TABLE IF NOT EXISTS participant\n" + "(\n"
        + "    user_email email PRIMARY KEY,\n"
        + "    birth_date DATE NOT NULL,\n"
        + "    FOREIGN KEY (user_email) references \"user\" (user_email) ON UPDATE CASCADE ON DELETE CASCADE\n"
        + ");";
    super.getDatabase().update(sqlTableParticipant);

    String sqlTableAuction="CREATE TABLE IF NOT EXISTS auction\n" + "(\n"
        + "    ID                    SERIAL PRIMARY KEY,\n"
        + "    title                 VARCHAR(80) CHECK (length(title) > 5)              NOT NULL,\n"
        + "    description           VARCHAR(1400) CHECK (length(description) > 20)     NOT NULL,\n"
        + "    reserve_price         INTEGER                                            NOT NULL,\n"
        + "    buyout_price          INTEGER                                            NOT NULL,\n"
        + "    minimum_bid_increment INTEGER                                            NOT NULL,\n"
        + "    current_bid           INTEGER,\n"
        + "    current_bidder        email,\n"
        + "    image_data            VARCHAR(250)                                       NOT NULL,\n"
        + "    status                VARCHAR(7) CHECK (status IN ('ONGOING', 'CLOSED')) NOT NULL,\n"
        + "    start_time            TIME                                               NOT NULL,\n"
        + "    end_time              TIME                                               NOT NULL,\n"
        + "    creator_email         email                                              NOT NULL,\n"
        + "    FOREIGN KEY (creator_email) references participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE,\n"
        + "    FOREIGN KEY (current_bidder) references participant (user_email) ON UPDATE CASCADE\n"
        + ");";
    super.getDatabase().update(sqlTableAuction);

    String sqlTableBid="CREATE TABLE IF NOT EXISTS bid\n" + "(\n"
        + "    bid_id            SERIAL PRIMARY KEY,\n"
        + "    participant_email email                          NOT NULL,\n"
        + "    auction_id        INTEGER                        NOT NULL,\n"
        + "    bid_amount        INTEGER CHECK (bid_amount > 0) NOT NULL,\n"
        + "    FOREIGN KEY (participant_email) REFERENCES participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE,\n"
        + "    FOREIGN KEY (auction_id) REFERENCES auction (id) ON DELETE CASCADE\n"
        + ");";
    super.getDatabase().update(sqlTableBid);

    String sqlTableNotification="CREATE TABLE IF NOT EXISTS notification\n" + "(\n"
        + "    notification_id SERIAL PRIMARY KEY,\n"
        + "    receiver        email,\n"
        + "    content         VARCHAR(1000),\n" + "    date            DATE,\n"
        + "    time            TIME,\n"
        + "    FOREIGN KEY (receiver) REFERENCES participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE\n"
        + ");";
    super.getDatabase().update(sqlTableNotification);

    String sqlTableBannedParticipant="CREATE TABLE IF NOT EXISTS banned_participant\n" + "(\n"
        + "    user_email email,\n" + "    reason     VARCHAR(600),\n"
        + "    FOREIGN KEY (user_email) references participant (user_email) ON UPDATE CASCADE ON DELETE CASCADE\n"
        + ");";
    super.getDatabase().update(sqlTableBannedParticipant);

    String sqlTableModerator="\n" + "CREATE TABLE IF NOT EXISTS moderator\n" + "(\n"
        + "    moderator_email email PRIMARY KEY,\n"
        + "    personal_email  email,\n"
        + "    FOREIGN KEY (moderator_email) REFERENCES \"user\" (user_email)\n"
        + ");";
    super.getDatabase().update(sqlTableModerator);
  }
  private void insertModerator() throws SQLException
  {
    String sqlInsertUser="INSERT INTO \"user\"(user_email, password, phone_number, first_name, last_name)\n"
        + "VALUES ('bob@bidhub', '1234', null, null, null);\n";
    super.getDatabase().update(sqlInsertUser);
    String sqlInsertModerator="INSERT INTO moderator (moderator_email, personal_email)\n"
        + "VALUES ('bob@bidhub', null);";
    super.getDatabase().update(sqlInsertModerator);
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
        "SELECT *\n" + "FROM auction\n" + "WHERE id=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, id);
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

  @Override public synchronized Notification saveNotification(String content,
      String receiver) throws SQLException
  {
    String sql = "INSERT INTO notification(receiver, content, date, time) VALUES (?, ?, ?, ?);";
    Date date = Date.valueOf(LocalDate.now());
    Time time = Time.valueOf(LocalTime.now());

    super.getDatabase().update(sql, receiver, content, date, time);
    return new Notification(date + " " + time, content, receiver);
  }

  @Override public synchronized Bid saveBid(String participantEmail,
      int bidAmount, int auctionId) throws SQLException
  {
    checkBid(bidAmount, participantEmail, auctionId);
    String sql = "INSERT INTO bid (participant_email, auction_id, bid_amount) VALUES (?, ?, ?)";
    super.getDatabase().update(sql, participantEmail, auctionId, bidAmount);
    Bid bid = new Bid(auctionId, participantEmail, bidAmount);
    updateCurrentBid(bid);
    return bid;
  }

  @Override public synchronized void markAsClosed(int id) throws SQLException
  {
    String sql = "UPDATE auction SET status='CLOSED'\n" + "WHERE ID=?;";
    super.getDatabase().update(sql, id);
  }

  @Override public synchronized Bid getCurrentBidForAuction(int auctionId)
      throws SQLException
  {
    String sql = "SELECT current_bid, current_bidder FROM auction WHERE ID=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, auctionId);
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

  @Override public synchronized User getUserInfo(String email)
      throws SQLException
  {
    User user = getUser(email);
    if (user != null)
    {
      if (email.equals(super.getModeratorEmail()))
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

  @Override public synchronized void deleteAuction(String moderatorEmail,
      int auctionId, String reason) throws SQLException
  {
    String sql = "DELETE FROM auction WHERE ID=?;";
    super.getDatabase().update(sql, auctionId);
  }

  @Override public synchronized Bid buyout(String bidder, int auctionId)
      throws SQLException
  {
    Auction auction = getAuctionById(auctionId);
    if (auction != null)
    {
      if (auction.getCurrentBid() != 0)
        throw new SQLException("No buyout option. This auction has bids.");
      Bid bid = saveBid(bidder, auction.getPriceConstraint().getBuyoutPrice(),
          auctionId);
      String sql = "UPDATE auction SET status='CLOSED' WHERE ID=?;";
      super.getDatabase().update(sql, auctionId);

      return bid;
    }
    else
    {
      throw new SQLException("Auction does not exist or has already ended.");
    }
  }

  private synchronized void updateCurrentBid(Bid currentBid) throws SQLException
  {
    String sql = "UPDATE auction SET current_bid=?, current_bidder=? WHERE auction.ID=?;";
    super.getDatabase()
        .update(sql, currentBid.getBidAmount(), currentBid.getBidder(),
            currentBid.getAuctionId());
  }

  private synchronized Date getParticipantInfo(String email) throws SQLException
  {
    String sql = "SELECT participant.birth_date FROM participant WHERE user_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      return Date.valueOf(row[0].toString());
    }
    return null;
  }

  private synchronized String getModeratorSpecificInfo() throws SQLException
  {
    String sql = "SELECT personal_email FROM moderator WHERE moderator_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase()
        .query(sql, super.getModeratorEmail());
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      if (row[0] != null)
        return row[0].toString();
    }
    return null;
  }

  private synchronized User getUser(String email) throws SQLException
  {
    String sql = "SELECT phone_number, first_name, last_name FROM \"user\" WHERE user_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
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

  private synchronized void checkBid(int bidAmount, String participantEmail,
      int auctionId) throws SQLException
  {
    String retrieveSql =
        "SELECT auction.current_bid, auction.current_bidder, auction.reserve_price, auction.minimum_bid_increment, auction.status, auction.creator_email\n"
            + "FROM auction\n" + "WHERE auction.ID=?;";
    ArrayList<Object[]> results = super.getDatabase()
        .query(retrieveSql, auctionId);
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
