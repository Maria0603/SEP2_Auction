package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.AuctionModel;
import model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class CreateLoginViewModel
{
  private StringProperty headerProperty, firstnameProperty, lastnameProperty, emailProperty, dateProperty, passwordProperty, repasswordProperty, phoneProperty, errorProperty;
  private AuctionModel model;
  private ViewModelState viewState;

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

    dateProperty = new SimpleStringProperty();

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
    //  Null Checks need to be included - TODO: NOT IN THE VIEW MODEL
    if (validateInputCreateAccount())
    {
      return false;
    }
    try
    {
      model.addUser(firstnameProperty.get().trim(),
          lastnameProperty.get().trim(), emailProperty.get().trim(),
          passwordProperty.get(), phoneProperty.get().trim());
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
    if (emailProperty.get().trim().isEmpty() || passwordProperty.get().trim()
        .isEmpty())
    {
      errorProperty.set("Some fields are empty");
      return false;
    }

    try
    {
      User user = model.getUser(emailProperty.get().trim(),
          passwordProperty.get());
      //  Giving the viewState all the user info from the model=>takes from servers database
      viewState.setUserEmail(user.getEmail());
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

  private boolean validateInputCreateAccount()
  {
    errorProperty.set("");
    if (firstnameProperty.get() == null)
    {
      errorProperty.set("Empty first name");
      return true;
    }
    if (lastnameProperty.get() == null)
    {
      errorProperty.set("Empty last name");
      return true;
    }
    if (emailProperty.get() == null)
    {
      errorProperty.set("Empty email name");
      return true;
    }
    if (!emailProperty.get().contains("@"))
    {
      errorProperty.set("Email has to be in 'name@domain' format");
      return true;
    }
    if (phoneProperty.get() == null)
    {
      errorProperty.set("Empty phone name ");
      return true;
    }
    if (passwordProperty.get() == null)
    {
      errorProperty.set("Empty password");
      return true;
    }
    if (repasswordProperty.get() == null)
    {
      errorProperty.set("Empty second password");
      return true;
    }
    if (!passwordProperty.get().equals(repasswordProperty.get()))
    {
      errorProperty.set("Passwords do not match");
      return true;
    }

    try
    {
      long phone = Long.parseLong(phoneProperty.get());
    }
    catch (NumberFormatException e)
    {
      errorProperty.set("Phone number must be a number");
      return true;
    }
    return false;
  }

  //  Has to be used from controller :c
  public boolean ageValidation(LocalDate birthday)
  {
    if (birthday != null)
    {
      LocalDate currentDate = LocalDate.now();
      Period period = Period.between(birthday, currentDate);
      int age = period.getYears();
      if (age >= 18)
      {
        return true;
      }
      else
      {
        throw new IllegalArgumentException("User is not over 18 years old.");
      }
    }
    return false;
  }

}
