package mediator;

import model.domain.NotificationList;
import model.domain.User;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The UserListRemote interface provides methods for managing users and their notifications remotely.
 * It extends the Remote interface to support RMI (Remote Method Invocation).
 */
public interface UserListRemote extends Remote
{
  /**
   * Retrieves the notifications for a specific receiver.
   *
   * @param receiver the email or identifier of the receiver.
   * @return the list of notifications for the receiver.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  NotificationList getNotifications(String receiver) throws RemoteException, SQLException;

  /**
   * Retrieves a list of all users.
   *
   * @return a list of all users.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  ArrayList<User> getAllUsers() throws RemoteException, SQLException;

  /**
   * Bans a participant with a specified reason.
   *
   * @param moderatorEmail the email of the moderator performing the ban.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for the ban.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  void banParticipant(String moderatorEmail, String participantEmail, String reason) throws RemoteException, SQLException;

  /**
   * Extracts the reason for banning a participant based on their email.
   *
   * @param email the email of the banned participant.
   * @return the reason for the ban.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  String extractBanningReason(String email) throws RemoteException, SQLException;

  /**
   * Unbans a participant.
   *
   * @param moderatorEmail the email of the moderator performing the unban.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  void unbanParticipant(String moderatorEmail, String participantEmail) throws RemoteException, SQLException;

  /**
   * Adds a listener for specific property changes.
   *
   * @param listener the listener to be added.
   * @param propertyNames the properties to listen for.
   * @return true if the listener was added successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  boolean addListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;

  /**
   * Removes a listener for specific property changes.
   *
   * @param listener the listener to be removed.
   * @param propertyNames the properties to stop listening for.
   * @return true if the listener was removed successfully, false otherwise.
   * @throws RemoteException if there is an RMI error.
   */
  boolean removeListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException;
}
