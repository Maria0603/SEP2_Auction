package viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import model.AuctionModel;
import model.Notification;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

public class FixedPaneViewModel implements PropertyChangeListener {
  private StringProperty emailProperty, myProfile_settingsProperty, titleOf_myAuctions_allAuctionsButton, myBids_bannedUsers, notificationsButtonBackgroundProperty;
  private AuctionModel model;
  private ViewModelState state;

  //all button in the fixed pane
  private BooleanProperty buttonsDisabled;
  //notification and sell item buttons, for moderator
  private BooleanProperty displayButtons;
  private BooleanProperty myBidsButtonVisibilityProperty;

  public FixedPaneViewModel(AuctionModel model, ViewModelState state) {
    this.state = state;
    this.model = model;
    model.addListener("Notification", this);
    emailProperty = new SimpleStringProperty();
    titleOf_myAuctions_allAuctionsButton = new SimpleStringProperty();
    notificationsButtonBackgroundProperty = new SimpleStringProperty();
    notificationsButtonBackgroundProperty.set("");

    buttonsDisabled = new SimpleBooleanProperty();
    displayButtons = new SimpleBooleanProperty();
    myBidsButtonVisibilityProperty = new SimpleBooleanProperty();
  }

  public BooleanProperty getDisplayButtons() {
    return displayButtons;
  }

  public void reset() {
    emailProperty.set(state.getUserEmail());

    if(state.isModerator()){
      setWindowAppearanceForModerator();
    }
  }

  private void setWindowAppearanceForModerator(){
    //TODO: add
    titleOf_myAuctions_allAuctionsButton.set("Accounts");
    myBidsButtonVisibilityProperty.set(false);
  }

  public void sellItem() {
    buttonsDisabled.set(true);
  }

  public void setForDisplayProfile() {
    buttonsDisabled.set(false);
    state.setDisplay();
  }

  public void setForResetPassword() {
    buttonsDisabled.set(true);
    state.setResetPassword();
  }

  public void leaveAuctionView() {
    buttonsDisabled.set(false);
    if (state.isModerator())
      displayButtons.set(false);
    else
      displayButtons.set(true);
  }

  public void allAuctions() {
    state.setAllAuctions();
    try {
      if (model.isModerator(state.getUserEmail())) {
        state.setModerator(true);
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void myBids() {
    state.setBids();
  }

  public void myCreatedAuctions() {
    state.setCreatedAuctions();
  }

  public StringProperty getEmailProperty() {
    return emailProperty;
  }

  public StringProperty getNotificationsButtonBackgroundProperty() {
    return notificationsButtonBackgroundProperty;
  }

  public StringProperty getTitleOf_myAuctions_allAuctionsButton(){
    return titleOf_myAuctions_allAuctionsButton;
  }

  public BooleanProperty getButtonsDisabled() {
    return buttonsDisabled;
  }

  public BooleanProperty getMyBidsButtonVisibilityProperty(){
    return myBidsButtonVisibilityProperty;
  }

  public void setForEditProfile() {
    buttonsDisabled.set(true);
    state.setEdit();
  }

  public void setModeratorInfo() {
    state.setLookingAtModerator(true);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    //  TODO: To be combined
    //if(evt.getPropertyName().equals("Notification")){
    //notificationsButtonBackgroundProperty.set("-fx-background-color: #ff0000; ");}

    if (emailProperty.get()
        .equals(((Notification) evt.getNewValue()).getReceiver())) {
      notificationsButtonBackgroundProperty.set(
          "-fx-background-color: #ff0000; ");
    }
  }
}
