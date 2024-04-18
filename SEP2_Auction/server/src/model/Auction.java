package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Objects;


public class Auction
    implements NamedPropertyChangeSubject, PropertyChangeListener, Serializable
{
  private int ID;
  private String title, description, currentBidder, imagePath, status;
  int reservePrice, buyoutPrice, minimumIncrement, auctionTime, currentBid;

  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private Timer timer;
  private PropertyChangeSupport property;

  public Auction(int ID, String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, int currentBid, String currentBidder,
      String imagePath, String status)
  {
    property = new PropertyChangeSupport(this);
    setID(ID);
    setTitle(title);
    setDescription(description);
    setReservePrice(reservePrice);
    setMinimumIncrement(minimumIncrement);
    setBuyoutPrice(buyoutPrice);
    setAuctionTime(auctionTime);
    setImagePath(imagePath);
    this.status=status;

    this.timer = new Timer(this.auctionTime, ID);
    this.timer.addListener("Time"+ID, this);
    this.timer.addListener("End"+ID, this);
    Thread t = new Thread(timer);
    t.start();

  }
  public int getCurrentBid()
  {
    return currentBid;
  }
  public void setCurrentBid(int bid)
  {
    //logic for bid
    this.currentBid=bid;
  }
  public String getCurrentBidder()
  {
    return currentBidder;
  }
  public void setCurrentBidder(String bidder)
  {
    //logic for bidder
    this.currentBidder=bidder;
  }


  public int getID() {
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
    this.status=status;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    int maxTitleLength = 80;
    int minTitleLength=5;
    if (title.length() > maxTitleLength)
      throw new IllegalArgumentException("The title is too long!");
    else if (title.length() < minTitleLength)
      throw new IllegalArgumentException("The title is too short!");
    this.title = title;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    int maxDescriptionLength = 1400, minDescriptionLength=20;
    if (description.length() > maxDescriptionLength)
      throw new IllegalArgumentException("The description is too long!");
    else if(description.length()<minDescriptionLength)
      throw new IllegalArgumentException("The description is too short!");
    this.description = description;
  }

  public int getReservePrice()
  {
    return reservePrice;
  }

  public void setReservePrice(int reservePrice)
  {
    if (reservePrice < 0)
      throw new IllegalArgumentException("The reserve price cannot be negative!");
    this.reservePrice = reservePrice;
  }

  public int getBuyoutPrice()
  {
    return buyoutPrice;
  }

  public void setBuyoutPrice(int buyoutPrice)
  {
    if (buyoutPrice < reservePrice)
      throw new IllegalArgumentException("Th buyout price must be greater than the reserve price!");
    this.buyoutPrice = buyoutPrice;
  }

  public int getMinimumIncrement()
  {
    return minimumIncrement;
  }

  public void setMinimumIncrement(int minimumIncrement)
  {
    if (minimumIncrement<1)
      throw new IllegalArgumentException("The minimum bid increment must be at least 1!");
    this.minimumIncrement = minimumIncrement;
  }

  public void setAuctionTime(int auctionTime)
  {
    //to be updated when the moderator adds the time interval
    if (auctionTime <= 0 || auctionTime > 24)
      throw new IllegalArgumentException("The auction time is out of bounds");

    /////////////////////////////////////////////////////////////////////////////////
    //correct line:
    this.auctionTime = auctionTime * 3600;
    ////////////////////////////////////////////////////////////////////////////////
    //for testing purposes:
    //this.auctionTime=auctionTime;
  }

  public String getImagePath()
  {
    return imagePath;
  }

  public void setImagePath(String imagePath)
  {
    this.imagePath = imagePath;
  }

  @Override public String toString()
  {
    return "ID=" + ID + ", title='" + title + '\''
        + ", description='" + description + '\'' + ", reservePrice="
        + reservePrice + ", buyoutPrice=" + buyoutPrice + ", minimumIncrement="
        + minimumIncrement + ", auctionTime=" + auctionTime + ", imagePath='"
        + imagePath + '\'' + ", timer=" + timer + ", property=" + property
        + '}';
  }

  @Override public boolean equals(Object obj)
  {
    if (obj == null || this.getClass() != obj.getClass())
      return false;

    Auction auction = (Auction)obj;
    return ID == auction.ID && reservePrice == auction.reservePrice
        && buyoutPrice == auction.buyoutPrice
        && minimumIncrement == auction.minimumIncrement
        && auctionTime == auction.auctionTime && Objects.equals(title,
        auction.title) && Objects.equals(description, auction.description)
        && Objects.equals(imagePath, auction.imagePath);
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
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

    //System.out.println(evt.getNewValue());
  }

}


