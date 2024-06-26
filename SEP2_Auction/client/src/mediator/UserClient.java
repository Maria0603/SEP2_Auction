package mediator;

import model.AuctionListModel;
import model.AuctionModel;
import model.UserListModel;
import model.UserModel;
import model.domain.AuctionList;
import model.domain.User;
import utility.observer.event.ObserverEvent;
import utility.observer.listener.GeneralListener;
import utility.observer.listener.RemoteListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The UserClient class implements the UserModel interface and acts as a client
 * to interact with the UserRemote server. It also implements RemoteListener to
 * listen for remote events.
 */
public class UserClient implements RemoteListener<String, Object>, UserModel {
  private UserRemote server;
  private final PropertyChangeSupport property;

  /**
   * Constructs a UserClient and starts the connection to the server.
   *
   * @throws IOException if an I/O error occurs.
   */
  public UserClient() throws IOException {
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
      server = (UserRemote) Naming.lookup("rmi://localhost:1099/UserRemote");

      server.addListener(this, "Notification");
      server.addListener(this, "Ban");
      server.addListener(this, "Reset");
      server.addListener(this, "Edit");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

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
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public String addUser(String firstname, String lastname, String email, String password,
                        String repeatedPassword, String phone, LocalDate birthday) throws IllegalArgumentException {
    try {
      return server.addUser(firstname, lastname, email, password, repeatedPassword, phone, birthday);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Logs in a user.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return a String message indicating the result of the operation.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public String login(String email, String password) throws IllegalArgumentException {
    try {
      return server.login(email, password);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Resets the password of a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password of the user.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void resetPassword(String userEmail, String oldPassword, String newPassword,
                            String repeatPassword) throws IllegalArgumentException {
    try {
      server.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Retrieves a user by email.
   *
   * @param email the email of the user.
   * @return the User with the specified email.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public User getUser(String email) throws IllegalArgumentException {
    try {
      return server.getUser(email);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Retrieves moderator information.
   *
   * @return the moderator's User information.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public User getModeratorInfo() throws IllegalArgumentException {
    try {
      return server.getModeratorInfo();
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Checks if a user is a moderator.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public boolean isModerator(String email) throws IllegalArgumentException {
    try {
      return server.isModerator(email);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return false;
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
   * @return the updated User.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public User editInformation(String oldEmail, String firstname, String lastname,
                              String email, String password, String phone, LocalDate birthday) throws IllegalArgumentException {
    try {
      return server.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Deletes a user account.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public void deleteAccount(String email, String password) throws IllegalArgumentException {
    try {
      server.deleteAccount(email, password);
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
