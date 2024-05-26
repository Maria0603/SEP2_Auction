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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AuctionServer
    implements AuctionRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
{
  private AuctionModel model;
  private PropertyChangeHandler<String, Object> property;

  public AuctionServer(AuctionModel model)
      throws MalformedURLException, RemoteException
  {
    this.model = model;
    property = new PropertyChangeHandler<>(this, true);

    model.addListener("Auction", this);
    model.addListener("Time", this);
    model.addListener("End", this);
    model.addListener("Bid", this);
    model.addListener("Notification", this);
    model.addListener("Edit", this);
    model.addListener("Ban", this);
    model.addListener("Reset", this);
    model.addListener("DeleteAuction", this);
    model.addListener("DeleteAccount", this);

    startServer();
  }

  private void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("AuctionRemote", this);
  }

  @Override public synchronized Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller)
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


  @Override public synchronized Bid placeBid(String bidder, int bidValue, int auctionId)
      throws RemoteException, SQLException
  {
    return model.placeBid(bidder, bidValue, auctionId);
  }

  @Override
  public synchronized void buyout(String bidder, int auctionId)
      throws RemoteException, SQLException {

      model.buyout(bidder, auctionId);
  }

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws RemoteException, SQLException
  {
    model.deleteAuction(moderatorEmail, auctionId, reason);
  }


  @Override public synchronized boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException
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
    if(evt.getPropertyName().equals("Bid"))
      System.out.println("Bid received in server");
    property.firePropertyChange(evt.getPropertyName(),
        String.valueOf(evt.getOldValue()), evt.getNewValue());
  }
}