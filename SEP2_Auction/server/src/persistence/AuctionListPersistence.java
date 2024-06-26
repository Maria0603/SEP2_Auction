package persistence;

import model.domain.*;

import java.sql.SQLException;

/**
 * The AuctionListPersistence interface provides methods for managing and retrieving auction lists from the database.
 */
public interface AuctionListPersistence
{
  /**
   * Retrieves an auction by its ID from the database.
   *
   * @param id the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  Auction getAuctionById(int id) throws SQLException;

  /**
   * Retrieves a list of ongoing auctions from the database.
   *
   * @return a list of ongoing auctions.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getOngoingAuctions() throws SQLException;

  /**
   * Retrieves a list of previous bids made by a specific bidder from the database.
   *
   * @param bidder the email of the bidder.
   * @return a list of previous bids made by the bidder.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getPreviousBids(String bidder) throws SQLException;

  /**
   * Retrieves a list of auctions created by a specific seller from the database.
   *
   * @param seller the email of the seller.
   * @return a list of auctions created by the seller.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getCreatedAuctions(String seller) throws SQLException;

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  boolean isModerator(String email) throws SQLException;

  /**
   * Retrieves a list of all auctions from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the auctions.
   * @return a list of all auctions.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getAllAuctions(String moderatorEmail) throws SQLException;
}
