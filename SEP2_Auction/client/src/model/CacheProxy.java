package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class CacheProxy implements AuctionModel, PropertyChangeListener
{
  private AuctionList ongoingAuctionsCache;
  private AuctionModelManager modelManager;
  private PropertyChangeSupport property;

  public CacheProxy() throws SQLException, IOException
  {
    property = new PropertyChangeSupport(this);
    this.modelManager = new AuctionModelManager();
    modelManager.addListener("Auction", this);
    modelManager.addListener("End", this);
    ongoingAuctionsCache = modelManager.getOngoingAuctions();
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
    Auction auction = modelManager.getAuction(ID);
    Timer timer = new Timer(modelManager.timeLeft(Time.valueOf(LocalTime.now()),
        auction.getEndTime()) - 1, ID);
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
    }
    property.firePropertyChange(evt);
  }
}
