package mediator;

import model.domain.Auction;
import model.domain.AuctionList;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * The AuctionListRemote interface provides methods for managing auctions remotely.
 * It extends the Remote interface to support RMI (Remote Method Invocation).
 */
public interface AuctionListRemote extends Remote
{
  /**
   * Retrieves a list of ongoing auctions.
   *
   * @return a list of ongoing auctions.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getOngoingAuctions() throws RemoteException, SQLException;

  /**
   * Retrieves a list of previous bids made by a specific bidder.
   *
   * @param bidder the email or identifier of the bidder.
   * @return a list of previous bids made by the bidder.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getPreviousBids(String bidder) throws RemoteException, SQLException;

  /**
   * Retrieves a list of auctions created by a specific seller.
   *
   * @param seller the email or identifier of the seller.
   * @return a list of auctions created by the seller.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getCreatedAuctions(String seller) throws RemoteException, SQLException;

  /**
   * Retrieves a list of all auctions, accessible by a moderator.
   *
   * @param moderatorEmail the email of the moderator.
   * @return a list of all auctions.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  AuctionList getAllAuctions(String moderatorEmail) throws RemoteException, SQLException;

  /**
   * Retrieves a specific auction by its ID.
   *
   * @param ID the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  Auction getAuction(int ID) throws RemoteException, SQLException;

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  boolean isModerator(String email) throws RemoteException, SQLException;

  /**
   * Adds a listener for specific property changes.
   *
   * @param listener the listener to be added.
   * @param propertyNames the properties to listen for.
   * @return true if the listener was added successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  boolean addListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;

  /**
   * Removes a listener for specific property changes.
   *
   * @param listener the listener to be removed.
   * @param propertyNames the properties to stop listening for.
   * @return true if the listener was removed successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  boolean removeListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;
}
