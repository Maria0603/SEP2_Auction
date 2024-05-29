package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.domain.User;

/**
 * The AccountViewModel class is responsible for providing properties for binding
 * user account information to the view in a JavaFX application.
 */
public class AccountViewModel
{
  private final StringProperty emailProperty;
  private final StringProperty firstNameProperty;
  private final StringProperty lastNameProperty;
  private final StringProperty phoneProperty;

  /**
   * Constructs an AccountViewModel with the specified user.
   *
   * @param user the user whose account information is to be represented
   */
  public AccountViewModel(User user)
  {
    emailProperty = new SimpleStringProperty(user.getEmail());
    firstNameProperty = new SimpleStringProperty(user.getFirstname());
    lastNameProperty = new SimpleStringProperty(user.getLastname());
    phoneProperty = new SimpleStringProperty(user.getPhone());
  }

  /**
   * Gets the email property.
   *
   * @return the email property
   */
  public StringProperty getEmailProperty()
  {
    return emailProperty;
  }

  /**
   * Gets the first name property.
   *
   * @return the first name property
   */
  public StringProperty getFirstNameProperty()
  {
    return firstNameProperty;
  }

  /**
   * Gets the last name property.
   *
   * @return the last name property
   */
  public StringProperty getLastNameProperty()
  {
    return lastNameProperty;
  }

  /**
   * Gets the phone property.
   *
   * @return the phone property
   */
  public StringProperty getPhoneProperty()
  {
    return phoneProperty;
  }
}
