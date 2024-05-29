package model;

import model.domain.Auction;
import model.domain.Bid;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;

/**
 * The AuctionModel interface provides methods for managing individual auctions and bids.
 * It extends the NamedPropertyChangeSubject to support property change notifications.
 */
public interface AuctionModel extends NamedPropertyChangeSubject
{
  /**
   * Starts a new auction with the specified details.
   *
   * @param title the title of the auction.
   * @param description the description of the auction.
   * @param reservePrice the reserve price of the auction.
   * @param buyoutPrice the buyout price of the auction.
   * @param minimumIncrement the minimum increment for bids.
   * @param auctionTime the duration of the auction in minutes.
   * @param imageData the image data associated with the auction.
   * @param seller the seller of the auction.
   * @return the created Auction object.
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  Auction startAuction(String title, String description, int reservePrice,
                       int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData,
                       String seller) throws SQLException, ClassNotFoundException;

  /**
   * Retrieves a specific auction by its ID.
   *
   * @param ID the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  Auction getAuction(int ID) throws SQLException;

  /**
   * Places a bid on a specified auction.
   *
   * @param bidder the email or identifier of the bidder.
   * @param bidValue the value of the bid.
   * @param auctionId the identifier of the auction.
   * @return the placed Bid object.
   * @throws SQLException if there is a database access error.
   */
  Bid placeBid(String bidder, int bidValue, int auctionId) throws SQLException;

  /**
   * Executes a buyout for a specified auction.
   *
   * @param bidder the email or identifier of the bidder.
   * @param auctionId the identifier of the auction.
   * @throws SQLException if there is a database access error.
   */
  void buyout(String bidder, int auctionId) throws SQLException;

  /**
   * Deletes an auction with the specified reason.
   *
   * @param moderatorEmail the email of the moderator requesting the deletion.
   * @param auctionId the identifier of the auction.
   * @param reason the reason for the deletion.
   * @throws SQLException if there is a database access error.
   */
  void deleteAuction(String moderatorEmail, int auctionId, String reason) throws SQLException;
}
