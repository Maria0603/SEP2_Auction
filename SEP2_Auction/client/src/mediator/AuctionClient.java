package mediator;

import model.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

import model.domain.*;
import utility.observer.event.ObserverEvent;
import utility.observer.listener.RemoteListener;

public class AuctionClient
    implements RemoteListener<String, Object>, AuctionModel
{
  private AuctionRemote server;
  private final PropertyChangeSupport property;

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
      server = (AuctionRemote) Naming.lookup(
          "rmi://localhost:1099/AuctionRemote");

      server.addListener(this, "End");
      server.addListener(this, "Bid");
      server.addListener(this, "Edit");
      server.addListener(this, "Ban");
      server.addListener(this, "DeleteAuction");
      server.addListener(this, "DeleteAccount");

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller)
      throws SQLException, ClassNotFoundException
  {
    try
    {
      return server.startAuction(title, description, reservePrice, buyoutPrice,
          minimumIncrement, auctionTime, imageData, seller);
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

  @Override public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws SQLException
  {
    try
    {
      return server.placeBid(bidder, bidValue, auctionId);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public void buyout(String bidder, int auctionId) throws SQLException
  {
    try
    {
      server.buyout(bidder, auctionId);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }

  }

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws SQLException
  {
    try
    {
      server.deleteAuction(moderatorEmail, auctionId, reason);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
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
