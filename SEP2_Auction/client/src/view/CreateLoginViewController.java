package view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Region;
import viewmodel.CreateLoginViewModel;

import javafx.scene.control.*;

import java.util.Optional;

public class CreateLoginViewController
{
  //  Labels
  @FXML private Label headerLabel;
  @FXML private Label errorLabel;
  @FXML private Label firstNameLabel;
  @FXML private Label lastNameLabel;
  @FXML private Label emailLabel;
  @FXML private Label phoneLabel;
  @FXML private Label dateLabel;
  @FXML private Label passwordLabel;
  @FXML private Label repeatPasswordLabel;

  //  Fields
  @FXML private TextField firstNameField;
  @FXML private TextField lastNameField;
  @FXML private TextField emailField;
  @FXML private TextField phoneField;
  @FXML private DatePicker datePicker;
  @FXML private TextField passwordField;
  @FXML private TextField repeatPasswordField;

  //  Buttons
  @FXML private Button confirmButton;
  @FXML private Button resetPasswordButton;
  @FXML private Button cancelButton;
  @FXML private Button login_createAccountButton;
  private Region root;
  private CreateLoginViewModel viewModel;
  private ViewHandler viewHandler;

  public void init(ViewHandler viewHandler,
      CreateLoginViewModel createLoginViewModel, Region root,
      WindowType windowType)
  {
    this.root = root;
    this.viewHandler = viewHandler;
    this.viewModel = createLoginViewModel;

    bindValues();
    bindVisibleProperty();
    bindDisableProperty();

    reset(windowType);
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(WindowType type)
  {
    errorLabel.setText("");
    switch (type)
    {
      case SIGN_UP:
        viewModel.setForCreate();
        break;
      case LOG_IN:
        viewModel.setForLogin();
        break;
      case RESET_PASSWORD:
        viewModel.setForResetPassword();
        login_createAccountButton.setLayoutX(920);
        headerLabel.setLayoutX(400);
        confirmButton.setLayoutY(580);
        resetPasswordButton.setLayoutY(580);
        resetPasswordButton.setLayoutX(440);
        errorLabel.setLayoutY(510);
        errorLabel.setLayoutX(55);
        break;
      case DISPLAY_PROFILE:
        viewModel.setForDisplayProfile();

        login_createAccountButton.setLayoutX(920);
        headerLabel.setLayoutX(400);
        resetPasswordButton.setLayoutY(580);
        resetPasswordButton.setLayoutX(440);
        cancelButton.setLayoutY(580);
        cancelButton.setLayoutX(810);
        errorLabel.setLayoutY(510);
        errorLabel.setLayoutX(55);
        confirmButton.setLayoutY(580);
        break;

      case EDIT_PROFILE:
        viewModel.setForEditProfile();

        break;
    }
  }

  @FXML public void onEnter(ActionEvent actionEvent)
  {
    //confirm();
  }

  @FXML public void confirmButtonPressed()
  {
    viewModel.confirm();
    if (errorLabel.getText().isEmpty())
      viewHandler.openView(WindowType.ALL_AUCTIONS);
  }

  @FXML public void cancelButtonPressed()
  {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation");
    alert.setHeaderText("Are you sure you want to leave?");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK)
    {
      viewHandler.openView(WindowType.DISPLAY_PROFILE);
    }
  }

