package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The AuctionListCacheProxy class acts as a proxy for the AuctionListModel and implements caching
 * for auction-related data. It listens to changes from the AuctionListModelManager and updates
 * the cache accordingly.
 */
public class AuctionListCacheProxy extends CacheProxy implements AuctionListModel, PropertyChangeListener {

  private AuctionList ongoingAuctionsCache;
  private AuctionList allAuctionsCache;
  private AuctionList previousBidsCache;
  private AuctionList createdAuctionsCache;
  private final AuctionListModelManager modelManager;
  private final PropertyChangeSupport property;

  /**
   * Constructs a new AuctionListCacheProxy object and initializes the cache and listeners.
   *
   * @throws IllegalArgumentException if an illegal argument is provided.
   * @throws IOException if an I/O error occurs.
   */
  public AuctionListCacheProxy() throws IllegalArgumentException, IOException {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new AuctionListModelManager();
    modelManager.addListener("Auction", this);
    modelManager.addListener("End", this);
    modelManager.addListener("Bid", this);
    modelManager.addListener("Edit", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("DeleteAuction", this);
    modelManager.addListener("DeleteAccount", this);

    ongoingAuctionsCache = new AuctionList();
    allAuctionsCache = new AuctionList();
    previousBidsCache = new AuctionList();
    createdAuctionsCache = new AuctionList();

    super.getUserEmail().addListener((observable, oldValue, newValue) -> {
      try {
        updateCache(userEmail.get());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Retrieves the list of ongoing auctions.
   *
   * @return the list of ongoing auctions.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public AuctionList getOngoingAuctions() throws IllegalArgumentException {
    if (ongoingAuctionsCache.getSize() == 0)
      ongoingAuctionsCache = modelManager.getOngoingAuctions();
    return ongoingAuctionsCache;
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
    if (previousBidsCache.getSize() == 0)
      previousBidsCache = modelManager.getPreviousBids(bidder);
    return previousBidsCache;
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
    if (createdAuctionsCache.getSize() == 0)
      createdAuctionsCache = modelManager.getCreatedAuctions(seller);
    return createdAuctionsCache;
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
    if (allAuctionsCache.getSize() == 0)
      allAuctionsCache = modelManager.getAllAuctions(moderatorEmail);
    return allAuctionsCache;
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
    return modelManager.getAuction(ID);
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
    return modelManager.isModerator(email);
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
   * Updates the cache by fetching data from the model manager.
   *
   * @param userEmail the email of the user.
   * @throws SQLException if a database access error occurs.
   */
  private void updateCache(String userEmail) throws SQLException {
    createdAuctionsCache = modelManager.getCreatedAuctions(userEmail);
    previousBidsCache = modelManager.getPreviousBids(userEmail);
    ongoingAuctionsCache = modelManager.getOngoingAuctions();
    if (isModerator(userEmail))
      allAuctionsCache = modelManager.getAllAuctions(userEmail);
  }

  /**
   * Updates the bid information in the cache.
   *
   * @param bid the bid to update.
   * @param cache the cache to update.
   */
  private void updateBidIn(Bid bid, AuctionList cache) {
    if (cache.contains(bid.getAuctionId())) {
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
    }
  }

  /**
   * Handles the "Auction" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedAuction(PropertyChangeEvent evt) {
    Auction auction = (Auction) evt.getNewValue();
    ongoingAuctionsCache.addAuction(auction);
    allAuctionsCache.addAuction(auction);

    if (super.getUserEmail().equals(auction.getSeller()))
      createdAuctionsCache.addAuction(auction);
  }

  /**
   * Handles the "End" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedEnd(PropertyChangeEvent evt) {
    int auctionId = Integer.parseInt(evt.getOldValue().toString());
    ongoingAuctionsCache.removeAuction(auctionId);

    if (evt.getNewValue() instanceof Bid) {
      Bid buyout = (Bid) evt.getNewValue();
      Auction auction = null;
      try {
        auction = getAuction(buyout.getAuctionId());
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
      if (buyout.getBidder().equals(super.getUserEmail()))
        if (auction != null)
          previousBidsCache.addAuction(auction);
      updateBidIn(buyout, allAuctionsCache);

      if (auction != null && auction.getSeller().equals(super.getUserEmail()))
        updateBidIn(buyout, createdAuctionsCache);
    }
  }

  /**
   * Handles the "Bid" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedBid(PropertyChangeEvent evt) {
    // we receive a bid
    Bid bid = (Bid) evt.getNewValue();

    // obviously, for an ongoing auction, so we update the cache
    updateBidIn(bid, ongoingAuctionsCache);
    updateBidIn(bid, allAuctionsCache);

    // if we placed the bid, we add the auction in cache
    if (!previousBidsCache.contains(bid.getAuctionId()) && bid.getBidder().equals(super.getUserEmail())) {
      try {
        previousBidsCache.addAuction(getAuction(bid.getAuctionId()));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    } else {
      // if someone else placed a bid for an auction where we previously bid
      // we update the cache
      updateBidIn(bid, previousBidsCache);
    }

    updateBidIn(bid, createdAuctionsCache);
  }

  /**
   * Handles the "Edit" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedEdit(PropertyChangeEvent evt) {
    if (super.getUserEmail().equals(evt.getOldValue().toString()))
      super.setUserEmail(evt.getNewValue().toString());
    try {
      updateCache(super.getUserEmail().get());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles the "Ban" or "DeleteAccount" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedBanOrDeleteAccount(PropertyChangeEvent evt) {
    try {
      updateCache(super.getUserEmail().get());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles the "DeleteAuction" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedDeleteAuction(PropertyChangeEvent evt) {
    int id = Integer.parseInt(evt.getNewValue().toString());
    ongoingAuctionsCache.removeAuction(id);
    previousBidsCache.removeAuction(id);
    createdAuctionsCache.removeAuction(id);
    allAuctionsCache.removeAuction(id);
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param evt the property change event containing the property change information.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "Auction" -> receivedAuction(evt);
      case "End" -> receivedEnd(evt);
      case "Bid" -> receivedBid(evt);
      case "Edit" -> receivedEdit(evt);
      case "Ban", "DeleteAccount" -> receivedBanOrDeleteAccount(evt);
      case "DeleteAuction" -> receivedDeleteAuction(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
  }
}
