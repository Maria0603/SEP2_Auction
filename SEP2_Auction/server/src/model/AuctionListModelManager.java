package model;

import model.domain.*;
import persistence.AuctionListPersistence;
import persistence.AuctionListProtectionProxy;
import java.sql.SQLException;

/**
 * The AuctionListModelManager class implements the AuctionListModel interface
 * and provides methods for managing auction lists using a persistence layer.
 */
public class AuctionListModelManager implements AuctionListModel
{
  private final AuctionListPersistence auctionListDatabase;

  /**
   * Constructs an AuctionListModelManager and initializes the persistence layer.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public AuctionListModelManager() throws SQLException, ClassNotFoundException
  {
    auctionListDatabase = new AuctionListProtectionProxy();
  }

  /**
   * Retrieves a specific auction by its ID.
   *
   * @param ID the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized Auction getAuction(int ID) throws SQLException
  {
    return auctionListDatabase.getAuctionById(ID);
  }

  /**
   * Retrieves a list of ongoing auctions.
   *
   * @return a list of ongoing auctions.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getOngoingAuctions() throws SQLException
  {
    return auctionListDatabase.getOngoingAuctions();
  }

  /**
   * Retrieves a list of previous bids made by a specific bidder.
   *
   * @param bidder the email or identifier of the bidder.
   * @return a list of previous bids made by the bidder.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getPreviousBids(String bidder) throws SQLException
  {
    return auctionListDatabase.getPreviousBids(bidder);
  }

  /**
   * Retrieves a list of auctions created by a specific seller.
   *
   * @param seller the email or identifier of the seller.
   * @return a list of auctions created by the seller.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getCreatedAuctions(String seller) throws SQLException
  {
    return auctionListDatabase.getCreatedAuctions(seller);
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
    return auctionListDatabase.isModerator(email);
  }

  /**
   * Retrieves a list of all auctions, accessible by a moderator.
   *
   * @param moderatorEmail the email of the moderator.
   * @return a list of all auctions.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getAllAuctions(String moderatorEmail) throws SQLException
  {
    return auctionListDatabase.getAllAuctions(moderatorEmail);
  }
}
