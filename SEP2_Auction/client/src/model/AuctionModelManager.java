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
  private ArrayList<Integer> ids;

  public AuctionModelManager()
  {
    try
    {
      property = new PropertyChangeSupport(this);
      client = new AuctionClient();
      ids=new ArrayList<>();
      client.addListener("Auction", this);
      //client.addListener("Time", this);
      client.addListener("End", this);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
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
    Auction auction=client.getAuction(ID);
    if(auction!=null)
    {
      ids.add(auction.getID());
      Timer timer=new Timer(timeLeft(Time.valueOf(LocalTime.now()), auction.getEndTime())-1, ID);
      timer.addListener("Time", this);
      Thread t = new Thread(timer, String.valueOf(ID));
      t.start();
    }
    return auction;
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
    //model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}