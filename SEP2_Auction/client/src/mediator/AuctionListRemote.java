package mediator;

import model.domain.Auction;
import model.domain.AuctionList;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * The AuctionListRemote interface defines the remote methods that can be called
 * to interact with the auction list on the server.
 */
public interface AuctionListRemote extends Remote {

  /**
   * Retrieves the list of ongoing auctions from the server.
   *
   * @return the list of ongoing auctions.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  AuctionList getOngoingAuctions() throws RemoteException, SQLException;

  /**
   * Retrieves the list of previous bids by a bidder from the server.
   *
   * @param bidder the name of the bidder.
   * @return the list of previous bids.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  AuctionList getPreviousBids(String bidder) throws RemoteException, SQLException;

  /**
   * Retrieves the list of auctions created by a seller from the server.
   *
   * @param seller the name of the seller.
   * @return the list of created auctions.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  AuctionList getCreatedAuctions(String seller) throws RemoteException, SQLException;

  /**
   * Retrieves the list of all auctions for a moderator from the server.
   *
   * @param moderatorEmail the email of the moderator.
   * @return the list of all auctions.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  AuctionList getAllAuctions(String moderatorEmail) throws RemoteException, SQLException;

  /**
   * Retrieves an auction by its ID.
   *
   * @param ID the ID of the auction.
   * @return the Auction with the specified ID.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  Auction getAuction(int ID) throws RemoteException, SQLException;

  /**
   * Checks if a user is a moderator.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  boolean isModerator(String email) throws RemoteException, SQLException;

  /**
   * Adds a listener for specific property change events.
   *
   * @param listener the listener to add.
   * @param propertyNames the names of the properties to listen for.
   * @return true if the listener was successfully added, false otherwise.
   * @throws RemoteException if a remote communication error occurs.
   */
  boolean addListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;

  /**
   * Removes a listener for specific property change events.
   *
   * @param listener the listener to remove.
   * @param propertyNames the names of the properties to stop listening for.
   * @return true if the listener was successfully removed, false otherwise.
   * @throws RemoteException if a remote communication error occurs.
   */
  boolean removeListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;
}
