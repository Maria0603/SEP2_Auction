package mediator;

import model.AuctionModel;
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

public class UserServer implements UserRemote, RemoteSubject<String, Object>,
    PropertyChangeListener
  {
    private UserModel model;
    private PropertyChangeHandler<String, Object> property;

  public UserServer(UserModel model)
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
      Naming.rebind("UserRemote", this);
    }

    @Override
    public synchronized String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate
    birthday) throws SQLException {
    return model.addUser(firstname,lastname,email,password, repeatedPassword, phone, birthday);
  }

    @Override
    public synchronized String login(String email, String password) throws SQLException {
    System.out.println("AuctionServer: " + email + ", " + password);
    return model.login(email,password);
  }

    @Override public synchronized void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword)
      throws RemoteException, SQLException
    {
      model.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
    }

    @Override public synchronized User getUser(String email) throws RemoteException, SQLException
    {
      return model.getUser(email);
    }

    @Override public User getModeratorInfo() throws SQLException
    {
      return model.getModeratorInfo();
    }

    @Override public synchronized boolean isModerator(String email)
      throws RemoteException, SQLException
    {
      return model.isModerator(email);
    }

    @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws RemoteException, SQLException
    {
      return model.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
    }


    @Override
    public void deleteAccount(String email, String password) throws RemoteException, SQLException {
    model.deleteAccount(email, password);
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
