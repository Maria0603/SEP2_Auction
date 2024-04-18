package model;

import mediator.AuctionClient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

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
      //client.addListener("Time", this);
      //client.addListener("End", this);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }


  @Override public Auction startAuction(int ID, String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, String imagePath)
  {
    client.addListener("Time"+ID, this);
    client.addListener("End"+ID, this);
    return client.startAuction(ID, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imagePath);
  }

  @Override public Auction getAuction(int ID)
  {
    return client.getAuction(ID);
  }

  @Override public void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    //client.addListener(propertyName, listener);
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    //client.removeListener(propertyName, listener);
    property.removePropertyChangeListener(propertyName, listener);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    //model manager property fires auction events further
    property.firePropertyChange(evt);
    //System.out.println((String) evt.getNewValue());
  }
}