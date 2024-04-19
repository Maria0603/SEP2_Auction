package model;

import AuctionPersistence.AuctionDatabase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;

  public AuctionModelManager()
  {
    property = new PropertyChangeSupport(this);
  }

  @Override public Auction startAuction(int ID, String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException
  {
    /*
    //without database
    auction = new Auction(ID, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, 0, null, imageData, "ON SALE");
    property.firePropertyChange("Auction", null, auction);
    auction.addListener("Time", this);
    auction.addListener("End", this);
    return auction;
     */

    //with database
    Auction auction = AuctionDatabase.getInstance()
        .saveAuction(ID, title, description, reservePrice, buyoutPrice,
            minimumIncrement, auctionTime, imageData);
    property.firePropertyChange("Auction", null, auction);

    auction.addListener("Time", this);
    auction.addListener("End", this);
    return auction;
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    return AuctionDatabase.getInstance().getAuctionById(ID);
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
    //if(evt.getPropertyName().equals("Time") && (int)evt.getNewValue()%1000==0)
    property.firePropertyChange(evt);
  }
}