package model;

import mediator.AuctionClient;
import mediator.UserClient;
import model.domain.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserModelManager implements UserModel, PropertyChangeListener
{
  private final PropertyChangeSupport property;
  private final UserClient client;

  public UserModelManager() throws IOException, IllegalArgumentException
  {
    property = new PropertyChangeSupport(this);
    client = new UserClient();

    client.addListener("Notification", this);
    client.addListener("Ban", this);
    client.addListener("Reset", this);
    client.addListener("Edit", this);
  }

  @Override public String addUser(String firstname, String lastname,
      String email, String password, String repeatedPassword, String phone,
      LocalDate birthday) throws IllegalArgumentException
  {
    return client.addUser(firstname, lastname, email, password,
        repeatedPassword, phone, birthday);
  }

  @Override public String login(String email, String password)
      throws IllegalArgumentException
  {
    return client.login(email, password);
  }

  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws IllegalArgumentException
  {
    client.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
  }

  @Override public User getUser(String email) throws IllegalArgumentException
  {
    return client.getUser(email);
  }

  @Override public User getModeratorInfo() throws IllegalArgumentException
  {
    return client.getModeratorInfo();
  }

  @Override public boolean isModerator(String email) throws IllegalArgumentException
  {
    return client.isModerator(email);
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws IllegalArgumentException
  {
    return client.editInformation(oldEmail, firstname, lastname, email,
        password, phone, birthday);
  }

  @Override public void deleteAccount(String email, String password)
      throws IllegalArgumentException
  {
    client.deleteAccount(email, password);
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

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}
