package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

/**
 * The AuctionCacheProxy class acts as a proxy for the AuctionModel and implements caching
 * for auction-related data. It listens to changes from the AuctionModelManager and updates
 * the cache accordingly.
 */
public class AuctionCacheProxy extends CacheProxy implements AuctionModel, PropertyChangeListener {

  private final AuctionModelManager modelManager;
  private final PropertyChangeSupport property;
  private AuctionList previousOpenedAuctions;

  /**
   * Constructs a new AuctionCacheProxy object and initializes the cache and listeners.
   *
   * @throws IllegalArgumentException if an illegal argument is provided.
   * @throws IOException if an I/O error occurs.
   */
  public AuctionCacheProxy() throws IllegalArgumentException, IOException {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new AuctionModelManager();
    modelManager.addListener("End", this);
    modelManager.addListener("Bid", this);
    modelManager.addListener("Edit", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("DeleteAuction", this);
    modelManager.addListener("DeleteAccount", this);

    previousOpenedAuctions = new AuctionList();
    super.getUserEmail().addListener((observable, oldValue, newValue) -> updateCache());
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
    return modelManager.startAuction(title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData, seller);
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
    if (previousOpenedAuctions.contains(ID))
      return previousOpenedAuctions.getAuctionByID(ID);
    Auction auction = modelManager.getAuction(ID);
    startTimer(auction);
    previousOpenedAuctions.addAuction(auction);
    return auction;
  }

  /**
   * Starts a timer for the specified auction.
   *
   * @param auction the auction to start the timer for.
   */
  private void startTimer(Auction auction) {
    if (auction.getStatus().equals("ONGOING")) {
      Timer timer = new Timer(timeLeft(Time.valueOf(LocalTime.now()), auction.getEndTime()) - 1, auction.getID());
      timer.addListener("Time", this);
      Thread t = new Thread(timer, String.valueOf(auction.getID()));
      t.start();
    }
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
    return modelManager.placeBid(bidder, bidValue, auctionId);
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
    modelManager.buyout(bidder, auctionId);
  }

  /**
   * Calculates the time left for the auction.
   *
   * @param currentTime the current time.
   * @param end the end time of the auction.
   * @return the time left for the auction in seconds.
   */
  private long timeLeft(Time currentTime, Time end) {
    long currentSeconds = currentTime.toLocalTime().toSecondOfDay();
    long endSeconds = end.toLocalTime().toSecondOfDay();
    if (currentSeconds >= endSeconds)
      return 60 * 60 * 24 - (currentSeconds - endSeconds);
    else
      return endSeconds - currentSeconds;
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
    modelManager.deleteAuction(moderatorEmail, auctionId, reason);
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
   * Handles the "End" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedEnd(PropertyChangeEvent evt) {
    int auctionId = Integer.parseInt(evt.getOldValue().toString());
    if (previousOpenedAuctions.contains(auctionId))
      previousOpenedAuctions.getAuctionByID(auctionId).setStatus("CLOSED");

    if (evt.getNewValue() instanceof Bid) {
      Bid buyout = (Bid) evt.getNewValue();
      updateBidIn(buyout, previousOpenedAuctions);
    }
  }

  /**
   * Handles the "Bid" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedBid(PropertyChangeEvent evt) {
    Bid bid = (Bid) evt.getNewValue();
    updateBidIn(bid, previousOpenedAuctions);
  }

  /**
   * Updates the cache by clearing previous opened auctions.
   */
  private void updateCache() {
    previousOpenedAuctions = new AuctionList();
  }

  /**
   * Handles the "Edit" event and updates the cache accordingly.
   */
  private void receivedEdit() {
    updateCache();
  }

  /**
   * Handles the "Ban" or "DeleteAccount" event and updates the cache accordingly.
   */
  private void receivedBanOrDeleteAccount() {
    updateCache();
  }

  /**
   * Handles the "DeleteAuction" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedDeleteAuction(PropertyChangeEvent evt) {
    int id = Integer.parseInt(evt.getNewValue().toString());
    previousOpenedAuctions.removeAuction(id);
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param evt the property change event containing the property change information.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "End" -> receivedEnd(evt);
      case "Bid" -> receivedBid(evt);
      case "Edit" -> receivedEdit();
      case "Ban", "DeleteAccount" -> receivedBanOrDeleteAccount();
      case "DeleteAuction" -> receivedDeleteAuction(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
  }
}
