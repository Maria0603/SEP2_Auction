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

/**
 * The UserListClient class implements the UserListModel interface and acts as a client
 * to interact with the UserListRemote server. It also implements RemoteListener to
 * listen for remote events.
 */
public class UserListClient implements RemoteListener<String, Object>, UserListModel {

  private UserListRemote server;
  private final PropertyChangeSupport property;

  /**
   * Constructs a UserListClient and starts the connection to the server.
   *
   * @throws IOException if an I/O error occurs.
   */
  public UserListClient() throws IOException {
    start();
    property = new PropertyChangeSupport(this);
  }

  /**
   * Establishes a connection to the server and registers the client as a listener
   * for various events.
   */
  private void start() {
    try {
      UnicastRemoteObject.exportObject(this, 0);
      server = (UserListRemote) Naming.lookup("rmi://localhost:1099/UserListRemote");

      server.addListener(this, "Notification");
      server.addListener(this, "Ban");
      server.addListener(this, "Bid");
      server.addListener(this, "Edit");
      server.addListener(this, "DeleteAccount");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves the list of notifications for a specific receiver.
   *
   * @param receiver the email of the receiver.
   * @return the NotificationList for the specified receiver.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public NotificationList getNotifications(String receiver) throws IllegalArgumentException {
    try {
      return server.getNotifications(receiver);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Retrieves the list of all users.
   *
   * @return an ArrayList of all Users.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public ArrayList<User> getAllUsers() throws IllegalArgumentException {
    try {
      return server.getAllUsers();
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Bans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for banning the participant.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void banParticipant(String moderatorEmail, String participantEmail, String reason) throws IllegalArgumentException {
    try {
      server.banParticipant(moderatorEmail, participantEmail, reason);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Extracts the reason for banning a participant.
   *
   * @param email the email of the banned participant.
   * @return a String indicating the reason for banning.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public String extractBanningReason(String email) throws IllegalArgumentException {
    try {
      return server.extractBanningReason(email);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void unbanParticipant(String moderatorEmail, String participantEmail) throws IllegalArgumentException {
    try {
      server.unbanParticipant(moderatorEmail, participantEmail);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Adds a listener for property change events.
   *
   * @param s the property name.
   * @param propertyChangeListener the listener to add.
   */
  @Override
  public void addListener(String s, PropertyChangeListener propertyChangeListener) {
    property.addPropertyChangeListener(s, propertyChangeListener);
  }

  /**
   * Removes a listener for property change events.
   *
   * @param s the property name.
   * @param propertyChangeListener the listener to remove.
   */
  @Override
  public void removeListener(String s, PropertyChangeListener propertyChangeListener) {
    property.removePropertyChangeListener(s, propertyChangeListener);
  }

  /**
   * Handles property change events and fires them to registered listeners.
   *
   * @param event the observer event containing the property change information.
   * @throws RemoteException if a remote communication error occurs.
   */
  @Override
  public void propertyChange(ObserverEvent<String, Object> event) throws RemoteException {
    property.firePropertyChange(event.getPropertyName(), event.getValue1(), event.getValue2());
  }
}
