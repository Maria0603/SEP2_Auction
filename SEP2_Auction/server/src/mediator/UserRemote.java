package mediator;

import model.domain.User;
import utility.observer.listener.GeneralListener;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * The UserRemote interface provides methods for managing user accounts remotely.
 * It extends the Remote interface to support RMI (Remote Method Invocation).
 */
public interface UserRemote extends Remote
{
  /**
   * Adds a new user with the specified details.
   *
   * @param firstname the first name of the user.
   * @param lastname the last name of the user.
   * @param email the email of the user.
   * @param password the password of the user.
   * @param repeatedPassword the repeated password for verification.
   * @param phone the phone number of the user.
   * @param birthday the birthday of the user.
   * @return a confirmation message or error message.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  String addUser(String firstname, String lastname, String email,
                 String password, String repeatedPassword, String phone,
                 LocalDate birthday) throws RemoteException, SQLException;

  /**
   * Logs in a user with the specified email and password.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return a confirmation message or error message.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  String login(String email, String password) throws RemoteException, SQLException;

  /**
   * Resets the password for a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password for verification.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  void resetPassword(String userEmail, String oldPassword, String newPassword,
                     String repeatPassword) throws RemoteException, SQLException;

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  User getUser(String email) throws RemoteException, SQLException;

  /**
   * Retrieves the information of the moderator.
   *
   * @return the User object containing moderator information.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  User getModeratorInfo() throws RemoteException, SQLException;

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
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
   * @return the updated User object.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  User editInformation(String oldEmail, String firstname, String lastname,
                       String email, String password, String phone, LocalDate birthday)
          throws RemoteException, SQLException;

  /**
   * Deletes a user account.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  void deleteAccount(String email, String password) throws RemoteException, SQLException;

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
