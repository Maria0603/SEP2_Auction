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

public class AuctionListClient
    implements RemoteListener<String, Object>, AuctionListModel
{
  private AuctionListRemote server;
  private final PropertyChangeSupport property;

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
      server = (AuctionListRemote) Naming.lookup(
          "rmi://localhost:1099/AuctionListRemote");

      server.addListener(this, "Auction");
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

  @Override public AuctionList getOngoingAuctions() throws IllegalArgumentException
  {
    try
    {
      return server.getOngoingAuctions();
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws IllegalArgumentException
  {
    try
    {
      return server.getPreviousBids(bidder);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws IllegalArgumentException
  {
    try
    {
      return server.getCreatedAuctions(seller);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public AuctionList getAllAuctions(String moderatorEmail)
      throws IllegalArgumentException
  {
    try
    {
      return server.getAllAuctions(moderatorEmail);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public Auction getAuction(int ID) throws IllegalArgumentException
  {
    try
    {
      return server.getAuction(ID);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public boolean isModerator(String email) throws IllegalArgumentException
  {
    try
    {
      return server.isModerator(email);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
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
