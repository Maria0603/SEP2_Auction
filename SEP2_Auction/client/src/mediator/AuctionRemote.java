package mediator;

import model.domain.Auction;
import model.domain.Bid;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * The AuctionRemote interface defines the remote methods that can be called 
 * to interact with individual auctions on the server.
 */
public interface AuctionRemote extends Remote {

  /**
   * Starts an auction on the server.
   *
   * @param title the title of the auction.
   * @param description the description of the auction.
   * @param reservePrice the reserve price of the auction.
   * @param buyoutPrice the buyout price of the auction.
   * @param minimumIncrement the minimum increment for bids.
   * @param auctionTime the duration of the auction.
   * @param imageData the image data associated with the auction.
   * @param seller the seller of the auction.
   * @return the started Auction.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   * @throws ClassNotFoundException if a class is not found.
   */
  Auction startAuction(String title, String description, int reservePrice,
                       int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData,
                       String seller) throws RemoteException, SQLException, ClassNotFoundException;

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
   * Places a bid on an auction.
   *
   * @param bidder the bidder's name.
   * @param bidValue the value of the bid.
   * @param auctionId the ID of the auction.
   * @return the placed Bid.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  Bid placeBid(String bidder, int bidValue, int auctionId) throws RemoteException, SQLException;

  /**
   * Performs a buyout on an auction.
   *
   * @param bidder the bidder's name.
   * @param auctionId the ID of the auction.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  void buyout(String bidder, int auctionId) throws RemoteException, SQLException;

  /**
   * Deletes an auction.
   *
   * @param moderatorEmail the email of the moderator.
   * @param auctionId the ID of the auction.
   * @param reason the reason for deleting the auction.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  void deleteAuction(String moderatorEmail, int auctionId, String reason) throws RemoteException, SQLException;

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
