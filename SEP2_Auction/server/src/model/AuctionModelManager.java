package model;

import AuctionPersistence.AuctionDatabase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private Auction auction;
  private PropertyChangeSupport property;

  public AuctionModelManager(Auction auction)
  {
    property = new PropertyChangeSupport(this);
    this.auction = auction;

  }

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
    ////////////////////////////////////////////////////////////////////////////
    property.firePropertyChange("Auction", null, auction);


    auction.addListener("Time", this);
    auction.addListener("End", this);
    return auction;
     */

    //with database

      auction = AuctionDatabase.getInstance().saveAuction(ID, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData);
      //System.out.println(auction.toString());
      property.firePropertyChange("Auction", null, auction);

      auction.addListener("Time", this);
      auction.addListener("End", this);
      return auction;
  }



  @Override public Auction getAuction(int ID) throws SQLException
  {
    //TODO:change when ArrayList is used
    /*
    try
    {
      return AuctionDatabase.getInstance().getAuctionById(auction.getID());
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }
    return null;
     */
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
    property.firePropertyChange(evt);
    //System.out.println(evt.getNewValue());

    //System.out.println((String) evt.getNewValue());
  }
}