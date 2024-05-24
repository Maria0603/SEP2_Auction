package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Auction
    implements NamedPropertyChangeSubject, PropertyChangeListener, Serializable
{
  private int ID;
  private Item item;
  private PriceConstraint priceConstraint;
  private String currentBidder, seller, status;
  private int auctionEndTime, currentBid;
  Time start, end;
  private byte[] imageData;

  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private PropertyChangeSupport property;

  public Auction(int ID, String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, Time auctionStart, Time auctionEnd,
      int currentBid, String currentBidder, String seller, byte[] imageData, String status)
  {
    property = new PropertyChangeSupport(this);
    this.ID = ID;
    this.item = new Item(title, description);
    this.priceConstraint = new PriceConstraint(reservePrice, buyoutPrice,
        minimumIncrement);

    start = auctionStart;
    end = auctionEnd;

    setImageData(imageData);
    setCurrentBid(currentBid);
    setCurrentBidder(currentBidder);
    setSeller(seller);
    this.status = status;

  }

  public Auction(int ID, String title, int currentBid, Time end,
      byte[] imageData)
  {
    this(ID, title, null, 0, 0, 0, null, end, currentBid, null, null, imageData,
        null);
  }

  public boolean isMatchesSearchMask(String searchMask) {
    return String.valueOf(ID).contains(searchMask) || item.getTitle()
        .toLowerCase().contains(searchMask);
  }

  public Item getItem()
  {
    return item;
  }

  public PriceConstraint getPriceConstraint()
  {
    return priceConstraint;
  }
  public String getSeller()
  {
    return seller;
  }

  public Time getEndTime()
  {
    return end;
  }

  public Time getStartTime()
  {
    return start;
  }

  public byte[] getImageData()
  {
    return imageData;
  }

  public void setImageData(byte[] imageData)
  {
    this.imageData = imageData;
  }

  public int getCurrentBid()
  {
    return currentBid;
  }

  public void setCurrentBid(int bid)
  {
    this.currentBid = bid;
  }

  public String getCurrentBidder()
  {
    return currentBidder;
  }

  public void setCurrentBidder(String bidder)
  {
    this.currentBidder = bidder;
  }

  public void setSeller(String seller)
  {
    this.seller=seller;
  }

  public int getID()
  {
    return ID;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  @Override public String toString()
  {
    return "ID=" + ID + ", title='" + item.getTitle() + '\'' + ", description='"
        + item.getDescription() + '\'' + ", reservePrice="
        + priceConstraint.getReservePrice() + ", buyoutPrice="
        + priceConstraint.getBuyoutPrice() + ", minimumIncrement="
        + priceConstraint.getMinimumIncrement() + ", auctionTime="
        + auctionEndTime + '\'';
  }

  @Override synchronized public void addListener(String propertyName,
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
    //auction property fires timer events further
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
        evt.getNewValue());

  }

  public int getId() {
    return ID;
  }

  public String getTitle() {
    return item.getTitle();
  }

  public String getDescription() {
    return item.getDescription();
  }
}


