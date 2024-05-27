package persistence;

import model.domain.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class UserProtectionProxy extends DatabasePersistence implements UserPersistence
{
  private final UserDatabase database;
    public UserProtectionProxy() throws SQLException, ClassNotFoundException
    {
      this.database = new UserDatabase();
    }

    @Override public NotificationList getNotifications(String receiver)
        throws SQLException
    {
      return database.getNotifications(receiver);
    }


    @Override public User createUser(String firstname, String lastname,
        String email, String password, String repeatedPassword, String phone,
        LocalDate birthday) throws SQLException
    {
      checkFirstName(firstname);
      checkLastName(lastname);
      checkPhone(phone);
      ageValidation(birthday);
      return database.createUser(firstname, lastname, email, password,
          repeatedPassword, phone, birthday);
    }

    @Override public String login(String email, String password)
        throws SQLException
    {
      return database.login(email, password);
    }

    @Override public ArrayList<User> getAllUsers() throws SQLException
    {
      return database.getAllUsers();
    }

    @Override public void resetPassword(String userEmail, String oldPassword,
        String newPassword, String repeatPassword) throws SQLException
    {
      database.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
    }

    @Override public User getUserInfo(String email) throws SQLException
    {
      return database.getUserInfo(email);
    }

    @Override public User getModeratorInfo() throws SQLException
    {
      return database.getModeratorInfo();
    }

    @Override public boolean isModerator(String email) throws SQLException
    {
      return database.isModerator(email);
    }

    @Override public User editInformation(String oldEmail, String firstname,
        String lastname, String email, String password, String phone,
        LocalDate birthday) throws SQLException
    {
      checkFirstName(firstname);
      checkLastName(lastname);
      ageValidation(birthday);
      return database.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
    }

    @Override public void banParticipant(String moderatorEmail,
        String participantEmail, String reason) throws SQLException
    {
      if (isNotModerator(moderatorEmail))
        throw new SQLException("You cannot ban another participant.");
      checkReason(reason);
      database.banParticipant(moderatorEmail, participantEmail, reason);
    }

    @Override public String extractBanningReason(String email) throws SQLException
    {
      return database.extractBanningReason(email);
    }

    @Override public void unbanParticipant(String moderatorEmail,
        String participantEmail) throws SQLException
    {
      if (isNotModerator(moderatorEmail))
        throw new SQLException("You cannot unban another participant.");
      database.unbanParticipant(moderatorEmail, participantEmail);
    }

    @Override public void deleteAccount(String email, String password) throws SQLException
    {
      database.deleteAccount(email, password);
    }

    private boolean isNotModerator(String email)
    {
      return !email.equals(super.getModeratorEmail());
    }


    private void checkFirstName(String firstname) throws SQLException
    {
      if (firstname.isEmpty())
      {
        throw new SQLException("Empty first name.");
      }
      if (firstname.length() < 3)
      {
        throw new SQLException(
            "The first name must be at least 3 characters long.");
      }
    }

    private void checkLastName(String lastname) throws SQLException
    {
      if (lastname.isEmpty())
      {
        throw new SQLException("Empty last name.");
      }
      if (lastname.length() < 3)
      {
        throw new SQLException(
            "The last name must be at least 3 characters long.");
      }
    }


    private void checkPhone(String phone) throws SQLException
    {
      if (phone.length() < 4)
      {
        throw new SQLException("Invalid phone number.");
      }
      for(int i=0; i<phone.length(); i++)
      {
        if(!Character.isDigit(phone.charAt(i)))
          throw new SQLException("Invalid phone number.");
      }
    }

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
    private void checkReason(String reason) throws SQLException
    {
      if (reason == null || reason.length() < 3)
        throw new SQLException("The reason is too short.");
      if (reason.length() > 600)
        throw new SQLException("The reason is too long.");
    }

}
