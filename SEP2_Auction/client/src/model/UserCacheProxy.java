package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class UserCacheProxy extends Cache implements UserModel,
    PropertyChangeListener
{
  private UserModelManager modelManager;
  private PropertyChangeSupport property;

  public UserCacheProxy() throws SQLException, IOException
  {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new UserModelManager();

    modelManager.addListener("Edit", this);
    modelManager.addListener("Reset", this);
    modelManager.addListener("Ban", this);
  }


  @Override public String addUser(String firstname, String lastname,
      String email, String password, String repeatedPassword, String phone,
      LocalDate birthday) throws SQLException
  {
    String userEmail = modelManager.addUser(firstname, lastname, email, password,
        repeatedPassword, phone, birthday);

    super.setUserEmail(userEmail);
    return userEmail;
  }


  @Override public String login(String email, String password)
      throws SQLException
  {
    String userEmail = modelManager.login(email, password);
    super.setUserEmail(userEmail);
    return userEmail;
  }


  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    modelManager.resetPassword(userEmail, oldPassword, newPassword,
        repeatPassword);
  }

  @Override public User getUser(String email) throws SQLException
  {
    return modelManager.getUser(email);
  }

  @Override public User getModeratorInfo() throws SQLException
  {
    return modelManager.getModeratorInfo();
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    return modelManager.isModerator(email);
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
    return modelManager.editInformation(oldEmail, firstname, lastname, email,
        password, phone, birthday);
  }


  @Override public void deleteAccount(String email, String password)
      throws SQLException
  {
    modelManager.deleteAccount(email, password);
  }

  @Override public void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

  private void receivedEdit(PropertyChangeEvent evt)
  {
    if (super.getUserEmail().equals(evt.getOldValue().toString()))
    {
      super.setUserEmail(evt.getNewValue().toString());
    }
  }


  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    System.out.println("received "+evt.getPropertyName() + " in user cache");
    switch (evt.getPropertyName())
    {
      case "Edit" -> receivedEdit(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
        evt.getNewValue());
  }
}
