package mediator;

import model.AuctionModel;
import model.UserListModel;
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

public class UserListServer implements UserListRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
  {
    private UserListModel model;
    private PropertyChangeHandler<String, Object> property;

  public UserListServer(UserListModel model)
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
      Naming.rebind("UserListRemote", this);
    }

    @Override public synchronized NotificationList getNotifications(String receiver)
      throws RemoteException, SQLException
    {
      return model.getNotifications(receiver);
    }


    @Override public ArrayList<User> getAllUsers() throws SQLException {
    return model.getAllUsers();
  }


    @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason)
      throws RemoteException, SQLException
    {
      model.banParticipant(moderatorEmail, participantEmail, reason);
    }

    @Override public String extractBanningReason(String email)
      throws RemoteException, SQLException
    {
      return model.extractBanningReason(email);
    }

    @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws RemoteException, SQLException
    {
      model.unbanParticipant(moderatorEmail, participantEmail);
    }


    @Override public synchronized boolean addListener(
        GeneralListener<String, Object> listener,
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
