
package mediator;

import model.Auction;
import model.AuctionList;
import model.AuctionModel;
import model.NotificationList;
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

    startRegistry();
    startServer();
  }

  private void startRegistry()
  {
    try
    {
      Registry reg = LocateRegistry.createRegistry(1099);
      System.out.println("Registry started...");
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }

  private void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("Connect", this);
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData)
      throws RemoteException, SQLException, ClassNotFoundException
  {
    return model.startAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData);
  }

  @Override public Auction getAuction(int id)
      throws RemoteException, SQLException
  {
    return model.getAuction(id);
  }

  @Override public AuctionList getOngoingAuctions()
      throws RemoteException, SQLException
  {
    return model.getOngoingAuctions();
  }

  @Override public NotificationList getNotifications(String receiver)
      throws RemoteException, SQLException
  {
    return model.getNotifications(receiver);
  }

  @Override public boolean addListener(GeneralListener<String, Object> listener,
      String... propertyNames) throws RemoteException
  {
    return property.addListener(listener, propertyNames);
  }

  @Override public boolean removeListener(
      GeneralListener<String, Object> listener, String... propertyNames)
      throws RemoteException
  {
    return property.removeListener(listener, propertyNames);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    property.firePropertyChange(evt.getPropertyName(),
        String.valueOf(evt.getOldValue()), evt.getNewValue());
  }
}
