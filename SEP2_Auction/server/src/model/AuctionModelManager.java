package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

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
      int minimumIncrement, int auctionTime, String imagePath)
  {
    auction = new Auction(ID, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imagePath);
    ////////////////////////////////////////////////////////////////////////////
    property.firePropertyChange("Auction", null, auction);

    auction.addListener("Time"+auction.getID(), this);
    auction.addListener("End"+auction.getID(), this);
    return auction;
  }



  @Override public Auction getAuction(int ID)
  {
    //TODO:change when ArrayList is used
    return this.auction;
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