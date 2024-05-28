package mediator;

import model.UserListModel;
import model.domain.*;
import utility.observer.event.ObserverEvent;
import utility.observer.listener.RemoteListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserListClient
    implements RemoteListener<String, Object>, UserListModel
{

  private UserListRemote server;
  private final PropertyChangeSupport property;

  public UserListClient() throws IOException
  {
    start();
    property = new PropertyChangeSupport(this);
  }

  // establish server connection
  private void start()
  {
    try
    {
      UnicastRemoteObject.exportObject(this, 0);
      server = (UserListRemote) Naming.lookup(
          "rmi://localhost:1099/UserListRemote");

      server.addListener(this, "Notification");
      server.addListener(this, "Ban");
      server.addListener(this, "Bid");
      server.addListener(this, "Edit");
      server.addListener(this, "DeleteAccount");

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override public NotificationList getNotifications(String receiver)
      throws IllegalArgumentException
  {
    try
    {
      return server.getNotifications(receiver);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public ArrayList<User> getAllUsers() throws IllegalArgumentException
  {
    try
    {
      return server.getAllUsers();
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws IllegalArgumentException
  {
    try
    {
      server.banParticipant(moderatorEmail, participantEmail, reason);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  @Override public String extractBanningReason(String email) throws IllegalArgumentException
  {
    try
    {
      return server.extractBanningReason(email);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws IllegalArgumentException
  {
    try
    {
      server.unbanParticipant(moderatorEmail, participantEmail);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      throw new IllegalArgumentException(e.getMessage());
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