  @FXML public void resetPasswordButtonPressed()
  {
    if(headerLabel.getText().equals("Edit profile")){
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Confirmation");
      alert.setHeaderText("Do you really want to delete your account?");
      Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK)
      {
        viewModel.setForLogin();
        viewModel.deleteAccount();
        viewHandler.openView(WindowType.LOG_IN);
      }
    }
    else
      reset(WindowType.RESET_PASSWORD);
  }

  @FXML public void loginButtonPressed()
  {
    if (headerLabel.getText().equals("Create account"))
      reset(WindowType.LOG_IN);
    else if (headerLabel.getText().equals("Login"))
      reset(WindowType.SIGN_UP);
    else if (headerLabel.getText().equals("Your profile"))
      reset(WindowType.EDIT_PROFILE);
  }

  private void bindDisableProperty()
  {
    firstNameField.disableProperty()
        .bindBidirectional(viewModel.getDisableProperty());
    lastNameField.disableProperty()
        .bindBidirectional(viewModel.getDisableProperty());
    emailField.disableProperty()
        .bindBidirectional(viewModel.getDisableProperty());
    passwordField.disableProperty()
        .bindBidirectional(viewModel.getDisableProperty());
    repeatPasswordField.disableProperty()
        .bindBidirectional(viewModel.getDisableProperty());
    phoneField.disableProperty()
        .bindBidirectional(viewModel.getDisableProperty());
    datePicker.disableProperty()
        .bindBidirectional(viewModel.getDisableProperty());

    datePicker.getEditor().setDisable(true);
  }

  private void bindVisibleProperty()
  {
    //to control the visibility from the view model
    //login components
    emailField.visibleProperty()
        .bindBidirectional(viewModel.getEmailVisibility());
    emailLabel.visibleProperty()
        .bindBidirectional(viewModel.getEmailVisibility());

    passwordField.visibleProperty()
        .bindBidirectional(viewModel.getPasswordVisibility());
    passwordLabel.visibleProperty()
        .bindBidirectional(viewModel.getPasswordVisibility());

    //information components
    firstNameField.visibleProperty()
        .bindBidirectional(viewModel.getInformationVisibility());
    firstNameLabel.visibleProperty()
        .bindBidirectional(viewModel.getInformationVisibility());

    lastNameField.visibleProperty()
        .bindBidirectional(viewModel.getInformationVisibility());
    lastNameLabel.visibleProperty()
        .bindBidirectional(viewModel.getInformationVisibility());

    phoneField.visibleProperty()
        .bindBidirectional(viewModel.getInformationVisibility());
    phoneLabel.visibleProperty()
        .bindBidirectional(viewModel.getInformationVisibility());

    //birthday components - different visibility for moderator
    datePicker.visibleProperty()
        .bindBidirectional(viewModel.getBirthdayVisibility());
    dateLabel.visibleProperty()
        .bindBidirectional(viewModel.getBirthdayVisibility());

    //reset password components
    repeatPasswordField.visibleProperty()
        .bindBidirectional(viewModel.getResetPasswordVisibility());
    repeatPasswordLabel.visibleProperty()
        .bindBidirectional(viewModel.getResetPasswordVisibility());

    //special components
    resetPasswordButton.visibleProperty()
        .bindBidirectional(viewModel.getResetPasswordButtonVisibility());
    resetPasswordButton.textProperty().
            bindBidirectional(viewModel.getResetPasswordButtonText());
    login_createAccountButton.textProperty()
        .bindBidirectional(viewModel.getLogin_createButtonText());
    login_createAccountButton.visibleProperty()
        .bindBidirectional(viewModel.getLogin_createButtonVisibility());
    emailLabel.textProperty().bindBidirectional(viewModel.getEmailLabelText());
    cancelButton.visibleProperty()
        .bindBidirectional(viewModel.getCancelButtonVisibility());
    confirmButton.visibleProperty()
        .bindBidirectional(viewModel.getConfirmButtonVisibility());
  }

  private void bindValues()
  {
    //  Bind to viewModel
    firstNameField.textProperty()
        .bindBidirectional(viewModel.getFirstNameProperty());
    lastNameField.textProperty()
        .bindBidirectional(viewModel.getLastNameProperty());
    emailField.textProperty().bindBidirectional(viewModel.getEmailProperty());
    passwordField.textProperty()
        .bindBidirectional(viewModel.getPasswordProperty());
    repeatPasswordField.textProperty()
        .bindBidirectional(viewModel.getRepasswordProperty());
    phoneField.textProperty().bindBidirectional(viewModel.getPhoneProperty());
    errorLabel.textProperty().bindBidirectional(viewModel.getErrorProperty());
    headerLabel.textProperty().bindBidirectional(viewModel.getHeaderProperty());

    Bindings.bindBidirectional(datePicker.valueProperty(),
        viewModel.getBirthDate());
  }
}
