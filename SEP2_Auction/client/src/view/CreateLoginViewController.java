package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Region;
import viewmodel.CreateLoginViewModel;

import javafx.scene.control.*;

public class CreateLoginViewController {
    //  Labels
    @FXML private Label headerLabel;
    @FXML private Label errorLabel;
    @FXML private Label firstnameLabel;
    @FXML private Label lastnameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label dateLabel;
    @FXML private Label passwordLabel;
    @FXML private Label repeatPasswordLabel;

    //  Fields
    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker datePicker;
    @FXML private TextField passwordField;
    @FXML private TextField repasswordField;

    //  Buttons
    @FXML private Button createAccount_SaveChangesButton;
    @FXML private Button resetPasswordButton;
    @FXML private Button cancelButton;
    @FXML private Button loginButton;
    private Region root;
    private CreateLoginViewModel viewModel;
    private ViewHandler viewHandler;

    //  Purpose of this is to switch between Create User and Login
    private boolean isLogin;

    public void init(ViewHandler viewHandler, CreateLoginViewModel viewModel, Region root, WindowType windowType){
        this.root = root;
        this.viewHandler = viewHandler;
        this.viewModel = viewModel;

        //  Bind to viewModel
        firstnameField.textProperty().bindBidirectional(viewModel.firstnameProperty());
        lastnameField.textProperty().bindBidirectional(viewModel.lastnameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        repasswordField.textProperty().bindBidirectional(viewModel.repasswordProperty());
        phoneField.textProperty().bindBidirectional(viewModel.phoneProperty());
        errorLabel.textProperty().bindBidirectional(viewModel.errorProperty());



        //  reset(windowType);
        errorLabel.setText("");
    }
    public Region getRoot(){return root;}

    public void reset(WindowType type){
        viewModel.reset();  //  initial reset
        switch (type){
            case SIGN_UP:
                setForCreateAccount();
                break;
            case LOG_IN:
                setForLogin();
                break;
        }
    }

    //  TODO: maybe try to move this to the viewModel
    private void setForCreateAccount(){
        //  Buttons
        createAccount_SaveChangesButton.textProperty().set("Confirm");
        resetPasswordButton.setVisible(true);
        loginButton.textProperty().set("Login");
        //  Fields
        firstnameField.setVisible(true);
        lastnameField.setVisible(true);
        phoneField.setVisible(true);
        datePicker.setVisible(true);
        passwordField.setVisible(true);
        repasswordField.setVisible(true);
        errorLabel.textProperty().set("");
        //  Labels
        headerLabel.textProperty().set("Create Account");
        firstnameLabel.setVisible(true);
        lastnameLabel.setVisible(true);
        emailLabel.setVisible(true);
        phoneLabel.setVisible(true);
        dateLabel.setVisible(true);
        passwordLabel.setVisible(true);
        repeatPasswordLabel.setVisible(true);

        isLogin = false;
    }

    private void setForLogin(){
        //  Buttons
        createAccount_SaveChangesButton.textProperty().set("Confirm");
        resetPasswordButton.setVisible(false);
        loginButton.textProperty().set("Create Account");
        //  Fields
        headerLabel.textProperty().set("Login");
        firstnameField.setVisible(false);
        lastnameField.setVisible(false);
        phoneField.setVisible(false);
        datePicker.setVisible(false);
        passwordField.setVisible(true);
        repasswordField.setVisible(false);
        errorLabel.textProperty().set("");
        //  Labels
        firstnameLabel.setVisible(false);
        lastnameLabel.setVisible(false);
        emailLabel.setVisible(true);    //  only this
        phoneLabel.setVisible(false);
        dateLabel.setVisible(false);
        passwordLabel.setVisible(true); //  and this visible
        repeatPasswordLabel.setVisible(false);

        isLogin = true;
    }
    private void confirm(){
        if(!isLogin){
            createAccountButtonPress();
        }
        else if(isLogin){
            loginAfterPress();
        }
    }
    private void createAccountButtonPress(){
        try{
            /*if(viewModel.ageValidation(datePicker.getValue()) && viewModel.createUser()){
                reset(WindowType.LOG_IN);
                datePicker.setValue(null);  //  has to be reset for 'ageValidation'
            }*/
            if(viewModel.createUser()){
                reset(WindowType.LOG_IN);
                datePicker.setValue(null);  //  has to be reset for 'ageValidation'
            }
        }
        //  Database exceptions (like if email is in system or password matches the email)
        catch (IllegalArgumentException e){
            errorLabel.setText(e.getLocalizedMessage());
        }
    }

    private void loginAfterPress(){
        if(viewModel.login()){
            viewHandler.openView(WindowType.ALL_AUCTIONS);
        }
    }
    @FXML
    public void onEnter(ActionEvent actionEvent) {  confirm();  }
    @FXML
    public void createAccount_SaveChangesButtonPressed(ActionEvent actionEvent) {
        confirm();
    }
    @FXML
    public void cancelButtonPressed(ActionEvent actionEvent) {
        errorLabel.setText("CANCEL BUTTON: to be implemented");
    }
    @FXML
    public void resetPasswordButtonPressed(ActionEvent actionEvent) {
        errorLabel.setText("RESET PASSWORD BUTTON: to be implemented");
    }
    @FXML
    public void loginButtonPressed(ActionEvent actionEvent) {
        if(isLogin){
            reset(WindowType.SIGN_UP);
        }
        else{
            reset(WindowType.LOG_IN);
        }
    }
}
