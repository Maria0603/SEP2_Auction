package mediator;

import model.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import model.domain.*;
import utility.observer.event.ObserverEvent;
import utility.observer.listener.RemoteListener;

/**
 * The AuctionListClient class implements the AuctionListModel interface and acts as a client
 * to interact with the AuctionListRemote server. It also implements RemoteListener to
 * listen for remote events.
 */
public class AuctionListClient implements RemoteListener<String, Object>, AuctionListModel {
  private AuctionListRemote server;
  private final PropertyChangeSupport property;

  /**
   * Constructs an AuctionListClient and starts the connection to the server.
   *
   * @throws IOException if an I/O error occurs.
   */
  public AuctionListClient() throws IOException {
    start();
    property = new PropertyChangeSupport(this);
  }

  /**
   * Establishes a connection to the server and registers the client as a listener
   * for various events.
   */
  private void start() {
    try {
      UnicastRemoteObject.exportObject(this, 0);
      server = (AuctionListRemote) Naming.lookup("rmi://localhost:1099/AuctionListRemote");

      server.addListener(this, "Auction");
      server.addListener(this, "End");
      server.addListener(this, "Bid");
      server.addListener(this, "Edit");
      server.addListener(this, "Ban");
      server.addListener(this, "DeleteAuction");
      server.addListener(this, "DeleteAccount");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves the list of ongoing auctions from the server.
   *
   * @return the list of ongoing auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getOngoingAuctions() throws IllegalArgumentException {
    try {
      return server.getOngoingAuctions();
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Retrieves the list of previous bids by a bidder from the server.
   *
   * @param bidder the name of the bidder.
   * @return the list of previous bids.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getPreviousBids(String bidder) throws IllegalArgumentException {
    try {
      return server.getPreviousBids(bidder);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Retrieves the list of auctions created by a seller from the server.
   *
   * @param seller the name of the seller.
   * @return the list of created auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getCreatedAuctions(String seller) throws IllegalArgumentException {
    try {
      return server.getCreatedAuctions(seller);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Retrieves the list of all auctions for a moderator from the server.
   *
   * @param moderatorEmail the email of the moderator.
   * @return the list of all auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getAllAuctions(String moderatorEmail) throws IllegalArgumentException {
    try {
      return server.getAllAuctions(moderatorEmail);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Retrieves an auction by its ID.
   *
   * @param ID the ID of the auction.
   * @return the Auction with the specified ID.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public Auction getAuction(int ID) throws IllegalArgumentException {
    try {
      return server.getAuction(ID);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Checks if a user is a moderator.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public boolean isModerator(String email) throws IllegalArgumentException {
    try {
      return server.isModerator(email);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return false;
  }

  /**
   * Adds a listener for property change events.
   *
   * @param s the property name.
   * @param propertyChangeListener the listener to add.
   */
  @Override
  public void addListener(String s, PropertyChangeListener propertyChangeListener) {
    property.addPropertyChangeListener(s, propertyChangeListener);
  }

  /**
   * Removes a listener for property change events.
   *
   * @param s the property name.
   * @param propertyChangeListener the listener to remove.
   */
  @Override
  public void removeListener(String s, PropertyChangeListener propertyChangeListener) {
    property.removePropertyChangeListener(s, propertyChangeListener);
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param event the observer event containing the property change information.
   * @throws RemoteException if a remote communication error occurs.
   */
  @Override
  public void propertyChange(ObserverEvent<String, Object> event) throws RemoteException {
    property.firePropertyChange(event.getPropertyName(), event.getValue1(), event.getValue2());
  }
}
