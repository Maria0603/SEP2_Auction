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

  public AuctionModelManager() throws IOException, SQLException
  {
    property = new PropertyChangeSupport(this);
    client = new AuctionClient();
    client.addListener("Auction", this);
    client.addListener("End", this);
    client.addListener("Bid",this);
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData) throws SQLException, ClassNotFoundException
  {
    return client.startAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData);
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    return client.getAuction(ID);
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return client.getOngoingAuctions();
  }

  public long timeLeft(Time currentTime, Time end)
  {
    long currentSeconds = currentTime.toLocalTime().toSecondOfDay();
    long endSeconds = end.toLocalTime().toSecondOfDay();
    if (currentSeconds >= endSeconds)
      return 60 * 60 * 24 - (currentSeconds - endSeconds);
    else
      return endSeconds - currentSeconds;
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
    //model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}