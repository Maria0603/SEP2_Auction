package view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Region;
import viewmodel.CreateLoginViewModel;

import javafx.scene.control.*;

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

  public void init(ViewHandler viewHandler, CreateLoginViewModel createLoginViewModel,
      Region root, WindowType windowType)
  {
    this.root = root;
    this.viewHandler = viewHandler;
    this.viewModel = createLoginViewModel;

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

    Bindings.bindBidirectional(datePicker.valueProperty(), viewModel.getBirthDate());
    //to control the visibility from the view model
    //login components
    emailField.visibleProperty().bindBidirectional(viewModel.getLoginVisibility());
    emailLabel.visibleProperty().bindBidirectional(viewModel.getLoginVisibility());

    passwordField.visibleProperty().bindBidirectional(viewModel.getLoginVisibility());
    passwordLabel.visibleProperty().bindBidirectional(viewModel.getLoginVisibility());

    //information components
    firstNameField.visibleProperty().bindBidirectional(viewModel.getInformationVisibility());
    firstNameLabel.visibleProperty().bindBidirectional(viewModel.getInformationVisibility());

    lastNameField.visibleProperty().bindBidirectional(viewModel.getInformationVisibility());
    lastNameLabel.visibleProperty().bindBidirectional(viewModel.getInformationVisibility());

    phoneField.visibleProperty().bindBidirectional(viewModel.getInformationVisibility());
    phoneLabel.visibleProperty().bindBidirectional(viewModel.getInformationVisibility());

    //birthday components - different visibility for moderator
    datePicker.visibleProperty().bindBidirectional(viewModel.getBirthdayVisibility());
    dateLabel.visibleProperty().bindBidirectional(viewModel.getBirthdayVisibility());

    //reset password components
    repeatPasswordField.visibleProperty().bindBidirectional(viewModel.getResetPasswordVisibility());
    repeatPasswordLabel.visibleProperty().bindBidirectional(viewModel.getResetPasswordVisibility());

    //special components
    resetPasswordButton.visibleProperty().bindBidirectional(viewModel.getResetPasswordButtonVisibility());
    login_createAccountButton.textProperty().bindBidirectional(viewModel.getLogin_createButtonText());
    login_createAccountButton.visibleProperty().bindBidirectional(viewModel.getLogin_createButtonVisibility());
    emailLabel.textProperty().bindBidirectional(viewModel.getEmailLabelText());
    cancelButton.visibleProperty().bindBidirectional(viewModel.getCancelButtonVisibility());

    datePicker.getEditor().setDisable(true);
    reset(windowType);
    errorLabel.setText("");
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(WindowType type)
  {
    viewModel.reset();  //  initial reset
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
        break;
      case DISPLAY_PROFILE:
        viewModel.setForDisplayProfile();
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
    if(errorLabel.getText().isEmpty())
      viewHandler.openView(WindowType.ALL_AUCTIONS);
  }

  @FXML public void cancelButtonPressed(ActionEvent actionEvent)
  {
    //errorLabel.setText("CANCEL BUTTON: to be implemented");
  }

  @FXML public void resetPasswordButtonPressed(ActionEvent actionEvent)
  {
    reset(WindowType.RESET_PASSWORD);
  }

  @FXML public void loginButtonPressed(ActionEvent actionEvent)
  {
    if(headerLabel.getText().equals("Create account"))
      reset(WindowType.LOG_IN);
      else reset(WindowType.SIGN_UP);
  }
}
