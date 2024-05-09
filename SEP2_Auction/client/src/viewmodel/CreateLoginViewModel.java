package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.AuctionModel;
import model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CreateLoginViewModel
{
  private StringProperty headerProperty, firstnameProperty, lastnameProperty, emailProperty, passwordProperty, repasswordProperty, phoneProperty, errorProperty;
  private AuctionModel model;
  private ViewModelState viewState;
  private LocalDate birthDate;

  public CreateLoginViewModel(AuctionModel model, ViewModelState viewState)
  {
    this.viewState = viewState;
    this.model = model;
    headerProperty = new SimpleStringProperty();
    firstnameProperty = new SimpleStringProperty();
    lastnameProperty = new SimpleStringProperty();
    emailProperty = new SimpleStringProperty();
    passwordProperty = new SimpleStringProperty();
    repasswordProperty = new SimpleStringProperty();
    phoneProperty = new SimpleStringProperty();
    errorProperty = new SimpleStringProperty();
    birthDate=null;

    reset();
  }

  public void reset()
  {
    firstnameProperty.set("");
    lastnameProperty.set("");
    emailProperty.set("");
    passwordProperty.set("");
    repasswordProperty.set("");
    phoneProperty.set("");
    errorProperty.set("");
    birthDate=null;
  }

  public boolean createUser()
  {
    try
    {
      String email=model.addUser(firstnameProperty.get().trim(),
          lastnameProperty.get().trim(), emailProperty.get().trim(),
          passwordProperty.get(), repasswordProperty.get(), phoneProperty.get().trim(), birthDate);
      viewState.setUserEmail(email);
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean login()
  {
    errorProperty.set("");
    try
    {
      String user = model.login(emailProperty.get().trim(),
          passwordProperty.get());
      //  Giving the viewState all the user info from the model=>takes from servers database
      viewState.setUserEmail(user);
      return true;
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
      return false;
    }

    //  ViewState
  }

  public StringProperty getFirstNameProperty()
  {
    return firstnameProperty;
  }

  public StringProperty getLastNameProperty()
  {
    return lastnameProperty;
  }

  public StringProperty getEmailProperty()
  {
    return emailProperty;
  }

  public StringProperty getPasswordProperty()
  {
    return passwordProperty;
  }

  public StringProperty getRepasswordProperty()
  {
    return repasswordProperty;
  }

  public StringProperty getPhoneProperty()
  {
    return phoneProperty;
  }

  public StringProperty getErrorProperty()
  {
    return errorProperty;
  }

  public void receiveBirthDate(LocalDate date)
  {
    this.birthDate=date;
  }


}
