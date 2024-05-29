package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.time.LocalDate;

/**
 * The UserCacheProxy class acts as a proxy for the UserModel and implements caching
 * for user-related data. It listens to changes from the UserModelManager and updates
 * the cache accordingly.
 */
public class UserCacheProxy extends CacheProxy implements UserModel, PropertyChangeListener {

  private final UserModelManager modelManager;
  private final PropertyChangeSupport property;

  /**
   * Constructs a new UserCacheProxy object and initializes the cache and listeners.
   *
   * @throws IllegalArgumentException if an illegal argument is provided.
   * @throws IOException if an I/O error occurs.
   */
  public UserCacheProxy() throws IllegalArgumentException, IOException {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new UserModelManager();

    modelManager.addListener("Edit", this);
    modelManager.addListener("Reset", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("Notification", this);
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
    String userEmail = modelManager.addUser(firstname, lastname, email, password, repeatedPassword, phone, birthday);
    super.setUserEmail(userEmail);
    return userEmail;
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
    String userEmail = modelManager.login(email, password);
    super.setUserEmail(userEmail);
    return userEmail;
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
    modelManager.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
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
    return modelManager.getUser(email);
  }

  /**
   * Retrieves moderator information.
   *
   * @return the moderator's User information.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  @Override
  public User getModeratorInfo() throws IllegalArgumentException {
    return modelManager.getModeratorInfo();
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
    return modelManager.isModerator(email);
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
    return modelManager.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
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
    modelManager.deleteAccount(email, password);
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
   * Handles the "Edit" event and updates the user email accordingly.
   *
   * @param evt the property change event.
   */
  private void receivedEdit(PropertyChangeEvent evt) {
    if (super.getUserEmail().get().equals(evt.getOldValue().toString())) {
      super.setUserEmail(evt.getNewValue().toString());
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
      case "Edit" -> receivedEdit(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
  }
}
