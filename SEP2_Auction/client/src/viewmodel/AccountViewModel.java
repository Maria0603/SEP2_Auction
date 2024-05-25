package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.User;

public class AccountViewModel{
  private final StringProperty emailProperty, firstNameProperty, lastNameProperty, phoneProperty;

  public AccountViewModel(User user) {
    emailProperty = new SimpleStringProperty(user.getEmail());
    firstNameProperty = new SimpleStringProperty(user.getFirstname());
    lastNameProperty = new SimpleStringProperty(user.getLastname());
    phoneProperty=new SimpleStringProperty(user.getPhone());
  }

  public StringProperty getEmailProperty() {
    return emailProperty;
  }

  public StringProperty getFirstNameProperty() {
    return firstNameProperty;
  }

  public StringProperty getLastNameProperty() {
    return lastNameProperty;
  }
  public StringProperty getPhoneProperty()
  {
    return phoneProperty;
  }
}
