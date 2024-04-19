package mediator;

import model.Auction;
import model.AuctionModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import utility.observer.event.ObserverEvent;
import utility.observer.listener.RemoteListener;

public class AuctionClient implements RemoteListener<String, Object>, AuctionModel
{
  private AuctionRemote server;
  private PropertyChangeSupport property;

  public AuctionClient() throws IOException
  {
    start();
    property=new PropertyChangeSupport(this);
  }
  //establish server connection
  private void start()
  {
    try
    {
      UnicastRemoteObject.exportObject(this, 0);
      server=(AuctionRemote) Naming.lookup("rmi://localhost:1099/Connect");

      server.addListener(this, "Auction");
      server.addListener(this, "Time");
      server.addListener(this, "End");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override public Auction startAuction(int ID, String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData)
  {
    try
    {
      return server.startAuction(ID, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }


  @Override public Auction getAuction(int ID)
  {
    try
    {
      return server.getAuction(ID);
    }
    catch(RemoteException e)
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
    property.firePropertyChange(event.getPropertyName(), event.getValue1(), event.getValue2());
    //System.out.println(event.getValue2());

  }

}
