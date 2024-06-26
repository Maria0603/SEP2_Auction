package persistence;

import model.domain.*;

import java.sql.SQLException;

/**
 * The AuctionPersistence interface provides methods for managing and persisting auction-related data.
 */
public interface AuctionPersistence
{
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
   * @throws SQLException if there is a database access error.
   */
  Auction saveAuction(String title, String description, int reservePrice,
                      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData,
                      String seller) throws SQLException;

  /**
   * Retrieves an auction by its ID from the database.
   *
   * @param id the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  Auction getAuctionById(int id) throws SQLException;

  /**
   * Marks an auction as closed in the database.
   *
   * @param id the identifier of the auction.
   * @throws SQLException if there is a database access error.
   */
  void markAsClosed(int id) throws SQLException;

  /**
   * Saves a notification to the database.
   *
   * @param content the content of the notification.
   * @param receiver the email of the receiver.
   * @return the created Notification object.
   * @throws SQLException if there is a database access error.
   */
  Notification saveNotification(String content, String receiver)
          throws SQLException;

  /**
   * Saves a bid to the database.
   *
   * @param participantEmail the email of the participant placing the bid.
   * @param bidAmount the amount of the bid.
   * @param auctionId the identifier of the auction.
   * @return the created Bid object.
   * @throws SQLException if there is a database access error.
   */
  Bid saveBid(String participantEmail, int bidAmount, int auctionId)
          throws SQLException;

  /**
   * Retrieves the current bid for a specified auction from the database.
   *
   * @param auctionId the identifier of the auction.
   * @return the current Bid object for the auction.
   * @throws SQLException if there is a database access error.
   */
  Bid getCurrentBidForAuction(int auctionId) throws SQLException;

  /**
   * Retrieves the information of a user by their email from the database.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  User getUserInfo(String email) throws SQLException;

  /**
   * Executes a buyout for a specified auction in the database.
   *
   * @param bidder the email of the bidder.
   * @param auctionId the identifier of the auction.
   * @return the created Bid object for the buyout.
   * @throws SQLException if there is a database access error.
   */
  Bid buyout(String bidder, int auctionId) throws SQLException;

  /**
   * Deletes an auction from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the deletion.
   * @param auctionId the identifier of the auction.
   * @param reason the reason for the deletion.
   * @throws SQLException if there is a database access error.
   */
  void deleteAuction(String moderatorEmail, int auctionId, String reason)
          throws SQLException;
}
