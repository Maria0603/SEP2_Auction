package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The CacheProxy class serves as an abstract base class for caching proxies,
 * providing common functionality for managing user email information.
 */
public abstract class CacheProxy {

  protected static StringProperty userEmail = new SimpleStringProperty();

  /**
   * Constructs a new CacheProxy object.
   */
  public CacheProxy() {
    // No specific initialization required
  }

  /**
   * Sets the user email.
   *
   * @param userEmail the user email to set.
   */
  public void setUserEmail(String userEmail) {
    CacheProxy.userEmail.set(userEmail);
  }

  /**
   * Gets the user email property.
   *
   * @return the user email property.
   */
  public StringProperty getUserEmail() {
    return userEmail;
  }
}
