package model;

import model.domain.*;
import persistence.UserPersistence;
import persistence.UserProtectionProxy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserListModelManager implements UserListModel
{
  private PropertyChangeSupport property;
  private UserPersistence userDatabase;

  public UserListModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    userDatabase = new UserProtectionProxy();
  }

  @Override public synchronized NotificationList getNotifications(
      String receiver) throws SQLException
  {
    return userDatabase.getNotifications(receiver);
  }

  @Override public ArrayList<User> getAllUsers() throws SQLException
  {
    return userDatabase.getAllUsers();
  }

  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    try
    {
      userDatabase.banParticipant(moderatorEmail, participantEmail, reason);
    }
    catch (SQLException e)
    {
      if (e.getMessage().contains("successfully"))
        property.firePropertyChange("Ban", null, participantEmail);
      throw new SQLException(e.getMessage());
    }

  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    return userDatabase.extractBanningReason(email);
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    userDatabase.unbanParticipant(moderatorEmail, participantEmail);
  }


  @Override public synchronized void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

}
