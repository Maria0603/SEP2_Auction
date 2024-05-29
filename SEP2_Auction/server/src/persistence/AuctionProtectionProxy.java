package persistence;

import model.domain.*;

import java.sql.SQLException;

/**
 * The AuctionProtectionProxy class implements the AuctionPersistence interface
 * and acts as a protection proxy for the AuctionDatabase, providing access control for auction-related data.
 */
public class AuctionProtectionProxy extends DatabasePersistence implements AuctionPersistence
{

  private final AuctionDatabase database;

  /**
   * Constructs an AuctionProtectionProxy and initializes the underlying AuctionDatabase.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public AuctionProtectionProxy() throws SQLException, ClassNotFoundException
  {
    database = new AuctionDatabase();
  }

  /**
   * Saves an auction to the database with the specified details.
   *
   * @param title the title of the auction.
   * @param description the description of the auction.
   * @param reservePrice the reserve price of the auction.
   * @param buyoutPrice the buyout price of the auction.
   * @param minimumIncrement the minimum increment for bids.
   * @param auctionTime the duration of the auction in hours.
   * @param imageData the image data associated with the auction.
   * @param seller the email of the seller.
   * @return the created Auction object.
   * @throws SQLException if there is a database access error or the seller is a moderator.
   */
  @Override
  public Auction saveAuction(String title, String description, int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
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
    return database.saveAuction(title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData, seller);
  }

  /**
   * Retrieves an auction by its ID from the database.
   *
   * @param id the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public Auction getAuctionById(int id) throws SQLException
  {
    return database.getAuctionById(id);
  }

  /**
   * Marks an auction as closed in the database.
   *
   * @param id the identifier of the auction.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void markAsClosed(int id) throws SQLException
  {
    database.markAsClosed(id);
  }

  /**
   * Saves a notification to the database.
   *
   * @param content the content of the notification.
   * @param receiver the email of the receiver.
   * @return the created Notification object.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public Notification saveNotification(String content, String receiver) throws SQLException
  {
    return database.saveNotification(content, receiver);
  }

  /**
   * Saves a bid to the database.
   *
   * @param participantEmail the email of the participant placing the bid.
   * @param bidAmount the amount of the bid.
   * @param auctionId the identifier of the auction.
   * @return the created Bid object.
   * @throws SQLException if there is a database access error or the participant is a moderator.
   */
  @Override
  public Bid saveBid(String participantEmail, int bidAmount, int auctionId) throws SQLException
  {
    if (!isNotModerator(participantEmail))
      throw new SQLException("The moderator cannot place bids.");
    database.checkBid(bidAmount, participantEmail, auctionId);
    return database.saveBid(participantEmail, bidAmount, auctionId);
  }

  /**
   * Retrieves the current bid for a specified auction from the database.
   *
   * @param auctionId the identifier of the auction.
   * @return the current Bid object for the auction.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public Bid getCurrentBidForAuction(int auctionId) throws SQLException
  {
    return database.getCurrentBidForAuction(auctionId);
  }

  /**
   * Retrieves the information of a user by their email from the database.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public User getUserInfo(String email) throws SQLException
  {
    return database.getUserInfo(email);
  }

  /**
   * Executes a buyout for a specified auction in the database.
   *
   * @param bidder the email of the bidder.
   * @param auctionId the identifier of the auction.
   * @return the created Bid object for the buyout.
   * @throws SQLException if there is a database access error or the bidder is a moderator.
   */
  @Override
  public Bid buyout(String bidder, int auctionId) throws SQLException
  {
    if (!isNotModerator(bidder))
      throw new SQLException("You cannot participate in auctions.");
    database.checkBid(database.getAuctionById(auctionId).getPriceConstraint().getBuyoutPrice(), bidder, auctionId);
    return database.buyout(bidder, auctionId);
  }

  /**
   * Deletes an auction from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the deletion.
   * @param auctionId the identifier of the auction.
   * @param reason the reason for the deletion.
   * @throws SQLException if there is a database access error or the user is not a moderator.
   */
  @Override
  public void deleteAuction(String moderatorEmail, int auctionId, String reason) throws SQLException
  {
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot delete this item.");
    checkReason(reason);
    database.deleteAuction(moderatorEmail, auctionId, reason);
  }

  /**
   * Checks if a user is not a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is not a moderator, false otherwise.
   */
  private boolean isNotModerator(String email)
  {
    return !email.equals(super.getModeratorEmail());
  }

  /**
   * Checks the validity of the auction title.
   *
   * @param title the title of the auction.
   * @throws SQLException if the title is too long or too short.
   */
  private void checkTitle(String title) throws SQLException
  {
    int maxTitleLength = 80;
    int minTitleLength = 5;
    if (title.length() > maxTitleLength)
      throw new SQLException("The title is too long!");
    else if (title.length() < minTitleLength)
      throw new SQLException("The title is too short!");
  }

  /**
   * Checks the validity of the auction description.
   *
   * @param description the description of the auction.
   * @throws SQLException if the description is too long or too short.
   */
  private void checkDescription(String description) throws SQLException
  {
    int maxDescriptionLength = 1400, minDescriptionLength = 20;
    if (description.length() > maxDescriptionLength)
      throw new SQLException("The description is too long!");
    else if (description.length() < minDescriptionLength)
      throw new SQLException("The description is too short!");
  }

  /**
   * Checks the validity of the reserve price.
   *
   * @param reservePrice the reserve price of the auction.
   * @throws SQLException if the reserve price is not positive.
   */
  private void checkReservePrice(int reservePrice) throws SQLException
  {
    if (reservePrice <= 0)
      throw new SQLException("The reserve price must be a positive number!");
  }

  /**
   * Checks the validity of the buyout price.
   *
   * @param buyoutPrice the buyout price of the auction.
   * @param reservePrice the reserve price of the auction.
   * @throws SQLException if the buyout price is not greater than the reserve price.
   */
  private void checkBuyoutPrice(int buyoutPrice, int reservePrice) throws SQLException
  {
    if (buyoutPrice <= reservePrice)
      throw new SQLException("The buyout price must be greater than the reserve price!");
  }

  /**
   * Checks the validity of the minimum bid increment.
   *
   * @param minimumIncrement the minimum bid increment.
   * @throws SQLException if the minimum increment is less than 1.
   */
  private void checkMinimumIncrement(int minimumIncrement) throws SQLException
  {
    if (minimumIncrement < 1)
      throw new SQLException("The minimum bid increment must be at least 1!");
  }

  /**
   * Checks the validity of the auction time.
   *
   * @param auctionTime the duration of the auction in hours.
   * @throws SQLException if the auction time is not within the valid range.
   */
  private void checkAuctionTime(int auctionTime) throws SQLException
  {
    // to be updated when the moderator adds the time interval
    if (auctionTime <= 0 || auctionTime > 24)
      throw new SQLException("The auction time can be at most 24 hours!");
  }

  /**
   * Checks the validity of the reason for deleting an auction.
   *
   * @param reason the reason for the deletion.
   * @throws SQLException if the reason is too short or too long.
   */
  private void checkReason(String reason) throws SQLException
  {
    if (reason == null || reason.length() < 3)
      throw new SQLException("The reason is too short.");
    if (reason.length() > 600)
      throw new SQLException("The reason is too long.");
  }
}
