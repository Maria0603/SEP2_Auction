package model;

import mediator.UserListClient;
import model.domain.NotificationList;
import model.domain.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The UserListModelManager class implements the UserListModel interface
 * and manages user lists. It acts as an intermediary between the client and the data model,
 * handling property change events and notifying listeners of changes.
 */
public class UserListModelManager implements UserListModel, PropertyChangeListener {

  private final PropertyChangeSupport property;
  private final UserListClient client;

  /**
   * Constructs a new UserListModelManager and initializes the client and listeners.
   *
   * @throws IOException if an I/O error occurs.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  public UserListModelManager() throws IOException, IllegalArgumentException {
    property = new PropertyChangeSupport(this);
    client = new UserListClient();

    client.addListener("Notification", this);
    client.addListener("Ban", this);
    client.addListener("Bid", this);
    client.addListener("Edit", this);
    client.addListener("DeleteAccount", this);
  }

  /**
   * Retrieves the list of notifications for a receiver.
   *
   * @param receiver the receiver of the notifications.
   * @return the list of notifications.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public NotificationList getNotifications(String receiver) throws IllegalArgumentException {
    return client.getNotifications(receiver);
  }

  /**
   * Retrieves the list of all users.
   *
   * @return the list of all users.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public ArrayList<User> getAllUsers() throws IllegalArgumentException {
    return client.getAllUsers();
  }

  /**
   * Bans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to ban.
   * @param reason the reason for banning the participant.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void banParticipant(String moderatorEmail, String participantEmail, String reason) throws IllegalArgumentException {
    client.banParticipant(moderatorEmail, participantEmail, reason);
  }

  /**
   * Extracts the reason for banning a participant.
   *
   * @param email the email of the participant.
   * @return the reason for banning the participant.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public String extractBanningReason(String email) throws IllegalArgumentException {
    return client.extractBanningReason(email);
  }

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to unban.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void unbanParticipant(String moderatorEmail, String participantEmail) throws IllegalArgumentException {
    client.unbanParticipant(moderatorEmail, participantEmail);
  }

  /**
   * Adds a listener for property change events.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to add.
   */
  @Override
  public void addListener(String propertyName, PropertyChangeListener listener) {
    property.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a listener for property change events.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to remove.
   */
  @Override
  public void removeListener(String propertyName, PropertyChangeListener listener) {
    property.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param evt the property change event containing the property change information.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}
