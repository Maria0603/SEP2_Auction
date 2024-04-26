package model;

import persistence.AuctionDatabase;
import persistence.AuctionPersistence;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;
  private AuctionPersistence auctionDatabase;

  public AuctionModelManager()
  {
    property = new PropertyChangeSupport(this);
  }

  @Override public Auction startAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException, ClassNotFoundException
  {
    auctionDatabase=new AuctionDatabase();
    Auction auction = auctionDatabase.saveAuction(title, description, reservePrice, buyoutPrice,
            minimumIncrement, auctionTime, imageData);
    property.firePropertyChange("Auction", null, auction);

    //auction.addListener("Time", this);
    auction.addListener("End", this);
    return auction;
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    return auctionDatabase.getAuctionById(ID);
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
    //update the time and status in the database
    /*if(evt.getPropertyName().equals("Time") && (int)evt.getNewValue()%5==0)
    {
      try
      {
        auctionDatabase.updateTime((int)evt.getOldValue(), (int)evt.getNewValue());
      }
      catch(SQLException e)
      {
        e.printStackTrace();
      }
    }*/
    if(evt.getPropertyName().equals("End"))
    {
      try
      {
        auctionDatabase.markAsClosed((int)evt.getOldValue());
      }
      catch(SQLException e)
      {
        e.printStackTrace();
      }
    }
    //model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}