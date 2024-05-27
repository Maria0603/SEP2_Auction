package mediator;

import model.*;
import model.domain.*;
import utility.observer.listener.GeneralListener;
import utility.observer.subject.PropertyChangeHandler;
import utility.observer.subject.RemoteSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class AuctionServer
    implements AuctionRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
{
  private final AuctionModel model;
  private final PropertyChangeHandler<String, Object> property;

  public AuctionServer(AuctionModel model,
      ListenerSubjectInterface listenerSubject)
      throws MalformedURLException, RemoteException
  {
    this.model = model;
    property = new PropertyChangeHandler<>(this, true);

    listenerSubject.addListener("End", this);
    listenerSubject.addListener("Bid", this);
    listenerSubject.addListener("DeleteAuction", this);
    listenerSubject.addListener("Ban", this);
    listenerSubject.addListener("Edit", this);
    listenerSubject.addListener("DeleteAccount", this);

    startServer();
  }

  private synchronized void startServer()
      throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("AuctionRemote", this);
  }

  @Override public synchronized Auction startAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws RemoteException, SQLException, ClassNotFoundException
  {
    return model.startAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public synchronized Auction getAuction(int id)
      throws RemoteException, SQLException
  {
    return model.getAuction(id);
  }

  @Override public synchronized Bid placeBid(String bidder, int bidValue,
      int auctionId) throws RemoteException, SQLException
  {
    return model.placeBid(bidder, bidValue, auctionId);
  }

  @Override public synchronized void buyout(String bidder, int auctionId)
      throws RemoteException, SQLException
  {

    model.buyout(bidder, auctionId);
  }

  @Override public synchronized void deleteAuction(String moderatorEmail,
      int auctionId, String reason) throws RemoteException, SQLException
  {
    model.deleteAuction(moderatorEmail, auctionId, reason);
  }

  @Override public synchronized boolean addListener(
      GeneralListener<String, Object> listener, String... propertyNames)
      throws RemoteException
  {
    return property.addListener(listener, propertyNames);
  }

  @Override public synchronized boolean removeListener(
      GeneralListener<String, Object> listener, String... propertyNames)
      throws RemoteException
  {
    return property.removeListener(listener, propertyNames);
  }

  @Override public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    property.firePropertyChange(evt.getPropertyName(),
        String.valueOf(evt.getOldValue()), evt.getNewValue());
  }
}