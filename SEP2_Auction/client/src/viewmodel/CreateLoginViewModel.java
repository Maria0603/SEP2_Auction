package viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.AuctionModel;

public class CreateLoginViewModel {
    private StringProperty headerProperty,
            firstnameProperty,lastnameProperty, emailProperty,
            passwordProperty, repasswordProperty, phoneProperty,errorProperty;
    private AuctionModel model;
    public CreateLoginViewModel(AuctionModel model){
        this.model = model;
        headerProperty = new SimpleStringProperty();
        firstnameProperty = new SimpleStringProperty();
        lastnameProperty = new SimpleStringProperty();
        emailProperty = new SimpleStringProperty();
        passwordProperty = new SimpleStringProperty();
        repasswordProperty = new SimpleStringProperty();
        phoneProperty = new SimpleStringProperty();
        errorProperty = new SimpleStringProperty();


    }
    public void reset(){
        firstnameProperty.set("");
        lastnameProperty.set("");
        emailProperty.set("");
        passwordProperty.set("");
        repasswordProperty.set("");
        phoneProperty.set("");
        errorProperty.set("");
    }
    public boolean createUser(){
        if(validateInput()){
            return false;
        }
        model.addUser(firstnameProperty.get(),lastnameProperty.get(),emailProperty.get(),passwordProperty.get(),phoneProperty.get());
        return true;
    }
    //  If 'null' is received the login was incorrect... else correct email of a user will added
    public void login(){

        String user = model.getUser(emailProperty.get(),passwordProperty.get());
        if(user.isEmpty()){
            errorProperty.set("Incorrect email or password");
        }

        //  TODO: call the database here
        errorProperty.set("Successful login as: "+user);
    }
    public StringProperty firstnameProperty() {
        return firstnameProperty;
    }
    public StringProperty lastnameProperty() {
        return lastnameProperty;
    }
    public StringProperty emailProperty() {
        return emailProperty;
    }
    public StringProperty passwordProperty() {
        return passwordProperty;
    }
    public StringProperty repasswordProperty() {
        return repasswordProperty;
    }
    public StringProperty phoneProperty() {
        return phoneProperty;
    }
    public StringProperty errorProperty() {
        return errorProperty;
    }
    private boolean validateInput(){
        //  TODO: Add more checks
        errorProperty.set("");
        if(firstnameProperty.get() == null){
            errorProperty.set("Empty first name");
            return true;
        }
        if(lastnameProperty.get() == null){
            errorProperty.set("Empty last name");
            return true;
        }
        if(emailProperty.get() == null){
            errorProperty.set("Empty email name");
            return true;
        }
        if(!emailProperty.get().contains("@")){
            errorProperty.set("Email has to be in 'name@domain' format");
            return true;
        }
        if(phoneProperty.get() == null){
            errorProperty.set("Empty phone name ");
            return true;
        }
        if(passwordProperty.get() == null){
            errorProperty.set("Empty password");
            return true;
        }
        if(repasswordProperty.get() == null){
            errorProperty.set("Empty second password");
            return true;
        }
        if(!passwordProperty.get().equals(repasswordProperty.get())){
            errorProperty.set("Passwords do not match");
            return true;
        }
        return false;
    }
}
