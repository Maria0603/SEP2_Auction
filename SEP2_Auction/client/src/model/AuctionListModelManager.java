package model;

import mediator.AuctionListClient;
import model.domain.Auction;
import model.domain.AuctionList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 * The AuctionListModelManager class implements the AuctionListModel interface
 * and manages auction lists. It acts as an intermediary between the client and the data model,
 * handling property change events and notifying listeners of changes.
 */
public class AuctionListModelManager implements AuctionListModel, PropertyChangeListener {

  private final PropertyChangeSupport property;
  private final AuctionListClient client;

  /**
   * Constructs a new AuctionListModelManager and initializes the client and listeners.
   *
   * @throws IOException if an I/O error occurs.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  public AuctionListModelManager() throws IOException, IllegalArgumentException {
    property = new PropertyChangeSupport(this);
    client = new AuctionListClient();
    client.addListener("Auction", this);
    client.addListener("End", this);
    client.addListener("Bid", this);
    client.addListener("Edit", this);
    client.addListener("Ban", this);
    client.addListener("DeleteAuction", this);
    client.addListener("DeleteAccount", this);
  }

  /**
   * Retrieves the list of ongoing auctions.
   *
   * @return the list of ongoing auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getOngoingAuctions() throws IllegalArgumentException {
    return client.getOngoingAuctions();
  }

  /**
   * Retrieves the list of previous bids by a bidder.
   *
   * @param bidder the name of the bidder.
   * @return the list of previous bids.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getPreviousBids(String bidder) throws IllegalArgumentException {
    return client.getPreviousBids(bidder);
  }

  /**
   * Retrieves the list of auctions created by a seller.
   *
   * @param seller the name of the seller.
   * @return the list of created auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getCreatedAuctions(String seller) throws IllegalArgumentException {
    return client.getCreatedAuctions(seller);
  }

  /**
   * Retrieves the list of all auctions for a moderator.
   *
   * @param moderatorEmail the email of the moderator.
   * @return the list of all auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getAllAuctions(String moderatorEmail) throws IllegalArgumentException {
    return client.getAllAuctions(moderatorEmail);
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
   * Checks if a user is a moderator.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public boolean isModerator(String email) throws IllegalArgumentException {
    return client.isModerator(email);
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param evt the property change event containing the property change information.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    property.firePropertyChange(evt);
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
}
