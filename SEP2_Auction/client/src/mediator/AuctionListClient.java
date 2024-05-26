package mediator;

import model.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import model.domain.*;
import utility.observer.event.ObserverEvent;
import utility.observer.listener.RemoteListener;

public class AuctionListClient
    implements RemoteListener<String, Object>, AuctionListModel
{
  private AuctionListRemote server;
  private PropertyChangeSupport property;

  public AuctionListClient() throws IOException
  {
    start();
    property = new PropertyChangeSupport(this);
  }

  private void start()
  {
    try
    {
      UnicastRemoteObject.exportObject(this, 0);
      server = (AuctionListRemote) Naming.lookup("rmi://localhost:1099/AuctionListRemote");

      server.addListener(this, "Auction");
      server.addListener(this, "End");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
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


  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    try
    {
      return server.getPreviousBids(bidder);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    try
    {
      return server.getCreatedAuctions(seller);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }


  @Override public AuctionList getAllAuctions(String moderatorEmail)
      throws SQLException
  {
    try
    {
      return server.getAllAuctions(moderatorEmail);
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
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    try
    {
      return server.isModerator(email);
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
    return false;
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
