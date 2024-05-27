package model;

import mediator.UserListClient;
import model.domain.NotificationList;
import model.domain.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserListModelManager
    implements UserListModel, PropertyChangeListener
{
  private final PropertyChangeSupport property;
  private final UserListClient client;

  public UserListModelManager() throws IOException, SQLException
  {
    property = new PropertyChangeSupport(this);
    client = new UserListClient();

    client.addListener("Notification", this);
    client.addListener("Ban", this);
    client.addListener("Bid", this);
    client.addListener("Edit", this);
    client.addListener("DeleteAccount", this);

  }

  @Override public NotificationList getNotifications(String receiver)
      throws SQLException
  {
    return client.getNotifications(receiver);
  }

  @Override public ArrayList<User> getAllUsers() throws SQLException
  {
    return client.getAllUsers();
  }

  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    client.banParticipant(moderatorEmail, participantEmail, reason);
  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    return client.extractBanningReason(email);
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    client.unbanParticipant(moderatorEmail, participantEmail);
  }

  @Override public void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}
