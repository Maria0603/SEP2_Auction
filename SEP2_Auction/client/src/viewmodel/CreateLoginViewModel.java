package viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.AuctionModel;

import java.sql.SQLException;
import java.time.LocalDate;

public class CreateLoginViewModel
{
  private StringProperty headerProperty, firstNameProperty, lastNameProperty, emailProperty, passwordProperty, repasswordProperty, phoneProperty, errorProperty;
  private AuctionModel model;
  private ViewModelState viewState;
  private LocalDate birthDate;

  private BooleanProperty informationVisibility; //first name, last name, phone, birthday
  private BooleanProperty loginVisibility; //email, password
  private BooleanProperty resetPasswordVisibility; //repeat password - special case
  private BooleanProperty birthdayVisibility; //special case for moderator

  private StringProperty emailLabelText;
  private StringProperty login_createButtonText;
  private BooleanProperty login_createButtonVisibility;
  private BooleanProperty resetPasswordButtonVisibility;
  private BooleanProperty cancelButtonVisibility;

  public CreateLoginViewModel(AuctionModel model, ViewModelState viewState)
  {
    this.viewState = viewState;
    this.model = model;
    headerProperty = new SimpleStringProperty();
    firstNameProperty = new SimpleStringProperty();
    lastNameProperty = new SimpleStringProperty();
    emailProperty = new SimpleStringProperty();
    passwordProperty = new SimpleStringProperty();
    repasswordProperty = new SimpleStringProperty();
    phoneProperty = new SimpleStringProperty();
    errorProperty = new SimpleStringProperty();
    birthDate=null;

    informationVisibility=new SimpleBooleanProperty();
    loginVisibility=new SimpleBooleanProperty();
    resetPasswordVisibility=new SimpleBooleanProperty();
    birthdayVisibility=new SimpleBooleanProperty();

    login_createButtonText=new SimpleStringProperty();
    login_createButtonVisibility=new SimpleBooleanProperty();
    resetPasswordButtonVisibility=new SimpleBooleanProperty();
    emailLabelText=new SimpleStringProperty();
    cancelButtonVisibility=new SimpleBooleanProperty();

    reset();
  }


  public void reset()
  {
    firstNameProperty.set("");
    lastNameProperty.set("");
    emailProperty.set("");
    passwordProperty.set("");
    repasswordProperty.set("");
    phoneProperty.set("");
    errorProperty.set("");
    birthDate=null;
  }
  public void setForCreate()
  {
    viewState.setCreate();
    login_createButtonVisibility.set(true);
    resetPasswordButtonVisibility.set(false);
    login_createButtonText.set("Login");

    informationVisibility.set(true);
    loginVisibility.set(true);
    resetPasswordVisibility.set(true);
    birthdayVisibility.set(true);
    errorProperty.set("");
    headerProperty.set("Create account");
    emailLabelText.set("Email");
    cancelButtonVisibility.set(false);
  }

  private void createUser()
  {
    errorProperty.set("");
    try
    {
      String email=model.addUser(firstNameProperty.get().trim(),
          lastNameProperty.get().trim(), emailProperty.get().trim(),
          passwordProperty.get(), repasswordProperty.get(), phoneProperty.get().trim(), birthDate);
      viewState.setUserEmail(email);
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
    }
  }

  public void setForLogin()
  {
    viewState.setLogin();
    login_createButtonVisibility.set(true);
    resetPasswordButtonVisibility.set(false);
    resetPasswordVisibility.set(false);
    informationVisibility.set(false);
    loginVisibility.set(true);
    birthdayVisibility.set(false);
    login_createButtonText.set("Create account");
    headerProperty.set("Login");
    errorProperty.set("");
    emailLabelText.set("Email");
    cancelButtonVisibility.set(false);

  }

  private void login()
  {
    errorProperty.set("");
    try
    {
      String user = model.login(emailProperty.get().trim(),
          passwordProperty.get());
      //  Giving the viewState all the user info from the model=>takes from servers database
      viewState.setUserEmail(user);
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
    }

    //  ViewState
  }
  public void setForResetPassword()
  {
    viewState.setResetPassword();
    headerProperty.set("Reset password");
    resetPasswordVisibility.set(true);
    loginVisibility.set(true);
    informationVisibility.set(false);
    birthdayVisibility.set(false);
    resetPasswordButtonVisibility.set(false);
    login_createButtonVisibility.set(false);
    cancelButtonVisibility.set(true);


    emailLabelText.set("Old password");
  }
  private void resetPassword()
  {
    errorProperty.set("");
    try
    {
      model.resetPassword(viewState.getUserEmail(), emailProperty.get(), passwordProperty.get(), repasswordProperty.get());
    }
    catch(SQLException e)
    {
      errorProperty.set(e.getMessage());
    }
  }
  public void confirm()
  {
    if(viewState.isResetPassword())
      resetPassword();
    else if (viewState.isCreate())
      createUser();
    else if(viewState.isLogin())
      login();
  }

  public StringProperty getFirstNameProperty()
  {
    return firstNameProperty;
  }

  public StringProperty getLastNameProperty()
  {
    return lastNameProperty;
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
  public BooleanProperty getLoginVisibility()
  {
    return loginVisibility;
  }
  public BooleanProperty getInformationVisibility()
  {
    return informationVisibility;
  }
  public BooleanProperty getResetPasswordVisibility()
  {
    return resetPasswordVisibility;
  }
  public BooleanProperty getBirthdayVisibility()
  {
    return birthdayVisibility;
  }
  public StringProperty getLogin_createButtonText()
  {
    return login_createButtonText;
  }
  public BooleanProperty getResetPasswordButtonVisibility()
  {
    return resetPasswordButtonVisibility;
  }
  public BooleanProperty getLogin_createButtonVisibility()
  {
    return login_createButtonVisibility;
  }

  public StringProperty getEmailLabelText()
  {
    return emailLabelText;
  }
  public BooleanProperty getCancelButtonVisibility()
  {
    return cancelButtonVisibility;
  }
  public StringProperty getHeaderProperty()
  {
    return headerProperty;
  }
}
