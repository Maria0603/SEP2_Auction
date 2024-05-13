package viewmodel;

import javafx.beans.property.*;
import model.AuctionModel;
import model.User;

import java.sql.SQLException;
import java.time.LocalDate;

public class CreateLoginViewModel
{
  private StringProperty headerProperty, firstNameProperty, lastNameProperty, emailProperty, passwordProperty, repasswordProperty, phoneProperty, errorProperty;
  private AuctionModel model;
  private ViewModelState viewState;
  private ObjectProperty<LocalDate> birthDate;

  private BooleanProperty informationVisibility; //first name, last name, phone, birthday
  private BooleanProperty emailVisibility; //email
  private BooleanProperty passwordVisibility; //password
  private BooleanProperty resetPasswordVisibility; //repeat password - special case
  private BooleanProperty birthdayVisibility; //special case for moderator

  private StringProperty emailLabelText;
  private StringProperty login_createButtonText;
  private BooleanProperty login_createButtonVisibility;
  private BooleanProperty resetPasswordButtonVisibility;
  private BooleanProperty cancelButtonVisibility;
  private BooleanProperty confirmButtonVisibility;

  private BooleanProperty disableProperty;

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
    birthDate=new SimpleObjectProperty<>();

    informationVisibility=new SimpleBooleanProperty();
    emailVisibility =new SimpleBooleanProperty();
    passwordVisibility=new SimpleBooleanProperty();
    resetPasswordVisibility=new SimpleBooleanProperty();
    birthdayVisibility=new SimpleBooleanProperty();

    login_createButtonText=new SimpleStringProperty();
    login_createButtonVisibility=new SimpleBooleanProperty();
    resetPasswordButtonVisibility=new SimpleBooleanProperty();
    emailLabelText=new SimpleStringProperty();
    cancelButtonVisibility=new SimpleBooleanProperty();
    confirmButtonVisibility=new SimpleBooleanProperty();

