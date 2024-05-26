package model;

import model.domain.*;
import persistence.ProtectionProxy;
import persistence.UserPersistence;
import persistence.UserProtectionProxy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserModelManager implements UserModel
{
  private PropertyChangeSupport property;
  private UserPersistence userDatabase;

  public UserModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    userDatabase = new UserProtectionProxy();
  }


  @Override public synchronized String addUser(String firstname,
      String lastname, String email, String password, String repeatedPassword,
      String phone, LocalDate birthday) throws SQLException
  {
    return userDatabase.createUser(firstname, lastname, email, password,
        repeatedPassword, phone, birthday).getEmail();
  }

  @Override public synchronized String login(String email, String password)
      throws SQLException
  {
    return userDatabase.login(email, password);
  }

  @Override public synchronized void resetPassword(String userEmail,
      String oldPassword, String newPassword, String repeatPassword)
      throws SQLException
  {
    userDatabase.resetPassword(userEmail, oldPassword, newPassword,
        repeatPassword);
    property.firePropertyChange("Reset", null, userEmail);
  }

  @Override public synchronized User getUser(String email) throws SQLException
  {
    return userDatabase.getUserInfo(email);
  }

  @Override public synchronized User getModeratorInfo() throws SQLException
  {
    return userDatabase.getModeratorInfo();
  }

  @Override public synchronized boolean isModerator(String email)
      throws SQLException
  {
    return userDatabase.isModerator(email);
  }

  @Override public synchronized User editInformation(String oldEmail,
      String firstname, String lastname, String email, String password,
      String phone, LocalDate birthday) throws SQLException
  {
    User user = userDatabase.editInformation(oldEmail, firstname, lastname,
        email, password, phone, birthday);
    if (!oldEmail.equals(email))
    {
      property.firePropertyChange("Edit", oldEmail, email);
    }
    return user;
  }

  @Override public void deleteAccount(String email, String password)
      throws SQLException
  {
    userDatabase.deleteAccount(email, password);
    property.firePropertyChange("DeleteAccount", null, null);
  }


  @Override public synchronized void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

}
