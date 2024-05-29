package model;

import model.domain.Auction;
import model.domain.AuctionList;

import java.sql.SQLException;

/**
 * The AuctionListModel interface provides methods for managing auction lists.
 */
public interface AuctionListModel
{
  /**
   * Retrieves a list of ongoing auctions.
   *
   * @return a list of ongoing auctions.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getOngoingAuctions() throws SQLException;

  /**
   * Retrieves a list of previous bids made by a specific bidder.
   *
   * @param bidder the email or identifier of the bidder.
   * @return a list of previous bids made by the bidder.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getPreviousBids(String bidder) throws SQLException;

  /**
   * Retrieves a list of auctions created by a specific seller.
   *
   * @param seller the email or identifier of the seller.
   * @return a list of auctions created by the seller.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getCreatedAuctions(String seller) throws SQLException;

  /**
   * Retrieves a list of all auctions, accessible by a moderator.
   *
   * @param moderatorEmail the email of the moderator.
   * @return a list of all auctions.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getAllAuctions(String moderatorEmail) throws SQLException;

  /**
   * Retrieves a specific auction by its ID.
   *
   * @param ID the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  Auction getAuction(int ID) throws SQLException;

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  boolean isModerator(String email) throws SQLException;
}
