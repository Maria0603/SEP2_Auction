package model;

import mediator.AuctionClient;
import model.domain.Auction;
import model.domain.Bid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 * The AuctionModelManager class implements the AuctionModel interface
 * and manages individual auctions. It acts as an intermediary between the client and the data model,
 * handling property change events and notifying listeners of changes.
 */
public class AuctionModelManager implements AuctionModel, PropertyChangeListener {

  private final PropertyChangeSupport property;
  private final AuctionClient client;

  /**
   * Constructs a new AuctionModelManager and initializes the client and listeners.
   *
   * @throws IOException if an I/O error occurs.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  public AuctionModelManager() throws IOException, IllegalArgumentException {
    property = new PropertyChangeSupport(this);
    client = new AuctionClient();
    client.addListener("End", this);
    client.addListener("Bid", this);
    client.addListener("Edit", this);
    client.addListener("Ban", this);
    client.addListener("DeleteAuction", this);
    client.addListener("DeleteAccount", this);
  }

  /**
   * Starts a new auction.
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
  public Auction startAuction(String title, String description, int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
          throws IllegalArgumentException, ClassNotFoundException {
    return client.startAuction(title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData, seller);
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
    return client.getAuction(ID);
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
  public Bid placeBid(String bidder, int bidValue, int auctionId) throws IllegalArgumentException {
    return client.placeBid(bidder, bidValue, auctionId);
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
    client.buyout(bidder, auctionId);
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
    client.deleteAuction(moderatorEmail, auctionId, reason);
  }

  /**
   * Adds a listener for property change events.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to add.
   */
  @Override
  public void addListener(String propertyName, PropertyChangeListener listener) {
    property.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a listener for property change events.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to remove.
   */
  @Override
  public void removeListener(String propertyName, PropertyChangeListener listener) {
    property.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param evt the property change event containing the property change information.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}
