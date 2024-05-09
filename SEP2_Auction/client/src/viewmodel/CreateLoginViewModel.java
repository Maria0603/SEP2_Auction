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
  private StringProperty headerProperty, firstnameProperty, lastnameProperty, emailProperty, dateProperty, passwordProperty, repasswordProperty, phoneProperty, errorProperty;
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

    //dateProperty = new SimpleStringProperty();
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
      errorProperty.set(e.getLocalizedMessage());
      return false;
    }
    return true;
  }

  public boolean login()
  {
    errorProperty.set("");
    if (emailProperty.get().trim().isEmpty() || passwordProperty.get().trim()
        .isEmpty())
    {
      errorProperty.set("Some fields are empty");
      return false;
    }

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
      errorProperty.set(e.getLocalizedMessage());
      return false;
    }

    //  ViewState
  }

  public StringProperty firstnameProperty()
  {
    return firstnameProperty;
  }

  public StringProperty lastnameProperty()
  {
    return lastnameProperty;
  }

  public StringProperty emailProperty()
  {
    return emailProperty;
  }

  public StringProperty passwordProperty()
  {
    return passwordProperty;
  }

  public StringProperty repasswordProperty()
  {
    return repasswordProperty;
  }

  public StringProperty phoneProperty()
  {
    return phoneProperty;
  }

  public StringProperty errorProperty()
  {
    return errorProperty;
  }

  public StringProperty dateProperty()
  {
    return dateProperty;
  }

  public void receiveBirthDate(LocalDate date)
  {
    this.birthDate=date;
  }


}
