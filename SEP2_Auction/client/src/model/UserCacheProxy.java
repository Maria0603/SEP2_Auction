package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserCacheProxy extends CacheProxy
    implements UserModel, PropertyChangeListener
{
  private final UserModelManager modelManager;
  private final PropertyChangeSupport property;

  public UserCacheProxy() throws IllegalArgumentException, IOException
  {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new UserModelManager();

    modelManager.addListener("Edit", this);
    modelManager.addListener("Reset", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("Notification", this);

  }

  @Override public String addUser(String firstname, String lastname,
      String email, String password, String repeatedPassword, String phone,
      LocalDate birthday) throws IllegalArgumentException
  {
    String userEmail = modelManager.addUser(firstname, lastname, email,
        password, repeatedPassword, phone, birthday);

    super.setUserEmail(userEmail);
    return userEmail;
  }

  @Override public String login(String email, String password)
      throws IllegalArgumentException
  {
    String userEmail = modelManager.login(email, password);
    super.setUserEmail(userEmail);
    return userEmail;
  }

  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws IllegalArgumentException
  {
    modelManager.resetPassword(userEmail, oldPassword, newPassword,
        repeatPassword);
  }

  @Override public User getUser(String email) throws IllegalArgumentException
  {
    return modelManager.getUser(email);
  }

  @Override public User getModeratorInfo() throws IllegalArgumentException
  {
    return modelManager.getModeratorInfo();
  }

  @Override public boolean isModerator(String email) throws IllegalArgumentException
  {
    return modelManager.isModerator(email);
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws IllegalArgumentException
  {
    return modelManager.editInformation(oldEmail, firstname, lastname, email,
        password, phone, birthday);
  }

  @Override public void deleteAccount(String email, String password)
      throws IllegalArgumentException
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
    switch (evt.getPropertyName())
    {
      case "Edit" -> receivedEdit(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
        evt.getNewValue());
  }
}
