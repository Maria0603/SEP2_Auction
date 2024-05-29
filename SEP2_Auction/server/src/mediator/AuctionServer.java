package mediator;

import model.*;
import model.domain.*;
import utility.observer.listener.GeneralListener;
import utility.observer.subject.PropertyChangeHandler;
import utility.observer.subject.RemoteSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

/**
 * The AuctionServer class implements the AuctionRemote and RemoteSubject interfaces,
 * and serves as a remote server for managing individual auctions.
 */
public class AuctionServer implements AuctionRemote, RemoteSubject<String, Object>, PropertyChangeListener
{
  private final AuctionModel model;
  private final PropertyChangeHandler<String, Object> property;

  /**
   * Constructs an AuctionServer with the specified model and listener subject.
   *
   * @param model the auction model.
   * @param listenerSubject the listener subject interface.
   * @throws MalformedURLException if the provided URL is malformed.
   * @throws RemoteException if there is an RMI error.
   */
  public AuctionServer(AuctionModel model, ListenerSubjectInterface listenerSubject)
          throws MalformedURLException, RemoteException
  {
    this.model = model;
    property = new PropertyChangeHandler<>(this, true);

    listenerSubject.addListener("End", this);
    listenerSubject.addListener("Bid", this);
    listenerSubject.addListener("DeleteAuction", this);
    listenerSubject.addListener("Ban", this);
    listenerSubject.addListener("Edit", this);
    listenerSubject.addListener("DeleteAccount", this);

    startServer();
  }

  /**
   * Starts the server and binds the AuctionRemote object.
   *
   * @throws RemoteException if there is an RMI error.
   * @throws MalformedURLException if the provided URL is malformed.
   */
  private synchronized void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("AuctionRemote", this);
  }

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
  @Override
  public synchronized Auction startAuction(String title, String description, int reservePrice,
                                           int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
          throws RemoteException, SQLException, ClassNotFoundException
  {
    return model.startAuction(title, description, reservePrice, buyoutPrice,
            minimumIncrement, auctionTime, imageData, seller);
  }

  /**
   * Retrieves an auction by its ID.
   *
   * @param id the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized Auction getAuction(int id) throws RemoteException, SQLException
  {
    return model.getAuction(id);
  }

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
  @Override
  public synchronized Bid placeBid(String bidder, int bidValue, int auctionId) throws RemoteException, SQLException
  {
    return model.placeBid(bidder, bidValue, auctionId);
  }

  /**
   * Executes a buyout for a specified auction.
   *
   * @param bidder the email or identifier of the bidder.
   * @param auctionId the identifier of the auction.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized void buyout(String bidder, int auctionId) throws RemoteException, SQLException
  {
    model.buyout(bidder, auctionId);
  }

  /**
   * Deletes an auction with the specified reason.
   *
   * @param moderatorEmail the email of the moderator requesting the deletion.
   * @param auctionId the identifier of the auction.
   * @param reason the reason for the deletion.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized void deleteAuction(String moderatorEmail, int auctionId, String reason) throws RemoteException, SQLException
  {
    model.deleteAuction(moderatorEmail, auctionId, reason);
  }

  /**
   * Adds a listener for specific property changes.
   *
   * @param listener the listener to be added.
   * @param propertyNames the properties to listen for.
   * @return true if the listener was added successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  @Override
  public synchronized boolean addListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException
  {
    return property.addListener(listener, propertyNames);
  }

  /**
   * Removes a listener for specific property changes.
   *
   * @param listener the listener to be removed.
   * @param propertyNames the properties to stop listening for.
   * @return true if the listener was removed successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  @Override
  public synchronized boolean removeListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException
  {
    return property.removeListener(listener, propertyNames);
  }

  /**
   * Handles property change events and fires property change notifications.
   *
   * @param evt the property change event.
   */
  @Override
  public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    property.firePropertyChange(evt.getPropertyName(), String.valueOf(evt.getOldValue()), evt.getNewValue());
  }
}
