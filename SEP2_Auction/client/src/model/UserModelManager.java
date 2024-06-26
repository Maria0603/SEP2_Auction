package model;

import mediator.UserClient;
import model.domain.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.time.LocalDate;

/**
 * The UserModelManager class implements the UserModel interface
 * and manages user-related operations. It acts as an intermediary between the client and the data model,
 * handling property change events and notifying listeners of changes.
 */
public class UserModelManager implements UserModel, PropertyChangeListener {

  private final PropertyChangeSupport property;
  private final UserClient client;

  /**
   * Constructs a new UserModelManager and initializes the client and listeners.
   *
   * @throws IOException if an I/O error occurs.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  public UserModelManager() throws IOException, IllegalArgumentException {
    property = new PropertyChangeSupport(this);
    client = new UserClient();

    client.addListener("Notification", this);
    client.addListener("Ban", this);
    client.addListener("Reset", this);
    client.addListener("Edit", this);
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
   * @return the email of the newly added user.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws IllegalArgumentException {
    return client.addUser(firstname, lastname, email, password, repeatedPassword, phone, birthday);
  }

  /**
   * Logs in a user.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return the email of the logged-in user.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public String login(String email, String password) throws IllegalArgumentException {
    return client.login(email, password);
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
  public void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws IllegalArgumentException {
    client.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
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
    return client.getUser(email);
  }

  /**
   * Retrieves moderator information.
   *
   * @return the moderator's User information.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public User getModeratorInfo() throws IllegalArgumentException {
    return client.getModeratorInfo();
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
    return client.isModerator(email);
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
  public User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws IllegalArgumentException {
    return client.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
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
    client.deleteAccount(email, password);
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
    // model manager property fires user events further
    property.firePropertyChange(evt);
  }
}
