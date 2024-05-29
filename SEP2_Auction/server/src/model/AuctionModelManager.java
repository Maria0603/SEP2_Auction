package model;

import model.domain.*;
import persistence.AuctionPersistence;
import persistence.AuctionProtectionProxy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

/**
 * The AuctionModelManager class implements the AuctionModel interface
 * and provides methods for managing auctions and bids using a persistence layer.
 */
public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private final PropertyChangeSupport property;
  private final AuctionPersistence auctionDatabase;

  /**
   * Constructs an AuctionModelManager and initializes the persistence layer.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public AuctionModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    auctionDatabase = new AuctionProtectionProxy();
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
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized Auction startAuction(String title, String description, int reservePrice,
                                           int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
          throws SQLException
  {
    Auction auction = auctionDatabase.saveAuction(title, description,
            reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData,
            seller);
    property.firePropertyChange("Auction", null, auction);
    startTimer(auction);
    return auction;
  }

  /**
   * Starts a timer for the specified auction.
   *
   * @param auction the auction to start the timer for.
   */
  private void startTimer(Auction auction)
  {
    ServerTimer timer = new ServerTimer(auction.getStartTime(),
            auction.getEndTime(), auction.getID());
    timer.addListener("End", this);
    new Thread(timer).start();
  }

  /**
   * Retrieves a specific auction by its ID.
   *
   * @param ID the identifier of the auction.
   * @return the auction with the specified ID.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized Auction getAuction(int ID) throws SQLException
  {
    return auctionDatabase.getAuctionById(ID);
  }

  /**
   * Places a bid on a specified auction.
   *
   * @param bidder the email or identifier of the bidder.
   * @param bidValue the value of the bid.
   * @param auctionId the identifier of the auction.
   * @return the placed Bid object.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized Bid placeBid(String bidder, int bidValue, int auctionId) throws SQLException
  {
    //we extract the existing bidder
    Bid existingBid = auctionDatabase.getCurrentBidForAuction(auctionId);
    Bid bid = auctionDatabase.saveBid(bidder, bidValue, auctionId);
    //if they exist, we send the notification in the event
    if (existingBid != null)
    {
      Notification notification = auctionDatabase.saveNotification(
              "Your bid has been beaten for auction ID: #" + auctionId + ".",
              existingBid.getBidder());
      property.firePropertyChange("Notification", null, notification);
    }
    property.firePropertyChange("Bid", null, bid);
    return bid;
  }

  /**
   * Executes a buyout for a specified auction.
   *
   * @param current_bidder the email or identifier of the bidder.
   * @param auctionId the identifier of the auction.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void buyout(String current_bidder, int auctionId) throws SQLException
  {
    Bid bid = auctionDatabase.buyout(current_bidder, auctionId);
    property.firePropertyChange("End", auctionId, bid);
    sendContactInformation(auctionId);
  }

  /**
   * Deletes an auction with the specified reason.
   *
   * @param moderatorEmail the email of the moderator requesting the deletion.
   * @param auctionId the identifier of the auction.
   * @param reason the reason for the deletion.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void deleteAuction(String moderatorEmail, int auctionId, String reason) throws SQLException
  {
    String seller = auctionDatabase.getAuctionById(auctionId).getSeller();
    auctionDatabase.deleteAuction(moderatorEmail, auctionId, reason);

    Notification notification = auctionDatabase.saveNotification(
            "Your auction ID: #" + auctionId + " has been deleted. Reason: "
                    + reason, seller);
    property.firePropertyChange("Notification", null, notification);
    property.firePropertyChange("DeleteAuction", null, auctionId);
  }

  /**
   * Sends contact information to the seller and winning bidder of a completed auction.
   *
   * @param id the identifier of the auction.
   * @throws SQLException if there is a database access error.
   */
  private void sendContactInformation(int id) throws SQLException
  {
    Auction auction = auctionDatabase.getAuctionById(id);
    if (auction.getCurrentBid() != 0)
    {
      User seller = auctionDatabase.getUserInfo(auction.getSeller());
      User bidder = auctionDatabase.getUserInfo(auction.getCurrentBidder());
      int bid = auction.getCurrentBid();

      String contentForSeller =
              "Your Auction(ID: #" + id + ") has ended, Final bidder: " + bidder
                      + ", with bid: " + bid + ".";
      String contentForBidder =
              "You've won an Auction(ID: #" + id + "), sold by: " + seller
                      + ", with bid: " + bid + ".";

      Notification notificationOne = auctionDatabase.saveNotification(
              contentForSeller, seller.getEmail());
      Notification notificationTwo = auctionDatabase.saveNotification(
              contentForBidder, bidder.getEmail());

      property.firePropertyChange("Notification", null, notificationOne);
      property.firePropertyChange("Notification", null, notificationTwo);
    }
  }

  /**
   * Adds a listener for a specific property change.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to be added.
   */
  @Override
  public synchronized void addListener(String propertyName, PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a listener for a specific property change.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to be removed.
   */
  @Override
  public synchronized void removeListener(String propertyName, PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * Handles property change events and fires property change notifications.
   *
   * @param evt the property change event.
   */
  @Override
  public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("End"))
    {
      try
      {
        auctionDatabase.markAsClosed((int) evt.getOldValue());
        sendContactInformation((int) evt.getOldValue());
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}
