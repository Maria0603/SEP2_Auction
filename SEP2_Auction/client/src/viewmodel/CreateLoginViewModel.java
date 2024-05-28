package viewmodel;

import javafx.beans.property.*;
import model.domain.User;
import model.UserModel;

import java.sql.SQLException;
import java.time.LocalDate;

public class CreateLoginViewModel
{
  private final StringProperty headerProperty;
  private final StringProperty firstNameProperty;
  private final StringProperty lastNameProperty;
  private final StringProperty emailProperty;
  private final StringProperty passwordProperty;
  private final StringProperty repasswordProperty, phoneProperty, errorProperty;
  private final UserModel model;
  private final ViewModelState viewState;
  private final ObjectProperty<LocalDate> birthDate;

  private final BooleanProperty informationVisibility; //first name, last name, phone, birthday
  private final BooleanProperty emailVisibility; //email
  private final BooleanProperty passwordVisibility; //password
  private final BooleanProperty resetPasswordVisibility; //repeat password - special case
  private final BooleanProperty birthdayVisibility; //special case for moderator

  private final StringProperty emailLabelText;
  private final StringProperty login_createButtonText;
  private final BooleanProperty login_createButtonVisibility;
  private final StringProperty resetPasswordButtonText;
  private final BooleanProperty resetPasswordButtonVisibility;
  private final BooleanProperty cancelButtonVisibility;
  private final BooleanProperty confirmButtonVisibility;

  private final BooleanProperty disableProperty;

  public CreateLoginViewModel(UserModel model, ViewModelState viewState)
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
    birthDate = new SimpleObjectProperty<>();

    informationVisibility = new SimpleBooleanProperty();
    emailVisibility = new SimpleBooleanProperty();
    passwordVisibility = new SimpleBooleanProperty();
    resetPasswordVisibility = new SimpleBooleanProperty();
    birthdayVisibility = new SimpleBooleanProperty();

    login_createButtonText = new SimpleStringProperty();
    login_createButtonVisibility = new SimpleBooleanProperty();
    resetPasswordButtonText = new SimpleStringProperty();
    resetPasswordButtonVisibility = new SimpleBooleanProperty();
    emailLabelText = new SimpleStringProperty();
    cancelButtonVisibility = new SimpleBooleanProperty();
    confirmButtonVisibility = new SimpleBooleanProperty();

    disableProperty = new SimpleBooleanProperty();

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
      String email = model.addUser(firstNameProperty.get().trim(),
          lastNameProperty.get().trim(), emailProperty.get().trim(),
          passwordProperty.get(), repasswordProperty.get(),
          phoneProperty.get().trim(), birthDate.get());
      viewState.setUserEmail(email);
      viewState.setModerator(model.isModerator(email));
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
    if (errorProperty.get().isEmpty())
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
      viewState.setUserEmail(user);
      viewState.setModerator(model.isModerator(user));
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
    if (errorProperty.get().isEmpty())
    {
      reset();
    }
  }

  public void setForResetPassword()
  {
    reset();
    errorProperty.set("");
    headerProperty.set("Reset password");
    resetPasswordButtonText.set("Reset Password");
    emailLabelText.set("Old password");

    viewState.setResetPassword();

    resetPasswordVisibility.set(true);
    emailVisibility.set(true);
    passwordVisibility.set(true);
    cancelButtonVisibility.set(true);
    confirmButtonVisibility.set(true);

    informationVisibility.set(false);
    birthdayVisibility.set(false);
    resetPasswordButtonVisibility.set(false);
    login_createButtonVisibility.set(false);
    disableProperty.set(false);
  }

  private void resetPassword()
  {
    errorProperty.set("");
    try
    {
      model.resetPassword(viewState.getUserEmail(), emailProperty.get(),
          passwordProperty.get(), repasswordProperty.get());
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
    if (errorProperty.get().isEmpty())
    {
      reset();
    }
  }

  public void setForDisplayProfile()
  {
    viewState.setDisplay();

    resetPasswordButtonText.set("Reset Password");
    login_createButtonText.set("Edit");
    errorProperty.set("");
    headerProperty.set("Your profile");
    emailLabelText.set("Email");

    login_createButtonVisibility.set(true);
    resetPasswordButtonVisibility.set(true);
    informationVisibility.set(true);
    emailVisibility.set(true);
    resetPasswordVisibility.set(false);
    disableProperty.set(true);

    birthdayVisibility.set(!viewState.isModerator());

    cancelButtonVisibility.set(false);
    passwordVisibility.set(false);
    confirmButtonVisibility.set(false);

    displayProfile();
  }

  private void displayProfile()
  {
    try
    {
      birthDate.set(null);
      User userToBeDisplayed;
      if (viewState.isLookingAtModerator())
      {
        setForDisplayModeratorInfo();
        userToBeDisplayed = model.getModeratorInfo();
        viewState.setLookingAtModerator(false);
      }
      else
        userToBeDisplayed = model.getUser(viewState.getUserEmail());

      if (userToBeDisplayed != null)
      {
        firstNameProperty.set(userToBeDisplayed.getFirstname());
        lastNameProperty.set(userToBeDisplayed.getLastname());
        emailProperty.set(userToBeDisplayed.getEmail());
        phoneProperty.set(userToBeDisplayed.getPhone());
        birthDate.set(userToBeDisplayed.getBirthday());
      }
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
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

    errorProperty.set("");
    headerProperty.set("Edit profile");
    emailLabelText.set("Email");
    resetPasswordButtonText.set("Delete Account");
    passwordProperty.set("");

    resetPasswordButtonVisibility.set(!viewState.isModerator());
    birthdayVisibility.set(!viewState.isModerator());

    login_createButtonVisibility.set(false);
    resetPasswordVisibility.set(false);
    disableProperty.set(false);

    informationVisibility.set(true);
    emailVisibility.set(true);
    cancelButtonVisibility.set(true);
    passwordVisibility.set(true);
    confirmButtonVisibility.set(true);
  }

  public void deleteAccount()
  {
    errorProperty.set("");
    try
    {
      model.deleteAccount(viewState.getUserEmail(), passwordProperty.get());
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
  }

  private void edit()
  {
    errorProperty.set("");
    try
    {
      model.editInformation(viewState.getUserEmail(),
          firstNameProperty.get().trim(), lastNameProperty.get().trim(),
          emailProperty.get().trim(), passwordProperty.get(),
          phoneProperty.get().trim(), birthDate.get());
      if (!viewState.isModerator())
        viewState.setUserEmail(emailProperty.get());
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
  }

  public void confirm()
  {
    if (viewState.isResetPassword())
      resetPassword();
    else if (viewState.isCreate())
      createUser();
    else if (viewState.isLogin())
      login();
    else if (viewState.isEdit())
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

  public StringProperty getResetPasswordButtonText()
  {
    return resetPasswordButtonText;
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
