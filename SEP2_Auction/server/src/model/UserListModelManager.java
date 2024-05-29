package model;

import model.domain.*;
import persistence.UserPersistence;
import persistence.UserProtectionProxy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The UserListModelManager class implements the UserListModel interface
 * and provides methods for managing user lists and their notifications using a persistence layer.
 */
public class UserListModelManager implements UserListModel
{
  private final PropertyChangeSupport property;
  private final UserPersistence userDatabase;

  /**
   * Constructs a UserListModelManager and initializes the persistence layer.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public UserListModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    userDatabase = new UserProtectionProxy();
  }

  /**
   * Retrieves the notifications for a specific receiver.
   *
   * @param receiver the email or identifier of the receiver.
   * @return the list of notifications for the receiver.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized NotificationList getNotifications(String receiver) throws SQLException
  {
    return userDatabase.getNotifications(receiver);
  }

  /**
   * Retrieves a list of all users.
   *
   * @return a list of all users.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public ArrayList<User> getAllUsers() throws SQLException
  {
    return userDatabase.getAllUsers();
  }

  /**
   * Bans a participant with a specified reason.
   *
   * @param moderatorEmail the email of the moderator performing the ban.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for the ban.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void banParticipant(String moderatorEmail, String participantEmail, String reason) throws SQLException
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

  /**
   * Extracts the reason for banning a participant based on their email.
   *
   * @param email the email of the banned participant.
   * @return the reason for the ban.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public String extractBanningReason(String email) throws SQLException
  {
    return userDatabase.extractBanningReason(email);
  }

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator performing the unban.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void unbanParticipant(String moderatorEmail, String participantEmail) throws SQLException
  {
    userDatabase.unbanParticipant(moderatorEmail, participantEmail);
  }

  /**
   * Adds a listener for a specific property change.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to be added.
   */
  @Override
  public synchronized void addListener(String propertyName, PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a listener for a specific property change.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to be removed.
   */
  @Override
  public synchronized void removeListener(String propertyName, PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
