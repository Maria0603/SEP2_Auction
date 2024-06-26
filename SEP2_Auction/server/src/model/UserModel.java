package model;

import model.domain.User;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * The UserModel interface provides methods for managing user accounts and information.
 * It extends the NamedPropertyChangeSubject to support property change notifications.
 */
public interface UserModel extends NamedPropertyChangeSubject
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
   * @throws SQLException if there is a database access error.
   */
  String addUser(String firstname, String lastname, String email,
                 String password, String repeatedPassword, String phone,
                 LocalDate birthday) throws SQLException;

  /**
   * Logs in a user with the specified email and password.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return a confirmation message or error message.
   * @throws SQLException if there is a database access error.
   */
  String login(String email, String password) throws SQLException;

  /**
   * Resets the password for a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password for verification.
   * @throws SQLException if there is a database access error.
   */
  void resetPassword(String userEmail, String oldPassword, String newPassword,
                     String repeatPassword) throws SQLException;

  /**
   * Retrieves a user by their email.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  User getUser(String email) throws SQLException;

  /**
   * Retrieves the information of the moderator.
   *
   * @return the User object containing moderator information.
   * @throws SQLException if there is a database access error.
   */
  User getModeratorInfo() throws SQLException;

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  boolean isModerator(String email) throws SQLException;

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
  User editInformation(String oldEmail, String firstname, String lastname,
                       String email, String password, String phone, LocalDate birthday)
          throws SQLException;

  /**
   * Deletes a user account.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws SQLException if there is a database access error.
   */
  void deleteAccount(String email, String password) throws SQLException;
}
