package persistence;

import model.domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The UserDatabase class implements the UserPersistence interface
 * and provides methods for managing user-related data in the database.
 */
public class UserDatabase extends DatabasePersistence implements UserPersistence
{
  /**
   * Constructs a UserDatabase and initializes the database connection.
   *
   * @throws SQLException if there is a database access error.
   * @throws ClassNotFoundException if the class of a serialized object cannot be found.
   */
  public UserDatabase() throws SQLException, ClassNotFoundException
  {
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
  public synchronized User createUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday) throws SQLException
  {
    String sqlUser = "INSERT INTO \"user\"(user_email, password, phone_number, first_name, last_name)  \n" + "VALUES(?,?,?,?,?);";
    String sqlParticipant = "INSERT INTO participant(user_email, birth_date) VALUES (?, ?);\n";
    super.getDatabase().update(sqlUser, email, password, phone, firstname, lastname);
    Date date = Date.valueOf(birthday);
    super.getDatabase().update(sqlParticipant, email, date);
    return new User(firstname, lastname, email, password, phone, birthday);
  }

  /**
   * Authenticates a user based on their email and password.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return the email of the authenticated user.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized String login(String email, String password) throws SQLException
  {
    return email;
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
  public synchronized void resetPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws SQLException
  {
    String sql = "UPDATE \"user\" SET password=?\n" + "WHERE user_email=?;";
    super.getDatabase().update(sql, newPassword, userEmail);
  }

  /**
   * Retrieves the information of a user by their email from the database.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized User getUserInfo(String email) throws SQLException
  {
    User user = getUser(email);
    if (user != null)
    {
      if (email.equals(super.getModeratorEmail()))
      {
        return new User(user.getFirstname(), user.getLastname(), getModeratorSpecificInfo(), null, user.getPhone(), null);
      }
      else
      {
        Date birthday = getParticipantInfo(email);
        return new User(user.getFirstname(), user.getLastname(), email, null, user.getPhone(), birthday.toLocalDate());
      }
    }
    return null;
  }

  /**
   * Retrieves the information of the moderator from the database.
   *
   * @return the User object representing the moderator.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized User getModeratorInfo() throws SQLException
  {
    return getUserInfo(super.getModeratorEmail());
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
    return isEmailIn(email, "moderator_email", "moderator");
  }

  /**
   * Deletes a user account from the database.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized void deleteAccount(String email, String password) throws SQLException
  {
    deleteAuctionsStartedBy(email);
    updateCurrentBidAndCurrentBidderAfterBan(email);
    deleteBids(email);

    String sql1 = "DELETE FROM participant WHERE user_email=?;";
    String sql2 = "DELETE FROM \"user\" WHERE user_email=?;";
    super.getDatabase().update(sql1, email);
    super.getDatabase().update(sql2, email);
  }

  /**
   * Extracts the banning reason for a user from the database.
   *
   * @param email the email of the user.
   * @return the banning reason for the user.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized String extractBanningReason(String email) throws SQLException
  {
    String sql = "SELECT reason FROM banned_participant WHERE user_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (Object[] row : results)
    {
      if (row[0] != null)
        return row[0].toString();
    }
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
  public synchronized void unbanParticipant(String moderatorEmail, String participantEmail) throws SQLException
  {
    String sql = "DELETE FROM banned_participant WHERE user_email=?;\n";
    super.getDatabase().update(sql, participantEmail);
    throw new SQLException("Account linked to email " + participantEmail + " successfully unbanned.");
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
  public synchronized User editInformation(String oldEmail, String firstname, String lastname, String email, String password, String phone, LocalDate birthday) throws SQLException
  {
    String sqlUser = "UPDATE \"user\" SET user_email=?, phone_number=?, first_name=?, last_name=?\n" + "WHERE user_email=?;";
    String sqlParticipantOrModerator;

    if (oldEmail.equals(super.getModeratorEmail()))
    {
      super.getDatabase().update(sqlUser, oldEmail, phone, firstname, lastname, oldEmail);

      sqlParticipantOrModerator = "UPDATE moderator SET personal_email=?\n" + "WHERE moderator_email=?;";
      super.getDatabase().update(sqlParticipantOrModerator, email, oldEmail);
    }
    else
    {
      super.getDatabase().update(sqlUser, email, phone, firstname, lastname, oldEmail);
      sqlParticipantOrModerator = "UPDATE participant SET birth_date=?\n" + "WHERE user_email=?;";
      Date date = Date.valueOf(birthday);
      super.getDatabase().update(sqlParticipantOrModerator, date, email);
    }
    return new User(firstname, lastname, email, password, phone, birthday);
  }

  /**
   * Retrieves the notifications for a specific user from the database.
   *
   * @param receiver the email of the receiver.
   * @return the NotificationList containing notifications for the receiver.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized NotificationList getNotifications(String receiver) throws SQLException
  {
    String sql = "SELECT * FROM notification WHERE receiver=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, receiver);
    NotificationList notifications = new NotificationList();
    for (Object[] row : results)
    {
      String content = row[2].toString();
      String dateTime = row[3].toString() + " " + row[4].toString();
      notifications.addNotification(new Notification(dateTime, content, receiver));
    }
    return notifications;
  }

  /**
   * Retrieves a list of all users from the database, excluding the moderator.
   *
   * @return an ArrayList of User objects.
   * @throws SQLException if there is a database access error.
   */
  @Override
  public synchronized ArrayList<User> getAllUsers() throws SQLException
  {
    String sql = "SELECT user_email, phone_number, first_name, last_name FROM \"user\" WHERE user_email!=?;\n";
    ArrayList<Object[]> queryResults = super.getDatabase().query(sql, super.getModeratorEmail());

    ArrayList<User> output = new ArrayList<>();
    String phone, firstName, lastName, email;

    for (Object[] userData : queryResults)
    {
      email = userData[0].toString();
      phone = userData[1].toString();
      firstName = userData[2].toString();
      lastName = userData[3].toString();

      output.add(new User(firstName, lastName, email, null, phone, null));
    }
    return output;
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
  public synchronized void banParticipant(String moderatorEmail, String participantEmail, String reason) throws SQLException
  {
    String sql = "INSERT INTO banned_participant(user_email, reason) VALUES (?, ?);\n";
    super.getDatabase().update(sql, participantEmail, reason);
    deleteAuctionsStartedBy(participantEmail);
    updateCurrentBidAndCurrentBidderAfterBan(participantEmail);
    deleteBids(participantEmail);
    throw new SQLException("Account linked to email " + participantEmail + " successfully banned.");
  }

  /**
   * Retrieves the birth date of a participant by their email from the database.
   *
   * @param email the email of the participant.
   * @return the birth date of the participant.
   * @throws SQLException if there is a database access error.
   */
  private synchronized Date getParticipantInfo(String email) throws SQLException
  {
    String sql = "SELECT participant.birth_date FROM participant WHERE user_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (Object[] row : results)
    {
      return Date.valueOf(row[0].toString());
    }
    return null;
  }

  /**
   * Retrieves specific information for a moderator from the database.
   *
   * @return the personal email of the moderator.
   * @throws SQLException if there is a database access error.
   */
  private synchronized String getModeratorSpecificInfo() throws SQLException
  {
    String sql = "SELECT personal_email FROM moderator WHERE moderator_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, super.getModeratorEmail());
    for (Object[] row : results)
    {
      if (row[0] != null)
        return row[0].toString();
    }
    return null;
  }

  /**
   * Retrieves a user by their email from the database.
   *
   * @param email the email of the user.
   * @return the User object corresponding to the specified email.
   * @throws SQLException if there is a database access error.
   */
  private synchronized User getUser(String email) throws SQLException
  {
    String sql = "SELECT phone_number, first_name, last_name FROM \"user\" WHERE user_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (Object[] row : results)
    {
      String phone = null;
      if (row[0] != null)
        phone = row[0].toString();
      String firstName = null;
      if (row[1] != null)
        firstName = row[1].toString();
      String lastName = null;
      if (row[2] != null)
        lastName = row[2].toString();
      return new User(firstName, lastName, email, null, phone, null);
    }
    return null;
  }

  /**
   * Validates the new password for a user during the password reset process.
   *
   * @param userEmail the email of the user.
   * @param oldPassword the old password of the user.
   * @param newPassword the new password of the user.
   * @param repeatPassword the repeated new password for confirmation.
   * @throws SQLException if there is a database access error or the new password is invalid.
   */
  public synchronized void validateNewPassword(String userEmail, String oldPassword, String newPassword, String repeatPassword) throws SQLException
  {
    checkPassword(newPassword, repeatPassword);
    String sql = "SELECT password FROM \"user\"\n" + "WHERE user_email=?;";
    ArrayList<Object[]> result = super.getDatabase().query(sql, userEmail);
    for (Object[] row : result)
    {
      String retrievedOldPassword = row[0].toString();
      if (!retrievedOldPassword.equals(oldPassword))
        throw new SQLException("The old password is incorrect.");
      if (oldPassword.equals(newPassword))
        throw new SQLException("The new password and the old one are the same.");
    }
  }

  /**
   * Checks if an email exists in a specified table and field.
   *
   * @param email the email to check.
   * @param field the field to check.
   * @param table the table to check.
   * @return true if the email exists, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  private synchronized boolean isEmailIn(String email, String field, String table) throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM " + table + " WHERE " + field + " =?";
    ArrayList<Object[]> result = super.getDatabase().query(sql, email);
    for (Object[] row : result)
    {
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  /**
   * Checks if a phone number exists in the system.
   *
   * @param phone the phone number to check.
   * @return the email associated with the phone number if it exists, null otherwise.
   * @throws SQLException if there is a database access error.
   */
  public synchronized String isPhoneInTheSystem(String phone) throws SQLException
  {
    int count = 0;
    String sql = "SELECT user_email, count(*) FROM \"user\"\n" + "WHERE phone_number=?\n" + "GROUP BY user_email;";
    ArrayList<Object[]> result = super.getDatabase().query(sql, phone);
    for (Object[] row : result)
    {
      count = Integer.parseInt(row[1].toString());
      if (count > 0)
        return row[0].toString();
    }
    return null;
  }

  /**
   * Validates the credentials for a user login.
   *
   * @param email the email of the user.
   * @param password the password of the user.
   * @return true if the credentials are valid, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  public synchronized boolean validateForLogin(String email, String password) throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM \"user\" WHERE user_email=? AND password=?;";
    ArrayList<Object[]> result = super.getDatabase().query(sql, email, password);
    for (Object[] row : result)
    {
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  /**
   * Checks the validity of an email address.
   *
   * @param email the email to check.
   * @throws SQLException if the email is invalid or already exists in the system.
   */
  public synchronized void checkEmail(String email) throws SQLException
  {
    if (email.isEmpty())
    {
      throw new SQLException("Empty email.");
    }
    if (!email.contains("@"))
    {
      throw new SQLException("Email must be in 'name@domain' format.");
    }
    if (email.length() > 200)
      throw new SQLException("The email is too long.");
    if (isEmailIn(email, "user_email", "\"user\""))
    {
      throw new SQLException("Email is already in the system.");
    }
  }

  /**
   * Checks the validity of a password.
   *
   * @param password the password to check.
   * @param repeatedPassword the repeated password for confirmation.
   * @throws SQLException if the password is invalid.
   */
  public synchronized void checkPassword(String password, String repeatedPassword) throws SQLException
  {
    if (password.isEmpty())
    {
      throw new SQLException("Empty password.");
    }
    if (password.length() < 8)
    {
      throw new SQLException("The password must be at least 3 characters long.");
    }
    if (password.length() > 255)
      throw new SQLException("The password is too long.");
    if (!password.equals(repeatedPassword))
      throw new SQLException("The passwords don't match.");
  }

  /**
   * Checks the validity of a phone number.
   *
   * @param phone the phone number to check.
   * @throws SQLException if the phone number is already in the system.
   */
  public synchronized void checkPhone(String phone) throws SQLException
  {
    if (isPhoneInTheSystem(phone) != null)
    {
      throw new SQLException("This phone number is already in the system.");
    }
  }

  /**
   * Deletes auctions started by a specific user.
   *
   * @param email the email of the user.
   * @throws SQLException if there is a database access error.
   */
  private void deleteAuctionsStartedBy(String email) throws SQLException
  {
    String sql = "DELETE FROM auction WHERE creator_email=?;";
    super.getDatabase().update(sql, email);
  }

  /**
   * Updates the current bid and bidder for auctions after a user is banned.
   *
   * @param email the email of the banned user.
   * @throws SQLException if there is a database access error.
   */
  private void updateCurrentBidAndCurrentBidderAfterBan(String email) throws SQLException
  {
    String sql1 = "WITH to_be_updated_auctions AS (\n"
            + "    SELECT a.id AS auction_id, MAX(b.bid_amount) AS new_bid_amount, b.participant_email AS new_bidder\n"
            + "    FROM auction a\n"
            + "    LEFT JOIN bid b ON a.id = b.auction_id AND b.participant_email != ?\n"
            + "    WHERE a.current_bidder = ?\n"
            + "    GROUP BY a.id, b.participant_email\n"
            + "    ORDER BY a.id, new_bid_amount DESC\n" + "),\n"
            + "ordered_auctions AS (\n"
            + "    SELECT auction_id, new_bid_amount, new_bidder,\n"
            + "           ROW_NUMBER() OVER (PARTITION BY auction_id ORDER BY new_bid_amount DESC) AS row\n"
            + "    FROM to_be_updated_auctions\n" + ")\n" + "UPDATE auction\n"
            + "SET current_bid = CASE\n"
            + "                      WHEN ordered_auctions.new_bid_amount IS NOT NULL THEN ordered_auctions.new_bid_amount\n"
            + "                      ELSE 0\n" + "    END,\n"
            + "    current_bidder = ordered_auctions.new_bidder\n"
            + "FROM ordered_auctions\n"
            + "WHERE auction.id = ordered_auctions.auction_id\n"
            + "AND (ordered_auctions.row = 1 OR ordered_auctions.new_bid_amount IS NULL);";
    super.getDatabase().update(sql1, email, email);
  }

  /**
   * Deletes bids placed by a specific user.
   *
   * @param email the email of the user.
   * @throws SQLException if there is a database access error.
   */
  private void deleteBids(String email) throws SQLException
  {
    String sql2 = "DELETE FROM bid WHERE participant_email=?;";
    super.getDatabase().update(sql2, email);
  }

  /**
   * Checks if a user is banned based on their email.
   *
   * @param email the email of the user.
   * @return true if the user is banned, false otherwise.
   * @throws SQLException if there is a database access error.
   */
  public boolean isBanned(String email) throws SQLException
  {
    String sql = "SELECT COUNT(*) FROM banned_participant WHERE user_email=?;\n";
    int count = 0;
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (Object[] row : results)
    {
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }
}
