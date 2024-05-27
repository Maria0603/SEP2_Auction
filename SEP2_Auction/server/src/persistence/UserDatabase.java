package persistence;

import model.domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class UserDatabase extends DatabasePersistence implements UserPersistence
{

  public UserDatabase() throws SQLException, ClassNotFoundException
  {
  }

  @Override public synchronized User createUser(String firstname,
      String lastname, String email, String password, String repeatedPassword,
      String phone, LocalDate birthday) throws SQLException
  {
    String sqlUser =
        "INSERT INTO sprint1database.users(user_email, password, phone_number, first_name, last_name)  \n"
            + "VALUES(?,?,?,?,?);";
    String sqlParticipant = "INSERT INTO participant(user_email, birth_date) VALUES (?, ?);\n";

    checkEmail(email);
    checkPassword(password, repeatedPassword);
    checkPhone(phone);
    super.getDatabase()
        .update(sqlUser, email, password, phone, firstname, lastname);
    Date date = Date.valueOf(birthday);
    super.getDatabase().update(sqlParticipant, email, date);
    return new User(firstname, lastname, email, password, phone, birthday);
  }

  @Override public synchronized String login(String email, String password)
      throws SQLException
  {
    if (!validateForLogin(email, password))
    {
      throw new SQLException("Credentials do not match");
    }
    if (isBanned(email))
      throw new SQLException(
          "Account closed. Reason: " + extractBanningReason(email));
    return email;
  }

  @Override public synchronized void resetPassword(String userEmail,
      String oldPassword, String newPassword, String repeatPassword)
      throws SQLException
  {
    validateNewPassword(userEmail, oldPassword, newPassword, repeatPassword);
    String sql = "UPDATE users SET password=?\n" + "WHERE users.user_email=?;";
    super.getDatabase().update(sql, newPassword, userEmail);
  }

  @Override public synchronized User getUserInfo(String email)
      throws SQLException
  {
    User user = getUser(email);
    if (user != null)
    {
      if (email.equals(super.getModeratorEmail()))
      {
        return new User(user.getFirstname(), user.getLastname(),
            getModeratorSpecificInfo(), null, user.getPhone(), null);
      }
      else
      {
        Date birthday = getParticipantInfo(email);
        return new User(user.getFirstname(), user.getLastname(), email, null,
            user.getPhone(), birthday.toLocalDate());
      }
    }
    return null;
  }

  @Override public synchronized User getModeratorInfo() throws SQLException
  {
    return getUserInfo(super.getModeratorEmail());
  }

  @Override public synchronized boolean isModerator(String email)
      throws SQLException
  {
    return isEmailIn(email, "moderator_email", "moderator");
  }

  @Override public synchronized void deleteAccount(String email,
      String password) throws SQLException
  {
    if (!validateForLogin(email, password))
    {
      throw new SQLException("Wrong password");
    }
    deleteAuctionsStartedBy(email);
    updateCurrentBidAndCurrentBidderAfterBan(email);
    deleteBids(email);

    String sql1 = "DELETE FROM participant WHERE user_email=?;";
    String sql2 = "DELETE FROM users WHERE user_email=?;";
    super.getDatabase().update(sql1, email);
    super.getDatabase().update(sql2, email);
  }

  @Override public synchronized String extractBanningReason(String email)
      throws SQLException
  {
    if (isBanned(email))
    {
      String sql = "SELECT reason FROM banned_participant WHERE user_email=?;\n";
      ArrayList<Object[]> results = super.getDatabase().query(sql, email);
      for (int i = 0; i < results.size(); i++)
      {
        Object[] row = results.get(i);
        if (row[0] != null)
          return row[0].toString();
      }
    }
    return null;
  }

  @Override public synchronized void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    if (!isBanned(participantEmail))
      throw new SQLException("This participant is not banned.");
    String sql = "DELETE FROM banned_participant WHERE user_email=?;\n";
    super.getDatabase().update(sql, participantEmail);
    throw new SQLException("Account linked to email " + participantEmail
        + " successfully unbanned.");
  }

  @Override public synchronized User editInformation(String oldEmail,
      String firstname, String lastname, String email, String password,
      String phone, LocalDate birthday) throws SQLException
  {
    if (validateForLogin(oldEmail, password))
    {
      if (!oldEmail.equals(email))
        checkEmail(email);
      checkPassword(password, password);
      String isPhoneTaken = isPhoneInTheSystem(phone);
      if (isPhoneTaken != null)
        if (!isPhoneTaken.equals(oldEmail))
          throw new SQLException("This phone number is taken.");
      String sqlUser =
          "UPDATE users SET user_email=?, phone_number=?, first_name=?, last_name=?\n"
              + "WHERE user_email=?;";
      String sqlParticipantOrModerator;

      if (oldEmail.equals(super.getModeratorEmail()))
      {
        super.getDatabase()
            .update(sqlUser, oldEmail, phone, firstname, lastname, oldEmail);

        sqlParticipantOrModerator = "UPDATE moderator SET personal_email=?\n"
            + "WHERE moderator_email=?;";
        super.getDatabase().update(sqlParticipantOrModerator, email, oldEmail);
      }
      else
      {
        super.getDatabase()
            .update(sqlUser, email, phone, firstname, lastname, oldEmail);
        sqlParticipantOrModerator =
            "UPDATE participant SET birth_date=?\n" + "WHERE user_email=?;";
        Date date = Date.valueOf(birthday);
        super.getDatabase().update(sqlParticipantOrModerator, date, email);
      }
      return new User(firstname, lastname, email, password, phone, birthday);
    }
    throw new SQLException("Wrong password");
  }

  @Override public synchronized NotificationList getNotifications(
      String receiver) throws SQLException
  {
    String sql = "SELECT * FROM notification WHERE receiver=?;";
    ArrayList<Object[]> results = super.getDatabase().query(sql, receiver);
    NotificationList notifications = new NotificationList();
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      String content = row[2].toString();
      String dateTime = row[3].toString() + " " + row[4].toString();
      notifications.addNotification(
          new Notification(dateTime, content, receiver));
    }
    return notifications;
  }

  @Override public synchronized ArrayList<User> getAllUsers()
      throws SQLException
  {
    String sql = "SELECT user_email, phone_number, first_name, last_name FROM users WHERE user_email!=?;\n";
    ArrayList<Object[]> queryResults = super.getDatabase()
        .query(sql, super.getModeratorEmail());

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

  @Override public synchronized void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    if (isBanned(participantEmail))
      throw new SQLException("This participant is already banned.");

    String sql = "INSERT INTO banned_participant(user_email, reason) VALUES (?, ?);\n";
    super.getDatabase().update(sql, participantEmail, reason);
    deleteAuctionsStartedBy(participantEmail);
    updateCurrentBidAndCurrentBidderAfterBan(participantEmail);
    deleteBids(participantEmail);
    throw new SQLException("Account linked to email " + participantEmail
        + " successfully banned.");
  }

  private synchronized Date getParticipantInfo(String email) throws SQLException
  {
    String sql = "SELECT participant.birth_date FROM participant WHERE user_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      return Date.valueOf(row[0].toString());
    }
    return null;
  }

  private synchronized String getModeratorSpecificInfo() throws SQLException
  {
    String sql = "SELECT personal_email FROM moderator WHERE moderator_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase()
        .query(sql, super.getModeratorEmail());
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      if (row[0] != null)
        return row[0].toString();
    }
    return null;
  }

  private synchronized User getUser(String email) throws SQLException
  {
    String sql = "SELECT phone_number, first_name, last_name FROM users WHERE user_email=?;\n";
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
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

  private synchronized void validateNewPassword(String userEmail,
      String oldPassword, String newPassword, String repeatPassword)
      throws SQLException
  {
    checkPassword(newPassword, repeatPassword);
    String sql = "SELECT users.password FROM users\n" + "WHERE user_email=?;";
    ArrayList<Object[]> result = super.getDatabase().query(sql, userEmail);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      String retrievedOldPassword = row[0].toString();
      if (!retrievedOldPassword.equals(oldPassword))
        throw new SQLException("The old password is incorrect.");
      if (oldPassword.equals(newPassword))
        throw new SQLException(
            "The new password and the old one are the same.");
    }
  }

  private synchronized boolean isEmailIn(String email, String field,
      String table) throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM " + table + " WHERE " + field + " =?";
    ArrayList<Object[]> result = super.getDatabase().query(sql, email);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  private synchronized String isPhoneInTheSystem(String phone)
      throws SQLException
  {
    int count = 0;
    String sql =
        "SELECT user_email, count(*) FROM users\n" + "WHERE phone_number=?\n"
            + "GROUP BY user_email;";
    ArrayList<Object[]> result = super.getDatabase().query(sql, phone);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      count = Integer.parseInt(row[1].toString());
      if (count > 0)
        return row[0].toString();
    }
    return null;
  }

  private synchronized boolean validateForLogin(String email, String password)
      throws SQLException
  {
    int count = 0;
    String sql = "SELECT count(*) FROM users WHERE user_email=? AND password=?;";
    ArrayList<Object[]> result = super.getDatabase()
        .query(sql, email, password);
    for (int i = 0; i < result.size(); i++)
    {
      Object[] row = result.get(i);
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

  private synchronized void checkEmail(String email) throws SQLException
  {
    if (email.isEmpty())
    {
      throw new SQLException("Empty email.");
    }
    if (!email.contains("@"))
    {
      throw new SQLException("Email must be in 'name@domain' format.");
    }
    if (isEmailIn(email, "user_email", "users"))
    {
      throw new SQLException("Email is already in the system.");
    }
  }

  private synchronized void checkPassword(String password,
      String repeatedPassword) throws SQLException
  {
    if (password.isEmpty())
    {
      throw new SQLException("Empty password.");
    }
    if (password.length() < 4)
    {
      throw new SQLException(
          "The password must be at least 3 characters long.");
    }
    if (!password.equals(repeatedPassword))
      throw new SQLException("The passwords don't match.");
  }

  private synchronized void checkPhone(String phone) throws SQLException
  {
    if (isPhoneInTheSystem(phone) != null)
    {
      throw new SQLException("This phone number is already in the system.");
    }
  }

  private void deleteAuctionsStartedBy(String email) throws SQLException
  {
    String sql = "DELETE FROM auction WHERE creator_email=?;";
    super.getDatabase().update(sql, email);
  }

  private void updateCurrentBidAndCurrentBidderAfterBan(String email)
      throws SQLException
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

  private void deleteBids(String email) throws SQLException
  {
    String sql2 = "DELETE FROM bid WHERE participant_email=?;";
    super.getDatabase().update(sql2, email);
  }

  private boolean isBanned(String email) throws SQLException
  {
    String sql = "SELECT COUNT(*) FROM banned_participant WHERE user_email=?;\n";
    int count = 0;
    ArrayList<Object[]> results = super.getDatabase().query(sql, email);
    for (int i = 0; i < results.size(); i++)
    {
      Object[] row = results.get(i);
      count = Integer.parseInt(row[0].toString());
    }
    return count > 0;
  }

}
