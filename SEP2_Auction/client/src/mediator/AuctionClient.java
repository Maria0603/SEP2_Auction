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
 * The AuctionClient class implements the AuctionModel interface and acts as a client
 * to interact with the AuctionRemote server. It also implements RemoteListener to
 * listen for remote events.
 */
public class AuctionClient implements RemoteListener<String, Object>, AuctionModel {
  private AuctionRemote server;
  private final PropertyChangeSupport property;

  /**
   * Constructs an AuctionClient and starts the connection to the server.
   *
   * @throws IOException if an I/O error occurs.
   */
  public AuctionClient() throws IOException {
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
      server = (AuctionRemote) Naming.lookup("rmi://localhost:1099/AuctionRemote");

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
   * @throws IllegalArgumentException if an illegal argument is provided.
   * @throws ClassNotFoundException if a class is not found.
   */
  @Override
  public Auction startAuction(String title, String description, int reservePrice, int buyoutPrice,
                              int minimumIncrement, int auctionTime, byte[] imageData, String seller)
          throws IllegalArgumentException, ClassNotFoundException {
    try {
      return server.startAuction(title, description, reservePrice, buyoutPrice,
              minimumIncrement, auctionTime, imageData, seller);
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
   * Places a bid on an auction.
   *
   * @param bidder the bidder's name.
   * @param bidValue the value of the bid.
   * @param auctionId the ID of the auction.
   * @return the placed Bid.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public Bid placeBid(String bidder, int bidValue, int auctionId)
          throws IllegalArgumentException {
    try {
      return server.placeBid(bidder, bidValue, auctionId);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Performs a buyout on an auction.
   *
   * @param bidder the bidder's name.
   * @param auctionId the ID of the auction.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void buyout(String bidder, int auctionId) throws IllegalArgumentException {
    try {
      server.buyout(bidder, auctionId);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Deletes an auction.
   *
   * @param moderatorEmail the email of the moderator.
   * @param auctionId the ID of the auction.
   * @param reason the reason for deleting the auction.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void deleteAuction(String moderatorEmail, int auctionId, String reason) throws IllegalArgumentException {
    try {
      server.deleteAuction(moderatorEmail, auctionId, reason);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
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
