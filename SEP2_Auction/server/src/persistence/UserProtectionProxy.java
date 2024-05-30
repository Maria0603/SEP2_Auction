package persistence;

import model.domain.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

/**
 * The UserProtectionProxy class implements the UserPersistence interface
 * and acts as a protection proxy for the UserDatabase, providing access control for user-related data.
 */
public class UserProtectionProxy extends DatabasePersistence implements UserPersistence
{
  private final UserDatabase database;

  /**
   * Constructs a UserProtectionProxy and initializes the underlying UserDatabase.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public UserProtectionProxy() throws SQLException, ClassNotFoundException
  {
    this.database = new UserDatabase();
  }

  /**
   * Retrieves the notifications for a specific user from the database.
   *
   * @param receiver the email of the receiver.
   * @return the NotificationList containing notifications for the receiver.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public NotificationList getNotifications(String receiver) throws SQLException
  {
    return database.getNotifications(receiver);
  }

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
  @Override
  public User createUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException
  {
    checkFirstName(firstname);
    checkLastName(lastname);
    checkPhone(phone);
    ageValidation(birthday);
    database.checkEmail(email);
    database.checkPassword(password, repeatedPassword);
    database.checkPhone(phone);
    return database.createUser(firstname, lastname, email, password, repeatedPassword, phone, birthday);
  }

  /**
   * Authenticates a user based on their email and password.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return the email of the authenticated user.
   * @throws SQLException if there is a database access error or the credentials are invalid.
   */
  @Override
  public String login(String email, String password) throws SQLException
  {
    if (!database.validateForLogin(email, password))
    {
      throw new SQLException("Credentials do not match");
    }
    if (database.isBanned(email))
      throw new SQLException("Account closed. Reason: " + extractBanningReason(email));
    return database.login(email, password);
  }

  /**
   * Retrieves a list of all users from the database, excluding the moderator.
   *
   * @return an ArrayList of User objects.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public ArrayList<User> getAllUsers() throws SQLException
  {
    return database.getAllUsers();
  }

  /**
   * Resets the password for a user.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password for confirmation.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws SQLException
  {
    database.validateNewPassword(userEmail, oldPassword, newPassword, repeatPassword);
    database.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
  }

  /**
   * Retrieves the information of a user by their email from the database.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public User getUserInfo(String email) throws SQLException
  {
    return database.getUserInfo(email);
  }

  /**
   * Retrieves the information of the moderator from the database.
   *
   * @return the User object representing the moderator.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public User getModeratorInfo() throws SQLException
  {
    return database.getModeratorInfo();
  }

  /**
   * Checks if a user is a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is a moderator, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public boolean isModerator(String email) throws SQLException
  {
    return database.isModerator(email);
  }

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
  @Override
  public User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws SQLException
  {
    checkFirstName(firstname);
    checkLastName(lastname);
    ageValidation(birthday);
    checkPhone(phone);

    if (database.validateForLogin(oldEmail, password))
    {
      if (!oldEmail.equals(email))
        database.checkEmail(email);
      database.checkPassword(password, password);
      String isPhoneTaken = database.isPhoneInTheSystem(phone);
      if (isPhoneTaken != null)
        if (!isPhoneTaken.equals(oldEmail))
          throw new SQLException("This phone number is taken.");
      return database.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
    }
    throw new SQLException("Wrong password");
  }

  /**
   * Bans a participant from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the ban.
   * @param participantEmail the email of the participant to be banned.
   * @param reason the reason for the ban.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void banParticipant(String moderatorEmail, String participantEmail, String reason) throws SQLException
  {
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot ban another participant.");
    if (database.isBanned(participantEmail))
      throw new SQLException("This participant is already banned.");
    checkReason(reason);
    database.banParticipant(moderatorEmail, participantEmail, reason);
  }

  /**
   * Extracts the banning reason for a user from the database.
   *
   * @param email the email of the user.
   * @return the banning reason for the user.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public String extractBanningReason(String email) throws SQLException
  {
    if (database.isBanned(email))
      return database.extractBanningReason(email);
    return null;
  }

  /**
   * Unbans a participant from the database.
   *
   * @param moderatorEmail the email of the moderator requesting the unban.
   * @param participantEmail the email of the participant to be unbanned.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void unbanParticipant(String moderatorEmail, String participantEmail) throws SQLException
  {
    if (!database.isBanned(participantEmail))
      throw new SQLException("This participant is not banned.");
    if (isNotModerator(moderatorEmail))
      throw new SQLException("You cannot unban another participant.");
    database.unbanParticipant(moderatorEmail, participantEmail);
  }

  /**
   * Deletes a user account from the database.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public void deleteAccount(String email, String password) throws SQLException
  {
    if (!database.validateForLogin(email, password))
    {
      throw new SQLException("Wrong password");
    }
    database.deleteAccount(email, password);
  }

  /**
   * Checks if a user is not a moderator based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is not a moderator, false otherwise.
   */
  private boolean isNotModerator(String email)
  {
    return !email.equals(super.getModeratorEmail());
  }

  /**
   * Checks the validity of the user's first name.
   *
   * @param firstname the first name of the user.
   * @throws SQLException if the first name is invalid.
   */
  private void checkFirstName(String firstname) throws SQLException
  {
    if (firstname.isEmpty())
    {
      throw new SQLException("Empty first name.");
    }
    if (firstname.length() < 3)
    {
      throw new SQLException("The first name must be at least 3 characters long.");
    }
    if (firstname.length() > 100)
      throw new SQLException("The first name is too long.");
  }

  /**
   * Checks the validity of the user's last name.
   *
   * @param lastname the last name of the user.
   * @throws SQLException if the last name is invalid.
   */
  private void checkLastName(String lastname) throws SQLException
  {
    if (lastname.isEmpty())
    {
      throw new SQLException("Empty last name.");
    }
    if (lastname.length() < 3)
    {
      throw new SQLException("The last name must be at least 3 characters long.");
    }
    if (lastname.length() > 100)
      throw new SQLException("The last name is too long.");
  }

  /**
   * Checks the validity of the user's phone number.
   *
   * @param phone the phone number of the user.
   * @throws SQLException if the phone number is invalid.
   */
  private void checkPhone(String phone) throws SQLException
  {
    if (phone.length() < 6)
    {
      throw new SQLException("Invalid phone number.");
    }
    if (phone.length() > 20)
      throw new SQLException("Invalid phone number");
    for (int i = 0; i < phone.length(); i++)
    {
      if (!Character.isDigit(phone.charAt(i)))
        throw new SQLException("Invalid phone number.");
    }
  }

  /**
   * Validates the user's age based on their birthday.
   *
   * @param birthday the birthday of the user.
   * @throws SQLException if the user is under 18 years old.
   */
  private void ageValidation(LocalDate birthday) throws SQLException
  {
    if (birthday != null)
    {
      LocalDate currentDate = LocalDate.now();
      Period period = Period.between(birthday, currentDate);
      int age = period.getYears();
      if (age < 18)
        throw new SQLException("You must be over 18 years old.");
    }
  }

  /**
   * Checks the validity of the reason for banning a participant.
   *
   * @param reason the reason for the ban.
   * @throws SQLException if the reason is too short or too long.
   */
  private void checkReason(String reason) throws SQLException
  {
    if (reason == null || reason.length() < 3)
      throw new SQLException("The reason is too short.");
    if (reason.length() > 600)
      throw new SQLException("The reason is too long.");
  }
}
