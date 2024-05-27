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

public class AuctionListServer
    implements AuctionListRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
{
  private final AuctionListModel model;
  private final PropertyChangeHandler<String, Object> property;

  public AuctionListServer(AuctionListModel model,
      ListenerSubjectInterface listenerSubject)
      throws MalformedURLException, RemoteException
  {
    this.model = model;

    property = new PropertyChangeHandler<>(this, true);

    listenerSubject.addListener("Auction", this);
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
    Naming.rebind("AuctionListRemote", this);
  }

  @Override public synchronized Auction getAuction(int id)
      throws RemoteException, SQLException
  {
    return model.getAuction(id);
  }

  @Override public synchronized AuctionList getOngoingAuctions()
      throws RemoteException, SQLException
  {
    return model.getOngoingAuctions();
  }

  @Override public synchronized AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    return model.getPreviousBids(bidder);
  }

  @Override public synchronized AuctionList getCreatedAuctions(String seller)
      throws RemoteException, SQLException
  {
    return model.getCreatedAuctions(seller);
  }

  @Override public synchronized boolean isModerator(String email)
      throws RemoteException, SQLException
  {
    return model.isModerator(email);
  }

  @Override public synchronized AuctionList getAllAuctions(
      String moderatorEmail) throws SQLException
  {
    return model.getAllAuctions(moderatorEmail);
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