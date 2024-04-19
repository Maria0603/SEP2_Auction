package model;

import mediator.AuctionClient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;
  private AuctionClient client;

  public AuctionModelManager()
  {
    try
    {
      property = new PropertyChangeSupport(this);
      client = new AuctionClient();
      client.addListener("Auction", this);
      client.addListener("Time", this);
      client.addListener("End", this);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @Override public Auction startAuction(int ID, String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException
  {
    return client.startAuction(ID, title, description, reservePrice,
        buyoutPrice, minimumIncrement, auctionTime, imageData);
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    return client.getAuction(ID);
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