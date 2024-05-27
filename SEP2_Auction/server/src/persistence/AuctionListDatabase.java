package persistence;

import model.domain.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class AuctionListDatabase extends DatabasePersistence
    implements AuctionListPersistence
{

  public AuctionListDatabase() throws SQLException, ClassNotFoundException
  {

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

  private synchronized Auction getCardAuctionById(int id) throws SQLException
  {
    String sql = "SELECT title, current_bid, image_data, end_time\n"
        + "FROM auction\n" + "WHERE id=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, id);
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
    String sql = "SELECT ID FROM auction WHERE status='ONGOING';";
    return getAuctions(sql, null);
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
    String sql = "SELECT ID FROM auction;";
    return getAuctions(sql, null);
  }

  private synchronized AuctionList getAuctions(String sql, String user)
      throws SQLException
  {
    ArrayList<Object[]> results;
    if (user == null)
      results = super.getDatabase().query(sql);
    else
      results = super.getDatabase().query(sql, user);
    AuctionList auctions = new AuctionList();
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      int id = Integer.parseInt(row[0].toString());
      auctions.addAuction(getCardAuctionById(id));
    }
    return auctions;
  }

  @Override public synchronized boolean isModerator(String email)
      throws SQLException
  {
    return isEmailIn(email, "moderator_email", "moderator");
  }

  private synchronized boolean isEmailIn(String email, String field,
      String table) throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM " + table + " WHERE " + field + " =?";
    ArrayList<Object[]> result = super.getDatabase().query(sql, email);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
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

}
