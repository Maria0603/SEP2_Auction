

package model;

import model.domain.*;
import persistence.AuctionPersistence;
import persistence.AuctionProtectionProxy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private final PropertyChangeSupport property;
  private final AuctionPersistence auctionDatabase;

  public AuctionModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    auctionDatabase = new AuctionProtectionProxy();
  }

  @Override public synchronized Auction startAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws SQLException
  {
    Auction auction = auctionDatabase.saveAuction(title, description,
        reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData,
        seller);
    property.firePropertyChange("Auction", null, auction);
    ServerTimer timer = new ServerTimer(auction.getStartTime(),
        auction.getEndTime(), auction.getID());
    timer.addListener("End", this);
    new Thread(timer).start();

    return auction;
  }

  @Override public synchronized Auction getAuction(int ID) throws SQLException
  {
    return auctionDatabase.getAuctionById(ID);
  }

  @Override public synchronized Bid placeBid(String bidder, int bidValue,
      int auctionId) throws SQLException
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

  @Override public void buyout(String current_bidder, int auctionId)
      throws SQLException
  {
    Bid bid = auctionDatabase.buyout(current_bidder, auctionId);
    property.firePropertyChange("End", auctionId, bid);
    sendContactInformation(auctionId);
  }

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws SQLException
  {
    String seller = auctionDatabase.getAuctionById(auctionId).getSeller();
    auctionDatabase.deleteAuction(moderatorEmail, auctionId, reason);

    Notification notification = auctionDatabase.saveNotification(
        "Your auction ID: #" + auctionId + " has been deleted. Reason: "
            + reason, seller);
    property.firePropertyChange("Notification", null, notification);
    property.firePropertyChange("DeleteAuction", null, auctionId);
  }

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

  @Override public synchronized void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void propertyChange(PropertyChangeEvent evt)
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
