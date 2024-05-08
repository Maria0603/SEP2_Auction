package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class CacheProxy implements AuctionModel, PropertyChangeListener
{
  private AuctionList ongoingAuctionsCache;
  private AuctionList previousOpenedAuctions;
//  private NotificationList notifications;
  private AuctionModelManager modelManager;
  private PropertyChangeSupport property;
  private ArrayList<Thread> timers;

  public CacheProxy() throws SQLException, IOException
  {
    property = new PropertyChangeSupport(this);
    this.modelManager = new AuctionModelManager();
    modelManager.addListener("Auction", this);
    modelManager.addListener("End", this);
    modelManager.addListener("Bid", this);

    ongoingAuctionsCache = modelManager.getOngoingAuctions();
    previousOpenedAuctions=new AuctionList();
    timers=new ArrayList<>();

//    notifications=modelManager.getNotifications("Mr Kaplan");
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData) throws SQLException, ClassNotFoundException
  {
    Auction auction = modelManager.startAuction(title, description,
        reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData);
    //ongoingAuctionsCache.addAuction(auction);
    return auction;
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    Auction auction;
    try
    {
      auction = previousOpenedAuctions.getAuctionByID(ID);
      for(int i=0; i<timers.size(); i++)
        if(timers.get(i).getId()==ID)
          timers.get(i).start();
      //else timers.get(i).interrupt();
    }
    catch(IllegalArgumentException e)
    {
      auction = modelManager.getAuction(ID);
      //add auction to cache
      previousOpenedAuctions.addAuction(auction);
      Timer timer = new Timer(timeLeft(Time.valueOf(LocalTime.now()), auction.getEndTime()) - 1, ID);
      timer.addListener("Time", this);
      timer.addListener("End", this);
      Thread t = new Thread(timer, String.valueOf(ID));
      //add timer to cache
      timers.add(t);
      t.start();
    }

    return auction;
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return ongoingAuctionsCache;
  }


//  @Override public NotificationList getNotifications(String receiver)
//      throws SQLException
//  {
//    return notifications;
//  }

  @Override public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws SQLException
  {
    return modelManager.placeBid(bidder, bidValue, auctionId);
  }

  private long timeLeft(Time currentTime, Time end) {
    long currentSeconds = currentTime.toLocalTime().toSecondOfDay();
    long endSeconds = end.toLocalTime().toSecondOfDay();
    if (currentSeconds >= endSeconds)
      return 60 * 60 * 24 - (currentSeconds - endSeconds);
    else
      return endSeconds - currentSeconds;
  }
  @Override
  public void addUser(String firstname, String lastname, String email, String password, String phone) {
    modelManager.addUser(firstname,lastname,email,password,phone);
  }

  @Override
  public String getUser(String email, String password) {
    return modelManager.getUser(email,password);

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

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName())
    {
      case "Auction":
        ongoingAuctionsCache.addAuction((Auction) evt.getNewValue());
        break;
      case "End":
        ongoingAuctionsCache.removeAuction((Auction) evt.getNewValue());
        //closedAuctionsCache.addAuction((Auction) evt.getNewValue());
        break;
      case "Notification":
//        notifications.addNotification((Notification) evt.getNewValue());
        break;
      case "Bid":
        Bid bid=(Bid)evt.getNewValue();
        ongoingAuctionsCache.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
        ongoingAuctionsCache.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());
        previousOpenedAuctions.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
        previousOpenedAuctions.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());

        break;
    }
    property.firePropertyChange(evt);
  }
}




