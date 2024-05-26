package persistence;

import model.domain.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class AuctionProtectionProxy extends DatabasePersistence implements AuctionPersistence
{

  private AuctionDatabase database;

  public AuctionProtectionProxy() throws SQLException, ClassNotFoundException
  {
    database=new AuctionDatabase();
  }

  @Override public Auction saveAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller) throws SQLException
  {
    if (!isNotModerator(seller))
      throw new SQLException("The moderator cannot start auctions.");
    checkAuctionTime(auctionTime);
    checkTitle(title);
    checkDescription(description);
    checkReservePrice(reservePrice);
    checkBuyoutPrice(buyoutPrice, reservePrice);
    checkMinimumIncrement(minimumIncrement);
    return database.saveAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public Auction getAuctionById(int id) throws SQLException
  {
    return database.getAuctionById(id);
  }

  @Override public void markAsClosed(int id) throws SQLException
  {
    database.markAsClosed(id);
  }

  @Override public Notification saveNotification(String content,
      String receiver) throws SQLException
  {
    return database.saveNotification(content, receiver);
  }

  @Override public Bid saveBid(String participantEmail, int bidAmount,
      int auctionId) throws SQLException
  {
    if (!isNotModerator(participantEmail))
      throw new SQLException("The moderator cannot place bids.");
    return database.saveBid(participantEmail, bidAmount, auctionId);
  }

  @Override public Bid getCurrentBidForAuction(int auctionId)
      throws SQLException
  {
    return database.getCurrentBidForAuction(auctionId);
  }

  @Override public User getUserInfo(String email) throws SQLException
  {
    return database.getUserInfo(email);
  }

  @Override public Bid buyout(String bidder, int auctionId) throws SQLException
  {
    if(!isNotModerator(bidder))
      throw new SQLException("You cannot participate in auctions.");
    return database.buyout(bidder, auctionId);
  }

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws SQLException
  {
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot delete this item.");
    checkReason(reason);
    database.deleteAuction(moderatorEmail, auctionId, reason);
  }

  private boolean isNotModerator(String email)
  {
    return !email.equals(super.getModeratorEmail());
  }

  private void checkTitle(String title) throws SQLException
  {
    int maxTitleLength = 80;
    int minTitleLength = 5;
    if (title.length() > maxTitleLength)
      throw new SQLException("The title is too long!");
    else if (title.length() < minTitleLength)
      throw new SQLException("The title is too short!");
  }

  private void checkDescription(String description) throws SQLException
  {
    int maxDescriptionLength = 1400, minDescriptionLength = 20;
    if (description.length() > maxDescriptionLength)
      throw new SQLException("The description is too long!");
    else if (description.length() < minDescriptionLength)
      throw new SQLException("The description is too short!");
  }

  private void checkReservePrice(int reservePrice) throws SQLException
  {
    if (reservePrice <= 0)
      throw new SQLException("The reserve price must be a positive number!");
  }

  private void checkBuyoutPrice(int buyoutPrice, int reservePrice)
      throws SQLException
  {
    if (buyoutPrice <= reservePrice)
      throw new SQLException(
          "The buyout price must be greater than the reserve price!");
  }

  private void checkMinimumIncrement(int minimumIncrement) throws SQLException
  {
    if (minimumIncrement < 1)
      throw new SQLException("The minimum bid increment must be at least 1!");
  }

  private void checkAuctionTime(int auctionTime) throws SQLException
  {
    // to be updated when the moderator adds the time interval
    if (auctionTime <= 0 || auctionTime > 24)
      throw new SQLException("The auction time can be at most 24 hours!");
  }

  private void checkReason(String reason) throws SQLException
  {
    if (reason == null || reason.length() < 3)
      throw new SQLException("The reason is too short.");
    if (reason.length() > 600)
      throw new SQLException("The reason is too long.");
  }
}
