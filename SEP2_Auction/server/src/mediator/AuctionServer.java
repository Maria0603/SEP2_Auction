package mediator;

import model.Auction;
import model.AuctionModel;
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

public class AuctionServer implements AuctionRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
{
  private AuctionModel model;
  private PropertyChangeHandler<String, Object> property;

  public AuctionServer(AuctionModel model) throws MalformedURLException, RemoteException
  {
    this.model=model;
    property=new PropertyChangeHandler<>(this, true);

    ///////////////////////////////////////////////////////
    //model.addListener("Auction", this);
    startRegistry();
    startServer();
  }
  private void startRegistry()
  {
    try
    {
      Registry reg= LocateRegistry.createRegistry(1099);
      System.out.println("Registry started...");
    }
    catch(RemoteException e)
    {
      e.printStackTrace();
    }
  }
  private void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("Connect", this);
  }


  @Override public Auction startAuction(int id, String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      String imagePath) throws RemoteException
  {
    return model.startAuction(id, title, description, reservePrice, buyoutPrice, minimumIncrement, auctionTime, imagePath);
  }

  @Override public Auction getAuction(int id) throws RemoteException
  {
    return model.getAuction(id);
  }

  @Override public int generateID() throws RemoteException
  {
    return model.generateID();
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
    property.firePropertyChange(evt.getPropertyName(), (String) evt.getOldValue(), evt.getNewValue());
  }
}
