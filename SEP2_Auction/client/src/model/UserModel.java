package model;

import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;
import java.time.LocalDate;

/**
 * The UserModel interface defines the methods for interacting with user-related data,
 * including adding a user, logging in, resetting a password, retrieving user information,
 * checking if a user is a moderator, editing user information, and deleting a user account.
 * It extends NamedPropertyChangeSubject to support property change notifications.
 */
public interface UserModel extends NamedPropertyChangeSubject {

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
  String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws IllegalArgumentException;

  /**
   * Logs in a user.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return the email of the logged-in user.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  String login(String email, String password) throws IllegalArgumentException;

  /**
   * Resets the password of a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password of the user.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws IllegalArgumentException;

  /**
   * Retrieves a user by email.
   *
   * @param email the email of the user.
   * @return the User with the specified email.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  User getUser(String email) throws IllegalArgumentException;

  /**
   * Retrieves moderator information.
   *
   * @return the moderator's User information.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  User getModeratorInfo() throws IllegalArgumentException;

  /**
   * Checks if a user is a moderator.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  boolean isModerator(String email) throws IllegalArgumentException;

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
  User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws IllegalArgumentException;

  /**
   * Deletes a user account.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws IllegalArgumentException if an illegal argument is provided.
   */
  void deleteAccount(String email, String password) throws IllegalArgumentException;
}
