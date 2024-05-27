package mediator;

import model.AuctionListModel;
import model.AuctionModel;
import model.UserListModel;
import model.UserModel;
import model.domain.AuctionList;
import model.domain.User;
import utility.observer.event.ObserverEvent;
import utility.observer.listener.GeneralListener;
import utility.observer.listener.RemoteListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class UserClient  implements RemoteListener<String, Object>, UserModel
{
  private UserRemote server;
  private PropertyChangeSupport property;

  public UserClient() throws IOException
  {
    start();
    property = new PropertyChangeSupport(this);
  }

  private void start()
  {
    try
    {
      UnicastRemoteObject.exportObject(this, 0);
      server = (UserRemote) Naming.lookup("rmi://localhost:1099/UserRemote");

      server.addListener(this, "Notification");
      server.addListener(this, "Ban");
      server.addListener(this, "Reset");
      server.addListener(this, "Edit");

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override public String addUser(String firstname, String lastname,
      String email, String password, String repeatedPassword, String phone,
      LocalDate birthday) throws SQLException
  {
    try
    {
      return server.addUser(firstname, lastname, email, password,
          repeatedPassword, phone, birthday);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public String login(String email, String password)
      throws SQLException
  {
    try
    {
      return server.login(email, password);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;

  }


  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    try
    {
      server.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }

  @Override public User getUser(String email) throws SQLException
  {
    try
    {
      return server.getUser(email);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public User getModeratorInfo() throws SQLException
  {
    try
    {
      return server.getModeratorInfo();
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    try
    {
      return server.isModerator(email);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return false;
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
    try
    {
      return server.editInformation(oldEmail, firstname, lastname, email,
          password, phone, birthday);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override public void deleteAccount(String email, String password)
      throws SQLException
  {
    try
    {
      server.deleteAccount(email, password);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
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
