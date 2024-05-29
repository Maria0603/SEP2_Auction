package persistence;

import model.domain.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The UserPersistence interface provides methods for managing and persisting user-related data.
 */
public interface UserPersistence
{
  /**
   * Creates a new user in the database with the specified details.
   *
   * @param firstname the first name of the user.
   * @param lastname the last name of the user.
   * @param email the email of the user.
   * @param password the password of the user.
   * @param repeatedPassword the repeated password for confirmation.
   * @param phone the phone number of the user.
   * @param birthday the birthday of the user.
   * @return the created User object.
   * @throws SQLException if there is a database access error.
   */
  User createUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException;

  /**
   * Authenticates a user based on their email and password.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return the email of the authenticated user.
   * @throws SQLException if there is a database access error.
   */
  String login(String email, String password) throws SQLException;

  /**
   * Resets the password for a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password for confirmation.
   * @throws SQLException if there is a database access error.
   */
  void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws SQLException;

  /**
   * Retrieves the information of a user by their email from the database.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  User getUserInfo(String email) throws SQLException;

  /**
   * Retrieves the information of the moderator from the database.
   *
   * @return the User object representing the moderator.
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
   * Edits the information of a user in the database.
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
  User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws SQLException;

  /**
   * Deletes a user account from the database.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws SQLException if there is a database access error.
   */
  void deleteAccount(String email, String password) throws SQLException;

  /**
   * Retrieves the notifications for a specific user from the database.
   *
   * @param receiver the email of the receiver.
   * @return the NotificationList containing notifications for the receiver.
   * @throws SQLException if there is a database access error.
   */
  NotificationList getNotifications(String receiver) throws SQLException;

  /**
   * Retrieves a list of all users from the database, excluding the moderator.
   *
   * @return an ArrayList of User objects.
   * @throws SQLException if there is a database access error.
   */
  ArrayList<User> getAllUsers() throws SQLException;

  /**
   * Bans a participant from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the ban.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for the ban.
   * @throws SQLException if there is a database access error.
   */
  void banParticipant(String moderatorEmail, String participantEmail, String reason) throws SQLException;

  /**
   * Extracts the banning reason for a user from the database.
   *
   * @param email the email of the user.
   * @return the banning reason for the user.
   * @throws SQLException if there is a database access error.
   */
  String extractBanningReason(String email) throws SQLException;

  /**
   * Unbans a participant from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the unban.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws SQLException if there is a database access error.
   */
  void unbanParticipant(String moderatorEmail, String participantEmail) throws SQLException;
}
