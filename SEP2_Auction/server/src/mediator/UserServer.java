package mediator;

import model.ListenerSubjectInterface;
import model.UserModel;
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
import java.time.LocalDate;

/**
 * The UserServer class implements the UserRemote and RemoteSubject interfaces,
 * and serves as a remote server for managing user accounts.
 */
public class UserServer implements UserRemote, RemoteSubject<String, Object>, PropertyChangeListener
{
  private final UserModel model;
  private final PropertyChangeHandler<String, Object> property;

  /**
   * Constructs a UserServer with the specified model and listener subject.
   *
   * @param model the user model.
   * @param listenerSubject the listener subject interface.
   * @throws MalformedURLException if the provided URL is malformed.
   * @throws RemoteException if there is an RMI error.
   */
  public UserServer(UserModel model, ListenerSubjectInterface listenerSubject)
          throws MalformedURLException, RemoteException
  {
    this.model = model;
    property = new PropertyChangeHandler<>(this, true);

    listenerSubject.addListener("Ban", this);
    listenerSubject.addListener("Notification", this);
    listenerSubject.addListener("Edit", this);
    listenerSubject.addListener("Reset", this);

    startServer();
  }

  /**
   * Starts the server and binds the UserRemote object.
   *
   * @throws RemoteException if there is an RMI error.
   * @throws MalformedURLException if the provided URL is malformed.
   */
  private synchronized void startServer() throws RemoteException, MalformedURLException
  {
    UnicastRemoteObject.exportObject(this, 0);
    Naming.rebind("UserRemote", this);
  }

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
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException {
    return model.addUser(firstname, lastname, email, password, repeatedPassword, phone, birthday);
  }

  /**
   * Logs in a user with the specified email and password.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return a confirmation message or error message.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized String login(String email, String password) throws SQLException {
    System.out.println("UserServer: " + email + ", " + password);
    return model.login(email, password);
  }

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
  @Override
  public synchronized void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws RemoteException, SQLException {
    model.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
  }

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized User getUser(String email) throws RemoteException, SQLException {
    return model.getUser(email);
  }

  /**
   * Retrieves the information of the moderator.
   *
   * @return the User object containing moderator information.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public User getModeratorInfo() throws SQLException {
    return model.getModeratorInfo();
  }

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized boolean isModerator(String email) throws RemoteException, SQLException {
    return model.isModerator(email);
  }

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
  @Override
  public synchronized User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws RemoteException, SQLException {
    return model.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
  }

  /**
   * Deletes a user account.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws RemoteException if there is an RMI error.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized void deleteAccount(String email, String password) throws RemoteException, SQLException {
    model.deleteAccount(email, password);
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
  public synchronized boolean addListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException {
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
  public synchronized boolean removeListener(GeneralListener<String, Object> listener, String... propertyNames) throws RemoteException {
    return property.removeListener(listener, propertyNames);
  }

  /**
   * Handles property change events and fires property change notifications.
   *
   * @param evt the property change event.
   */
  @Override
  public synchronized void propertyChange(PropertyChangeEvent evt) {
    property.firePropertyChange(evt.getPropertyName(), String.valueOf(evt.getOldValue()), evt.getNewValue());
  }
}