    disableProperty=new SimpleBooleanProperty();

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
    birthDate.set(LocalDate.now());
  }
  public void setForCreate()
  {
    viewState.setCreate();
    reset();
    login_createButtonVisibility.set(true);
    resetPasswordButtonVisibility.set(false);
    login_createButtonText.set("Login");

    informationVisibility.set(true);
    emailVisibility.set(true);
    passwordVisibility.set(true);
    resetPasswordVisibility.set(true);
    birthdayVisibility.set(true);
    confirmButtonVisibility.set(true);

    errorProperty.set("");
    headerProperty.set("Create account");
    emailLabelText.set("Email");
    cancelButtonVisibility.set(false);
    disableProperty.set(false);
  }

  private void createUser()
  {
    errorProperty.set("");
    try
    {
      String email=model.addUser(firstNameProperty.get().trim(),
          lastNameProperty.get().trim(), emailProperty.get().trim(),
          passwordProperty.get(), repasswordProperty.get(), phoneProperty.get().trim(), birthDate.get());
      viewState.setUserEmail(email);
      viewState.setModerator(model.isModerator(email));
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
    }
    if(errorProperty.get().isEmpty())
    {
      reset();
    }
  }

  public void setForLogin()
  {
    viewState.setLogin();
    reset();
    login_createButtonVisibility.set(true);
    resetPasswordButtonVisibility.set(false);
    resetPasswordVisibility.set(false);
    informationVisibility.set(false);
    emailVisibility.set(true);
    passwordVisibility.set(true);
    confirmButtonVisibility.set(true);

    birthdayVisibility.set(false);
    login_createButtonText.set("Create account");
    headerProperty.set("Login");
    errorProperty.set("");
    emailLabelText.set("Email");
    cancelButtonVisibility.set(false);
    disableProperty.set(false);


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

      viewState.setModerator(model.isModerator(user));
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
    }
    if(errorProperty.get().isEmpty())
    {
      reset();
    }

    //  ViewState
  }
  public void setForResetPassword()
  {
    errorProperty.set("");
    viewState.setResetPassword();

    reset();
    headerProperty.set("Reset password");
    resetPasswordVisibility.set(true);
    emailVisibility.set(true);
    passwordVisibility.set(true);
    informationVisibility.set(false);
    birthdayVisibility.set(false);
    resetPasswordButtonVisibility.set(false);
    login_createButtonVisibility.set(false);
    cancelButtonVisibility.set(true);
    confirmButtonVisibility.set(true);



    emailLabelText.set("Old password");
    disableProperty.set(false);

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
      //e.printStackTrace();
    }
    if(errorProperty.get().isEmpty())
    {
      reset();
    }
  }
  public void setForDisplayProfile()
  {
    viewState.setDisplay();

    resetPasswordButtonVisibility.set(true);
    login_createButtonVisibility.set(true);
    login_createButtonText.set("Edit");
    informationVisibility.set(true);
    emailVisibility.set(true);
    resetPasswordVisibility.set(false);
    birthdayVisibility.set(!viewState.isModerator());
    errorProperty.set("");
    headerProperty.set("Your profile");
    emailLabelText.set("Email");
    cancelButtonVisibility.set(false);
    passwordVisibility.set(false);
    confirmButtonVisibility.set(false);



    disableProperty.set(true);
    displayProfile();
  }

  private void displayProfile()
  {
    try
    {
      birthDate.set(null);
      User userToBeDisplayed;
      if(viewState.isLookingAtModerator())
      {
        setForDisplayModeratorInfo();
        userToBeDisplayed = model.getModeratorInfo();
        viewState.setLookingAtModerator(false);
      }
      else userToBeDisplayed=model.getUser(viewState.getUserEmail());

      if(userToBeDisplayed!=null)
      {
        firstNameProperty.set(userToBeDisplayed.getFirstname());
        lastNameProperty.set(userToBeDisplayed.getLastname());
        emailProperty.set(userToBeDisplayed.getEmail());
        phoneProperty.set(userToBeDisplayed.getPhone());
        birthDate.set(userToBeDisplayed.getBirthday());
      }
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
    }
  }
  private void setForDisplayModeratorInfo()
  {
    birthdayVisibility.set(false);
    resetPasswordButtonVisibility.set(false);
    login_createButtonVisibility.set(false);
    headerProperty.set("Moderator's information");
  }
  public void setForEditProfile()
  {
    viewState.setEdit();

    resetPasswordButtonVisibility.set(false);
    login_createButtonVisibility.set(false);
    //login_createButtonText.set("Edit");
    informationVisibility.set(true);
    emailVisibility.set(true);
    resetPasswordVisibility.set(false);
    birthdayVisibility.set(!viewState.isModerator());
    errorProperty.set("");
    headerProperty.set("Edit profile");
    emailLabelText.set("Email");
    cancelButtonVisibility.set(true);
    passwordProperty.set("");
    passwordVisibility.set(true);
    confirmButtonVisibility.set(true);

    disableProperty.set(false);
  }
  private void edit()
  {
    errorProperty.set("");
    try
    {
      model.editInformation(viewState.getUserEmail(), firstNameProperty.get().trim(), lastNameProperty.get().trim(), emailProperty.get().trim(), passwordProperty.get(), phoneProperty.get().trim(), birthDate.get());
      if(!viewState.isModerator())
        viewState.setUserEmail(emailProperty.get());
    }
    catch(SQLException e)
    {
      errorProperty.set(e.getMessage());
      //e.printStackTrace();
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
    else if(viewState.isEdit())
      edit();
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

  public ObjectProperty<LocalDate> getBirthDate()
  {
    return birthDate;
  }
  public BooleanProperty getEmailVisibility()
  {
    return emailVisibility;
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
  public BooleanProperty getPasswordVisibility()
  {
    return passwordVisibility;
  }
  public BooleanProperty getDisableProperty()
  {
    return disableProperty;
  }
  public BooleanProperty getConfirmButtonVisibility()
  {
    return confirmButtonVisibility;
  }
}
