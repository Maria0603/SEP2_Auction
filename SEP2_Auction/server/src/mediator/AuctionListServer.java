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
 * The AuctionListServer class implements the AuctionListRemote and RemoteSubject interfaces,
 * and serves as a remote server for managing auctions.
 */
public class AuctionListServer implements AuctionListRemote, RemoteSubject<String, Object>, PropertyChangeListener
{
  private final AuctionListModel model;
  private final PropertyChangeHandler<String, Object> property;

  /**
   * Constructs an AuctionListServer with the specified model and listener subject.
   *
   * @param model the auction list model.
   * @param listenerSubject the listener subject interface.
   * @throws MalformedURLException if the provided URL is malformed.
   * @throws RemoteException if there is an RMI error.
   */
  public AuctionListServer(AuctionListModel model, ListenerSubjectInterface listenerSubject)
          throws MalformedURLException, RemoteException
  {
    this.model = model;
    property = new PropertyChangeHandler<>(this, true);

    listenerSubject.addListener("Auction", this);
    listenerSubject.addListener("End", this);
    listenerSubject.addListener("Bid", this);
    listenerSubject.addListener("DeleteAuction", this);
    listenerSubject.addListener("Ban", this);
    listenerSubject.addListener("Edit", this);
    listenerSubject.addListener("DeleteAccount", this);

    startServer();
  }

  /**
   * Starts the server and binds the AuctionListRemote object.
   *
   * @throws RemoteException if there is an RMI error.
   * @throws MalformedURLException if the provided URL is malformed.
   */
  private synchronized void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("AuctionListRemote", this);
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
   * Retrieves a list of ongoing auctions.
   *
   * @return a list of ongoing auctions.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getOngoingAuctions() throws RemoteException, SQLException
  {
    return model.getOngoingAuctions();
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
    return model.getPreviousBids(bidder);
  }

  /**
   * Retrieves a list of auctions created by a specific seller.
   *
   * @param seller the email or identifier of the seller.
   * @return a list of auctions created by the seller.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized AuctionList getCreatedAuctions(String seller) throws RemoteException, SQLException
  {
    return model.getCreatedAuctions(seller);
  }

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized boolean isModerator(String email) throws RemoteException, SQLException
  {
    return model.isModerator(email);
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
    return model.getAllAuctions(moderatorEmail);
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
