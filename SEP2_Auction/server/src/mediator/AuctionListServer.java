package mediator;

import model.AuctionListModel;
import model.AuctionModel;
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

public class AuctionListServer implements AuctionListRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
  {
    private AuctionListModel model;
    private PropertyChangeHandler<String, Object> property;

  public AuctionListServer(AuctionListModel model)
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


    @Override public synchronized AuctionList getPreviousBids(String bidder) throws SQLException
    {
      return model.getPreviousBids(bidder);
    }

    @Override public AuctionList getCreatedAuctions(String seller)
      throws RemoteException, SQLException
    {
      return model.getCreatedAuctions(seller);
    }

    @Override public synchronized boolean isModerator(String email)
      throws RemoteException, SQLException
    {
      return model.isModerator(email);
    }


    @Override public AuctionList getAllAuctions(String moderatorEmail) throws SQLException
    {
      return model.getAllAuctions(moderatorEmail);
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