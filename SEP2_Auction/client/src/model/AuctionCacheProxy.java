package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class AuctionCacheProxy extends CacheProxy
    implements AuctionModel, PropertyChangeListener
{
  private final AuctionModelManager modelManager;
  private final PropertyChangeSupport property;

  private AuctionList previousOpenedAuctions;

  public AuctionCacheProxy() throws IllegalArgumentException, IOException
  {
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

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller)
      throws IllegalArgumentException, ClassNotFoundException
  {
    return modelManager.startAuction(title, description, reservePrice,
        buyoutPrice, minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public Auction getAuction(int ID) throws IllegalArgumentException
  {
    if (previousOpenedAuctions.contains(ID))
      return previousOpenedAuctions.getAuctionByID(ID);
    Auction auction = modelManager.getAuction(ID);
    startTimer(auction);
    previousOpenedAuctions.addAuction(auction);
    return auction;
  }

  private void startTimer(Auction auction)
  {
    if (auction.getStatus().equals("ONGOING"))
    {
      Timer timer = new Timer(
          timeLeft(Time.valueOf(LocalTime.now()), auction.getEndTime()) - 1,
          auction.getID());
      timer.addListener("Time", this);
      Thread t = new Thread(timer, String.valueOf(auction.getID()));
      t.start();
    }
  }

  @Override public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws IllegalArgumentException
  {
    return modelManager.placeBid(bidder, bidValue, auctionId);
  }

  @Override public void buyout(String bidder, int auctionId) throws IllegalArgumentException
  {
    modelManager.buyout(bidder, auctionId);
  }

  private long timeLeft(Time currentTime, Time end)
  {
    long currentSeconds = currentTime.toLocalTime().toSecondOfDay();
    long endSeconds = end.toLocalTime().toSecondOfDay();
    if (currentSeconds >= endSeconds)
      return 60 * 60 * 24 - (currentSeconds - endSeconds);
    else
      return endSeconds - currentSeconds;
  }

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws IllegalArgumentException
  {
    modelManager.deleteAuction(moderatorEmail, auctionId, reason);
  }

  @Override public void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

  private void updateBidIn(Bid bid, AuctionList cache)
  {
    if (cache.contains(bid.getAuctionId()))
    {
      cache.getAuctionByID(bid.getAuctionId())
          .setCurrentBidder(bid.getBidder());
      cache.getAuctionByID(bid.getAuctionId())
          .setCurrentBid(bid.getBidAmount());
    }
  }

  private void receivedEnd(PropertyChangeEvent evt)
  {
    int auctionId = Integer.parseInt(evt.getOldValue().toString());
    if (previousOpenedAuctions.contains(auctionId))
      previousOpenedAuctions.getAuctionByID(auctionId).setStatus("CLOSED");

    if (evt.getNewValue() instanceof Bid)
    {
      Bid buyout = (Bid) evt.getNewValue();
      updateBidIn(buyout, previousOpenedAuctions);
    }
  }

  private void receivedBid(PropertyChangeEvent evt)
  {
    Bid bid = (Bid) evt.getNewValue();
    updateBidIn(bid, previousOpenedAuctions);
  }

  private void updateCache()
  {
    previousOpenedAuctions = new AuctionList();
  }

  private void receivedEdit()
  {
    updateCache();
  }

  private void receivedBanOrDeleteAccount()
  {
    updateCache();
  }

  private void receivedDeleteAuction(PropertyChangeEvent evt)
  {
    int id = Integer.parseInt(evt.getNewValue().toString());
    previousOpenedAuctions.removeAuction(id);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName())
    {
      case "End" -> receivedEnd(evt);
      case "Bid" -> receivedBid(evt);
      case "Edit" -> receivedEdit();
      case "Ban", "DeleteAccount" -> receivedBanOrDeleteAccount();
      case "DeleteAuction" -> receivedDeleteAuction(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
        evt.getNewValue());
  }
}