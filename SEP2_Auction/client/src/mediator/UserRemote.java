package mediator;

import model.domain.User;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * The UserRemote interface defines the remote methods that can be called
 * to interact with individual user operations on the server.
 */
public interface UserRemote extends Remote {

  /**
   * Adds a new user to the system.
   *
   * @param firstname the first name of the user.
   * @param lastname the last name of the user.
   * @param email the email of the user.
   * @param password the password of the user.
   * @param repeatedPassword the repeated password of the user.
   * @param phone the phone number of the user.
   * @param birthday the birthday of the user.
   * @return a String message indicating the result of the operation.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  String addUser(String firstname, String lastname, String email, String password,
                 String repeatedPassword, String phone, LocalDate birthday) throws RemoteException, SQLException;

  /**
   * Logs in a user.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return a String message indicating the result of the operation.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  String login(String email, String password) throws RemoteException, SQLException;

  /**
   * Resets the password of a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password of the user.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  void resetPassword(String userEmail, String oldPassword, String newPassword,
                     String repeatPassword) throws RemoteException, SQLException;

  /**
   * Retrieves a user by email.
   *
   * @param email the email of the user.
   * @return the User with the specified email.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  User getUser(String email) throws RemoteException, SQLException;

  /**
   * Retrieves moderator information.
   *
   * @return the moderator's User information.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  User getModeratorInfo() throws RemoteException, SQLException;

  /**
   * Checks if a user is a moderator.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  boolean isModerator(String email) throws RemoteException, SQLException;

  /**
   * Edits the information of a user.
   *
   * @param oldEmail the old email of the user.
   * @param firstname the new first name of the user.
   * @param lastname the new last name of the user.
   * @param email the new email of the user.
   * @param password the new password of the user.
   * @param phone the new phone number of the user.
   * @param birthday the new birthday of the user.
   * @return the updated User.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  User editInformation(String oldEmail, String firstname, String lastname, String email,
                       String password, String phone, LocalDate birthday) throws RemoteException, SQLException;

  /**
   * Deletes a user account.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws RemoteException if a remote communication error occurs.
   * @throws SQLException if a database access error occurs.
   */
  void deleteAccount(String email, String password) throws RemoteException, SQLException;

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
