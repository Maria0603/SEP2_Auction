package model;

import javafx.application.Platform;
import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The UserListCacheProxy class acts as a proxy for the UserListModel and implements caching
 * for user-related data such as notifications. It listens to changes from the UserListModelManager
 * and updates the cache accordingly.
 */
public class UserListCacheProxy extends CacheProxy implements UserListModel, PropertyChangeListener {

  private NotificationList notificationsCache;
  private final UserListModelManager modelManager;
  private final PropertyChangeSupport property;

  /**
   * Constructs a new UserListCacheProxy object and initializes the cache and listeners.
   *
   * @throws IllegalArgumentException if an illegal argument is provided.
   * @throws IOException if an I/O error occurs.
   */
  public UserListCacheProxy() throws IllegalArgumentException, IOException {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new UserListModelManager();
    modelManager.addListener("Bid", this);
    modelManager.addListener("Notification", this);
    modelManager.addListener("Edit", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("DeleteAccount", this);

    notificationsCache = new NotificationList();
    super.getUserEmail().addListener((observable, oldValue, newValue) -> {
      try {
        updateCache(userEmail.get());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
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
    if (notificationsCache.getSize() == 0)
      notificationsCache = modelManager.getNotifications(receiver);
    return notificationsCache;
  }

  /**
   * Retrieves the list of all users.
   *
   * @return the list of all users.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public ArrayList<User> getAllUsers() throws IllegalArgumentException {
    return modelManager.getAllUsers();
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
    modelManager.banParticipant(moderatorEmail, participantEmail, reason);
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
    return modelManager.extractBanningReason(email);
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
    modelManager.unbanParticipant(moderatorEmail, participantEmail);
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
   * Updates the cache by fetching data from the model manager.
   *
   * @param userEmail the email of the user.
   * @throws SQLException if a database access error occurs.
   */
  private void updateCache(String userEmail) throws SQLException {
    notificationsCache = modelManager.getNotifications(userEmail);
  }

  /**
   * Handles the "Notification" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedNotification(PropertyChangeEvent evt) {
    Notification notification = (Notification) evt.getNewValue();
    if (notification.getReceiver().equals(super.getUserEmail().get()))
      notificationsCache.addNotification(notification);
  }

  /**
   * Handles the "Edit" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedEdit(PropertyChangeEvent evt) {
    try {
      updateCache(super.getUserEmail().get());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles the "Ban" or "DeleteAccount" event and updates the cache accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedBanOrDeleteAccount(PropertyChangeEvent evt) {
    try {
      updateCache(super.getUserEmail().get());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param evt the property change event containing the property change information.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "Notification" -> receivedNotification(evt);
      case "Edit" -> receivedEdit(evt);
      case "Ban", "DeleteAccount" -> receivedBanOrDeleteAccount(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
  }
}
