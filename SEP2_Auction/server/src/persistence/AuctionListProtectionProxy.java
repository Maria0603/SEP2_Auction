package persistence;

import model.domain.*;

import java.sql.*;

/**
 * The AuctionListProtectionProxy class implements the AuctionListPersistence interface
 * and acts as a protection proxy for the AuctionListDatabase, providing access control for auction data.
 */
public class AuctionListProtectionProxy extends DatabasePersistence implements AuctionListPersistence
{
  private final AuctionListDatabase database;

  /**
   * Constructs an AuctionListProtectionProxy and initializes the underlying AuctionListDatabase.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public AuctionListProtectionProxy() throws SQLException, ClassNotFoundException
  {
    database = new AuctionListDatabase();
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
   * Retrieves a list of ongoing auctions from the database.
   *
   * @return a list of ongoing auctions.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public AuctionList getOngoingAuctions() throws SQLException
  {
    return database.getOngoingAuctions();
  }

  /**
   * Retrieves a list of previous bids made by a specific bidder from the database.
   *
   * @param bidder the email of the bidder.
   * @return a list of previous bids made by the bidder.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public AuctionList getPreviousBids(String bidder) throws SQLException
  {
    return database.getPreviousBids(bidder);
  }

  /**
   * Retrieves a list of auctions created by a specific seller from the database.
   *
   * @param seller the email of the seller.
   * @return a list of auctions created by the seller.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public AuctionList getCreatedAuctions(String seller) throws SQLException
  {
    return database.getCreatedAuctions(seller);
  }

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public boolean isModerator(String email) throws SQLException
  {
    return database.isModerator(email);
  }

  /**
   * Retrieves a list of all auctions from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the auctions.
   * @return a list of all auctions.
   * @throws SQLException if there is a database access error or if the user is not a moderator.
   */
  @Override
  public AuctionList getAllAuctions(String moderatorEmail) throws SQLException
  {
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot access all auctions.");
    return database.getAllAuctions(moderatorEmail);
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
}
