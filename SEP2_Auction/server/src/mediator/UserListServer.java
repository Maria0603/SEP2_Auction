package mediator;

import model.AuctionModel;
import model.ListenerSubjectInterface;
import model.UserListModel;
import model.UserModel;
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
    private ListenerSubjectInterface listenerSubject;
    private PropertyChangeHandler<String, Object> property;

  public UserListServer(UserListModel model, ListenerSubjectInterface listenerSubject)
      throws MalformedURLException, RemoteException
    {
      this.model = model;
      this.listenerSubject=listenerSubject;
      property = new PropertyChangeHandler<>(this, true);
      this.listenerSubject.addListener("Edit", this);
      this.listenerSubject.addListener("DeleteAccount", this);
      this.listenerSubject.addListener("Bid", this);
      this.listenerSubject.addListener("Notification", this);
      this.listenerSubject.addListener("Ban", this);

      startServer();
    }


    private synchronized void startServer() throws RemoteException, MalformedURLException
    {
      UnicastRemoteObject.exportObject(this, 0);
      Naming.rebind("UserListRemote", this);
    }

    @Override public synchronized NotificationList getNotifications(String receiver)
      throws RemoteException, SQLException
    {
      return model.getNotifications(receiver);
    }


    @Override public synchronized ArrayList<User> getAllUsers() throws SQLException {
    return model.getAllUsers();
  }


    @Override public synchronized void banParticipant(String moderatorEmail,
      String participantEmail, String reason)
      throws RemoteException, SQLException
    {
      model.banParticipant(moderatorEmail, participantEmail, reason);
    }

    @Override public synchronized String extractBanningReason(String email)
      throws RemoteException, SQLException
    {
      return model.extractBanningReason(email);
    }

    @Override public synchronized void unbanParticipant(String moderatorEmail,
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
