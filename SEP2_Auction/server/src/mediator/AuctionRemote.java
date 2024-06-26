package mediator;

import model.domain.Auction;
import model.domain.Bid;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * The AuctionRemote interface provides methods for managing individual auctions remotely.
 * It extends the Remote interface to support RMI (Remote Method Invocation).
 */
public interface AuctionRemote extends Remote
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
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  Auction startAuction(String title, String description, int reservePrice,
                       int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData,
                       String seller)
          throws RemoteException, SQLException, ClassNotFoundException;

  /**
   * Retrieves an auction by its ID.
   *
   * @param ID the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  Auction getAuction(int ID) throws RemoteException, SQLException;

  /**
   * Places a bid on a specified auction.
   *
   * @param bidder the email or identifier of the bidder.
   * @param bidValue the value of the bid.
   * @param auctionId the identifier of the auction.
   * @return the placed Bid object.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  Bid placeBid(String bidder, int bidValue, int auctionId) throws RemoteException, SQLException;

  /**
   * Executes a buyout for a specified auction.
   *
   * @param bidder the email or identifier of the bidder.
   * @param auctionId the identifier of the auction.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  void buyout(String bidder, int auctionId) throws RemoteException, SQLException;

  /**
   * Deletes an auction with the specified reason.
   *
   * @param moderatorEmail the email of the moderator requesting the deletion.
   * @param auctionId the identifier of the auction.
   * @param reason the reason for the deletion.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  void deleteAuction(String moderatorEmail, int auctionId, String reason) throws RemoteException, SQLException;

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
