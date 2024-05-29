package mediator;

import model.ListenerSubjectInterface;
import model.UserListModel;
import model.domain.*;
import utility.observer.listener.GeneralListener;
import utility.observer.subject.PropertyChangeHandler;
import utility.observer.subject.RemoteSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The UserListServer class implements the UserListRemote and RemoteSubject interfaces,
 * and serves as a remote server for managing users and their notifications.
 */
public class UserListServer implements UserListRemote, RemoteSubject<String, Object>, PropertyChangeListener
{
  private final UserListModel model;
  private final PropertyChangeHandler<String, Object> property;

  /**
   * Constructs a UserListServer with the specified model and listener subject.
   *
   * @param model the user list model.
   * @param listenerSubject the listener subject interface.
   * @throws MalformedURLException if the provided URL is malformed.
   * @throws RemoteException if there is an RMI error.
   */
  public UserListServer(UserListModel model, ListenerSubjectInterface listenerSubject)
          throws MalformedURLException, RemoteException
  {
    this.model = model;
    property = new PropertyChangeHandler<>(this, true);
    listenerSubject.addListener("Edit", this);
    listenerSubject.addListener("DeleteAccount", this);
    listenerSubject.addListener("Bid", this);
    listenerSubject.addListener("Notification", this);
    listenerSubject.addListener("Ban", this);

    startServer();
  }

  /**
   * Starts the server and binds the UserListRemote object.
   *
   * @throws RemoteException if there is an RMI error.
   * @throws MalformedURLException if the provided URL is malformed.
   */
  private synchronized void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("UserListRemote", this);
  }

  /**
   * Retrieves the notifications for a specific receiver.
   *
   * @param receiver the email or identifier of the receiver.
   * @return the list of notifications for the receiver.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized NotificationList getNotifications(String receiver) throws RemoteException, SQLException
  {
    return model.getNotifications(receiver);
  }

  /**
   * Retrieves a list of all users.
   *
   * @return a list of all users.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized ArrayList<User> getAllUsers() throws SQLException
  {
    return model.getAllUsers();
  }

  /**
   * Bans a participant with a specified reason.
   *
   * @param moderatorEmail the email of the moderator performing the ban.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for the ban.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized void banParticipant(String moderatorEmail, String participantEmail, String reason)
          throws RemoteException, SQLException
  {
    model.banParticipant(moderatorEmail, participantEmail, reason);
  }

  /**
   * Extracts the reason for banning a participant based on their email.
   *
   * @param email the email of the banned participant.
   * @return the reason for the ban.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized String extractBanningReason(String email) throws RemoteException, SQLException
  {
    return model.extractBanningReason(email);
  }

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator performing the unban.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized void unbanParticipant(String moderatorEmail, String participantEmail) throws RemoteException, SQLException
  {
    model.unbanParticipant(moderatorEmail, participantEmail);
  }

  /**
   * Adds a listener for specific property changes.
   *
   * @param listener the listener to be added.
   * @param propertyNames the properties to listen for.
   * @return true if the listener was added successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  @Override
  public synchronized boolean addListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException
  {
    return property.addListener(listener, propertyNames);
  }

  /**
   * Removes a listener for specific property changes.
   *
   * @param listener the listener to be removed.
   * @param propertyNames the properties to stop listening for.
   * @return true if the listener was removed successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  @Override
  public synchronized boolean removeListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException
  {
    return property.removeListener(listener, propertyNames);
  }

  /**
   * Handles property change events and fires property change notifications.
   *
   * @param evt the property change event.
   */
  @Override
  public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    property.firePropertyChange(evt.getPropertyName(), String.valueOf(evt.getOldValue()), evt.getNewValue());
  }
}
