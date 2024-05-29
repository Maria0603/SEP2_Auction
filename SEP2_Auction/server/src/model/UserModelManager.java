package model;

import model.domain.*;
import persistence.UserPersistence;
import persistence.UserProtectionProxy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * The UserModelManager class implements the UserModel interface
 * and provides methods for managing user accounts and information using a persistence layer.
 */
public class UserModelManager implements UserModel
{
  private final PropertyChangeSupport property;
  private final UserPersistence userDatabase;

  /**
   * Constructs a UserModelManager and initializes the persistence layer.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public UserModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    userDatabase = new UserProtectionProxy();
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
   * @return the email of the created user.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized String addUser(String firstname, String lastname, String email, String password, String repeatedPassword,
                                     String phone, LocalDate birthday) throws SQLException
  {
    return userDatabase.createUser(firstname, lastname, email, password, repeatedPassword, phone, birthday).getEmail();
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
  public synchronized String login(String email, String password) throws SQLException
  {
    return userDatabase.login(email, password);
  }

  /**
   * Resets the password for a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password for verification.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws SQLException
  {
    userDatabase.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
    property.firePropertyChange("Reset", null, userEmail);
  }

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized User getUser(String email) throws SQLException
  {
    return userDatabase.getUserInfo(email);
  }

  /**
   * Retrieves the information of the moderator.
   *
   * @return the User object containing moderator information.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized User getModeratorInfo() throws SQLException
  {
    return userDatabase.getModeratorInfo();
  }

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized boolean isModerator(String email) throws SQLException
  {
    return userDatabase.isModerator(email);
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
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized User editInformation(String oldEmail, String firstname, String lastname, String email, String password,
                                           String phone, LocalDate birthday) throws SQLException
  {
    User user = userDatabase.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
    if (!oldEmail.equals(email))
      property.firePropertyChange("Edit", oldEmail, email);
    return user;
  }

  /**
   * Deletes a user account.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void deleteAccount(String email, String password) throws SQLException
  {
    userDatabase.deleteAccount(email, password);
    property.firePropertyChange("DeleteAccount", null, null);
  }

  /**
   * Adds a listener for a specific property change.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to be added.
   */
  @Override
  public synchronized void addListener(String propertyName, PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a listener for a specific property change.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to be removed.
   */
  @Override
  public synchronized void removeListener(String propertyName, PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
