package mediator;

import model.Auction;
import model.AuctionList;
import model.AuctionModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import utility.observer.event.ObserverEvent;
import utility.observer.listener.RemoteListener;

public class AuctionClient
    implements RemoteListener<String, Object>, AuctionModel
{
  private AuctionRemote server;
  private PropertyChangeSupport property;

  public AuctionClient() throws IOException
  {
    start();
    property = new PropertyChangeSupport(this);
  }

  // establish server connection
  private void start()
  {
    try
    {
      UnicastRemoteObject.exportObject(this, 0);
      server = (AuctionRemote) Naming.lookup("rmi://localhost:1099/Connect");

      server.addListener(this, "Auction");
      server.addListener(this, "Time");
      server.addListener(this, "End");
      server.addListener(this,"Bid");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData) throws SQLException, ClassNotFoundException
  {
    try
    {
      return server.startAuction(title, description, reservePrice, buyoutPrice,
          minimumIncrement, auctionTime, imageData);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    try
    {
      return server.getAuction(ID);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    try
    {
      return server.getOngoingAuctions();
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public void addListener(String s,
      PropertyChangeListener propertyChangeListener)
  {
    property.addPropertyChangeListener(s, propertyChangeListener);
  }

  @Override public void removeListener(String s,
      PropertyChangeListener propertyChangeListener)
  {
    property.removePropertyChangeListener(s, propertyChangeListener);
  }

  @Override public void propertyChange(ObserverEvent<String, Object> event)
      throws RemoteException
  {
    property.firePropertyChange(event.getPropertyName(), event.getValue1(),
        event.getValue2());
  }

}
