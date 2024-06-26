package persistence;

import model.domain.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * The AuctionListDatabase class implements the AuctionListPersistence interface
 * and provides methods for managing auction lists in the database.
 */
public class AuctionListDatabase extends DatabasePersistence implements AuctionListPersistence
{

  /**
   * Constructs an AuctionListDatabase and initializes the database connection.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public AuctionListDatabase() throws SQLException, ClassNotFoundException
  {

  }

  /**
   * Retrieves an auction by its ID from the database.
   *
   * @param id the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized Auction getAuctionById(int id) throws SQLException
  {
    String sql = "SELECT *\n" + "FROM auction\n" + "WHERE id=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, id);
    for (Object[] row : results)
    {
      try
      {
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
        return new Auction(id, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionStart, auctionEnd, currentBid, currentBidder, seller, imageData, status);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Retrieves a simplified auction (card view) by its ID from the database.
   *
   * @param id the identifier of the auction.
   * @return the simplified auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  private synchronized Auction getCardAuctionById(int id) throws SQLException
  {
    String sql = "SELECT title, current_bid, image_data, end_time\n"
            + "FROM auction\n" + "WHERE id=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, id);
    for (Object[] row : results)
    {
      String title = row[0].toString();
      int currentBid = Integer.parseInt(row[1].toString());
      String imagePath = row[2].toString();
      byte[] imageData = downloadImageFromRepository(imagePath);
      Time auctionEnd = Time.valueOf(row[3].toString());
      return new Auction(id, title, currentBid, auctionEnd, imageData);
    }
    return null;
  }

  /**
   * Retrieves a list of ongoing auctions from the database.
   *
   * @return a list of ongoing auctions.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getOngoingAuctions() throws SQLException
  {
    String sql = "SELECT ID FROM auction WHERE status='ONGOING';";
    return getAuctions(sql, null);
  }

  /**
   * Retrieves a list of previous bids made by a specific bidder from the database.
   *
   * @param bidder the email of the bidder.
   * @return a list of previous bids made by the bidder.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getPreviousBids(String bidder) throws SQLException
  {
    String sql = "SELECT DISTINCT bid.auction_id\n" + "FROM bid\n"
            + "WHERE participant_email=?;";
    return getAuctions(sql, bidder);
  }

  /**
   * Retrieves a list of auctions created by a specific seller from the database.
   *
   * @param seller the email of the seller.
   * @return a list of auctions created by the seller.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getCreatedAuctions(String seller) throws SQLException
  {
    String sql = "SELECT ID from auction WHERE creator_email=?;";
    return getAuctions(sql, seller);
  }

  /**
   * Retrieves a list of all auctions from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the auctions.
   * @return a list of all auctions.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getAllAuctions(String moderatorEmail) throws SQLException
  {
    String sql = "SELECT ID FROM auction;";
    return getAuctions(sql, null);
  }

  /**
   * Retrieves a list of auctions based on the specified SQL query and user.
   *
   * @param sql the SQL query to execute.
   * @param user the email of the user (can be null).
   * @return a list of auctions.
   * @throws SQLException if there is a database access error.
   */
  private synchronized AuctionList getAuctions(String sql, String user) throws SQLException
  {
    ArrayList<Object[]> results;
    if (user == null)
      results = super.getDatabase().query(sql);
    else
      results = super.getDatabase().query(sql, user);
    AuctionList auctions = new AuctionList();
    for (Object[] row : results)
    {
      int id = Integer.parseInt(row[0].toString());
      auctions.addAuction(getCardAuctionById(id));
    }
    return auctions;
  }

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized boolean isModerator(String email) throws SQLException
  {
    return isEmailIn(email, "moderator_email", "moderator");
  }

  /**
   * Checks if an email exists in a specified table and field.
   *
   * @param email the email to check.
   * @param field the field to check.
   * @param table the table to check.
   * @return true if the email exists, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  private synchronized boolean isEmailIn(String email, String field, String table) throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM " + table + " WHERE " + field + " =?";
    ArrayList<Object[]> result = super.getDatabase().query(sql, email);
    for (Object[] row : result)
    {
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  /**
   * Downloads an image from the repository.
   *
   * @param imagePath the path to the image.
   * @return the image data as a byte array.
   */
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
}
