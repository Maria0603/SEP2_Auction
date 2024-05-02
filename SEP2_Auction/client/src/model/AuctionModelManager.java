package model;

import mediator.AuctionClient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;
  private AuctionClient client;
  private AuctionList ongoingAuctionsCache;

  public AuctionModelManager() throws IOException, SQLException
  {
      property = new PropertyChangeSupport(this);
      client = new AuctionClient();
      ongoingAuctionsCache=client.getOngoingAuctions();
      client.addListener("Auction", this);
      client.addListener("End", this);
  }

  @Override public Auction startAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException, ClassNotFoundException
  {
    return client.startAuction(title, description, reservePrice,
        buyoutPrice, minimumIncrement, auctionTime, imageData);
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    Auction auction;
    try
    {
      auction = ongoingAuctionsCache.getAuctionByID(ID);
    }
    catch(IllegalArgumentException e)
    {
        auction = client.getAuction(ID);
    }
      Timer timer=new Timer(timeLeft(Time.valueOf(LocalTime.now()), auction.getEndTime())-1, ID);
      timer.addListener("Time", this);
      timer.addListener("End", this);
      Thread t = new Thread(timer, String.valueOf(ID));
      t.start();
    return auction;
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return ongoingAuctionsCache;
  }

  private long timeLeft(Time currentTime, Time end)
  {
    long currentSeconds = currentTime.toLocalTime().toSecondOfDay();
    long endSeconds = end.toLocalTime().toSecondOfDay();
    if (currentSeconds >= endSeconds)
      return 60*60*24-(currentSeconds-endSeconds);
    else return endSeconds-currentSeconds;
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
        //closedAuctionsCached.addAuction((Auction) evt.getNewValue());
        break;
    }
    //model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}