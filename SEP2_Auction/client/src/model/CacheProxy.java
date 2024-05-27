package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class CacheProxy
{
  protected static StringProperty userEmail = new SimpleStringProperty();

  public CacheProxy()
  {

  }

  public void setUserEmail(String userEmail)
  {
    CacheProxy.userEmail.set(userEmail);
  }

  public StringProperty getUserEmail()
  {
    return userEmail;
  }
}
