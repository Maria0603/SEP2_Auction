package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;

public class Auction
    implements NamedPropertyChangeSubject, PropertyChangeListener, Serializable
{
  private int ID;
  private Item item;
  private PriceConstraint priceConstraint;
  private String currentBidder, status;
  private int auctionEndTime, currentBid;
  Time start, end;
  private byte[] imageData;

  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private PropertyChangeSupport property;

  public Auction(int ID, String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, Time auctionStart, Time auctionEnd, int currentBid,
      String currentBidder, byte[] imageData, String status)
  {
    property = new PropertyChangeSupport(this);
    setID(ID);
    this.item=new Item(title, description);
    this.priceConstraint=new PriceConstraint(reservePrice, buyoutPrice, minimumIncrement);

    start=auctionStart;
    end=auctionEnd;

    setImageData(imageData);
    this.status = status;

  }
  public Item getItem()
  {
    return item;
  }
  public PriceConstraint getPriceConstraint()
  {
    return priceConstraint;
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
    if (imageData == null)
      throw new IllegalArgumentException("Please upload an image.");
    this.imageData = imageData;
  }

  public int getCurrentBid()
  {
    return currentBid;
  }

  public void setCurrentBid(int bid)
  {
    //logic for bid
    this.currentBid = bid;
  }

  public String getCurrentBidder()
  {
    return currentBidder;
  }

  public void setCurrentBidder(String bidder)
  {
    //logic for bidder
    this.currentBidder = bidder;
  }

  public int getID()
  {
    return ID;
  }

  public void setID(int ID)
  {
    this.ID = ID;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }



  public void setAuctionTime(int auctionTime)
  {
    if (auctionTime <= 0 || auctionTime > 24 * 3600)
      throw new IllegalArgumentException(
          "The auction time can be at most 24 hours!");

    /////////////////////////////////////////////////////////////////////////////////
    //correct line:
    this.auctionEndTime = auctionTime;
    ////////////////////////////////////////////////////////////////////////////////
    //for testing purposes:
    //this.auctionEndTime=auctionTime/3600;
  }

  @Override public String toString()
  {
    return "ID=" + ID + ", title='" + item.getTitle() + '\'' + ", description='"
        + item.getDescription() + '\'' + ", reservePrice=" + priceConstraint.getReservePrice()
        + ", buyoutPrice=" + priceConstraint.getBuyoutPrice() + ", minimumIncrement="
        + priceConstraint.getMinimumIncrement() + ", auctionTime=" + auctionEndTime + '\'';
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

}


