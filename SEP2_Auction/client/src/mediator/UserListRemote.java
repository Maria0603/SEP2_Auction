package mediator;

import model.domain.NotificationList;
import model.domain.User;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The UserListRemote interface defines the remote methods that can be called
 * to interact with the user list on the server.
 */
public interface UserListRemote extends Remote {

  /**
   * Retrieves the list of notifications for a specific receiver.
   *
   * @param receiver the email of the receiver.
   * @return the NotificationList for the specified receiver.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  NotificationList getNotifications(String receiver) throws RemoteException, SQLException;

  /**
   * Retrieves the list of all users.
   *
   * @return an ArrayList of all Users.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  ArrayList<User> getAllUsers() throws RemoteException, SQLException;

  /**
   * Bans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for banning the participant.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  void banParticipant(String moderatorEmail, String participantEmail, String reason) throws RemoteException, SQLException;

  /**
   * Extracts the reason for banning a participant.
   *
   * @param email the email of the banned participant.
   * @return a String indicating the reason for banning.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  String extractBanningReason(String email) throws RemoteException, SQLException;

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  void unbanParticipant(String moderatorEmail, String participantEmail) throws RemoteException, SQLException;

  /**
   * Adds a listener for specific property change events.
   *
   * @param listener the listener to add.
   * @param propertyNames the names of the properties to listen for.
   * @return true if the listener was successfully added, false otherwise.
   * @throws RemoteException if a remote communication error occurs.
   */
  boolean addListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;

  /**
   * Removes a listener for specific property change events.
   *
   * @param listener the listener to remove.
   * @param propertyNames the names of the properties to stop listening for.
   * @return true if the listener was successfully removed, false otherwise.
   * @throws RemoteException if a remote communication error occurs.
   */
  boolean removeListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;
}
