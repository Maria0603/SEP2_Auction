package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class AuctionListCacheProxy extends Cache
    implements AuctionListModel, PropertyChangeListener
{
  private AuctionList ongoingAuctionsCache;
  private AuctionList allAuctionsCache;
  private AuctionListModelManager modelManager;
  private PropertyChangeSupport property;

  private AuctionList previousBidsCache, createdAuctionsCache;

  public AuctionListCacheProxy() throws SQLException, IOException
  {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new AuctionListModelManager();
    modelManager.addListener("Auction", this);
    modelManager.addListener("End", this);
    modelManager.addListener("Bid", this);
    modelManager.addListener("Edit", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("Reset", this);
    modelManager.addListener("DeleteAuction", this);

    modelManager.addListener("DeleteAccount", this);

    ongoingAuctionsCache = new AuctionList();
    allAuctionsCache = new AuctionList();

    previousBidsCache = new AuctionList();
    createdAuctionsCache = new AuctionList();
  }


  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return ongoingAuctionsCache;
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    return previousBidsCache;
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    return createdAuctionsCache;
  }


  @Override public AuctionList getAllAuctions(String moderatorEmail)
      throws SQLException
  {
    return allAuctionsCache;
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    return null;
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    return modelManager.isModerator(email);
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
  private void updateCache(String userEmail) throws SQLException
  {
    createdAuctionsCache = modelManager.getCreatedAuctions(userEmail);
    previousBidsCache = modelManager.getPreviousBids(userEmail);
    ongoingAuctionsCache = modelManager.getOngoingAuctions();
    if(isModerator(userEmail))
      allAuctionsCache=modelManager.getAllAuctions(userEmail);
  }
  private void updateBidIn(Bid bid, AuctionList cache)
  {
    if(cache.contains(bid.getAuctionId()))
    {
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
    }
  }
  private void receivedAuction(PropertyChangeEvent evt)
  {
    Auction auction = (Auction) evt.getNewValue();
    ongoingAuctionsCache.addAuction(auction);
    allAuctionsCache.addAuction(auction);

    if (super.getUserEmail().equals(auction.getSeller()))
      createdAuctionsCache.addAuction(auction);
  }
  private void receivedEnd(PropertyChangeEvent evt)
  {
    System.out.println(
        evt.getPropertyName() + "     " + evt.getOldValue() + "    "
            + evt.getNewValue());
    int auctionId=Integer.parseInt(evt.getOldValue().toString());
    ongoingAuctionsCache.removeAuction(auctionId);

    if (evt.getNewValue() instanceof Bid)
    {
      Bid buyout = (Bid) evt.getNewValue();
      Auction auction = null;
      try
      {
        auction = modelManager.getAuction(buyout.getAuctionId());
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      if (buyout.getBidder().equals(super.getUserEmail()))
      {
        if (auction != null)
          previousBidsCache.addAuction(auction);
      }
      updateBidIn(buyout, allAuctionsCache);

      if (auction != null && auction.getSeller().equals(super.getUserEmail()))
      {
        updateBidIn(buyout, createdAuctionsCache);
      }

    }
  }

  private void receivedBid(PropertyChangeEvent evt)
  {
    // we receive a bid
    Bid bid = (Bid) evt.getNewValue();
    System.out.println("received bid in cache; " + bid.getAuctionId() + " "
        + bid.getBidder() + "   " + bid.getBidAmount());

    // obviously, for an ongoing auction, so we update the cache
    updateBidIn(bid, ongoingAuctionsCache);
    updateBidIn(bid, allAuctionsCache);

    //if we placed the bid, we add the auction in cache
    if (!previousBidsCache.contains(bid.getAuctionId()) && bid.getBidder().equals(super.getUserEmail()))
    {
      try
      {
        previousBidsCache.addAuction(getAuction(bid.getAuctionId()));
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    else
      // if someone else placed a bid for an auction where we previously bid
      // we update the cache
      updateBidIn(bid, previousBidsCache);

    updateBidIn(bid, createdAuctionsCache);
  }
  private void receivedEdit(PropertyChangeEvent evt)
  {
    if (super.getUserEmail().equals(evt.getOldValue().toString()))
    {
      super.setUserEmail(evt.getNewValue().toString());
    }
    try
    {
      updateCache(super.getUserEmail());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  private void receivedBanOrDeleteAccount(PropertyChangeEvent evt)
  {
    try
    {
      updateCache(super.getUserEmail());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  private void receivedDeleteAuction(PropertyChangeEvent evt)
  {
    int id = Integer.parseInt(evt.getNewValue().toString());
    ongoingAuctionsCache.removeAuction(id);
    previousBidsCache.removeAuction(id);
    createdAuctionsCache.removeAuction(id);
    allAuctionsCache.removeAuction(id);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    System.out.println("received "+evt.getPropertyName() + " in cache");
    switch (evt.getPropertyName())
    {
      case "Auction" -> receivedAuction(evt);
      case "End" -> receivedEnd(evt);

      case "Bid" -> receivedBid(evt);
      case "Edit" -> receivedEdit(evt);
      case "Ban", "DeleteAccount" -> receivedBanOrDeleteAccount(evt);
      case "DeleteAuction" -> receivedDeleteAuction(evt);

    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
        evt.getNewValue());
  }
}