package viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import model.domain.Notification;
import model.UserModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

public class FixedPaneViewModel implements PropertyChangeListener
{
  private final StringProperty emailProperty;
  private final StringProperty titleOf_myAuctions_allAuctionsButton;
  private final StringProperty notificationsButtonBackgroundProperty;
  private final UserModel model;
  private final ViewModelState state;

  //all buttons in the fixed pane
  private final BooleanProperty buttonsDisabled;
  private final BooleanProperty myBidsButtonVisibility;
  private final BooleanProperty sellItemButtonVisibility;
  private final BooleanProperty notificationsButtonVisibility;
  private final BooleanProperty bannedProperty;

  public FixedPaneViewModel(UserModel model, ViewModelState state)
  {
    this.state = state;
    this.model = model;
    model.addListener("Notification", this);
    model.addListener("Ban", this);
    model.addListener("Reset", this);
    emailProperty = new SimpleStringProperty();
    titleOf_myAuctions_allAuctionsButton = new SimpleStringProperty();
    notificationsButtonBackgroundProperty = new SimpleStringProperty();
    notificationsButtonBackgroundProperty.set("");

    buttonsDisabled = new SimpleBooleanProperty();

    sellItemButtonVisibility = new SimpleBooleanProperty();
    notificationsButtonVisibility = new SimpleBooleanProperty();
    myBidsButtonVisibility = new SimpleBooleanProperty();
    bannedProperty = new SimpleBooleanProperty(false);
  }

  public void reset()
  {
    emailProperty.set(state.getUserEmail());
    bannedProperty.set(false);
    if (state.isModerator())
      setAppearanceForModerator();
    else
      setAppearanceForUser();
  }

  private void setAppearanceForModerator()
  {
    titleOf_myAuctions_allAuctionsButton.set("All Accounts");
    myBidsButtonVisibility.set(false);
    sellItemButtonVisibility.set(false);
    notificationsButtonVisibility.set(false);
  }

  private void setAppearanceForUser()
  {
    titleOf_myAuctions_allAuctionsButton.set("My auctions");
    myBidsButtonVisibility.set(true);
    sellItemButtonVisibility.set(true);
    notificationsButtonVisibility.set(true);
  }

  public void sellItem()
  {
    buttonsDisabled.set(true);
  }

  public void setForDisplayProfile()
  {
    buttonsDisabled.set(false);
    state.setDisplay();
  }

  public void setForResetPassword()
  {
    buttonsDisabled.set(true);
    state.setResetPassword();
  }

  public void leaveAuctionView()
  {
    buttonsDisabled.set(false);
  }

  public void allAuctions()
  {
    state.setAllAuctions();
    try
    {
      if (model.isModerator(state.getUserEmail()))
      {
        state.setModerator(true);
      }
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
  }

  public void myBids()
  {
    state.setBids();
  }

  public void myCreatedAuctions()
  {
    state.setCreatedAuctions();
  }

  public StringProperty getEmailProperty()
  {
    return emailProperty;
  }

  public StringProperty getNotificationsButtonBackgroundProperty()
  {
    return notificationsButtonBackgroundProperty;
  }

  public StringProperty getTitleOf_myAuctions_allAuctionsButton()
  {
    return titleOf_myAuctions_allAuctionsButton;
  }

  public BooleanProperty getButtonsDisabled()
  {
    return buttonsDisabled;
  }

  public BooleanProperty getMyBidsButtonVisibility()
  {
    return myBidsButtonVisibility;
  }

  public BooleanProperty getSellItemButtonVisibility()
  {
    return sellItemButtonVisibility;
  }

  public BooleanProperty getNotificationsButtonVisibility()
  {
    return notificationsButtonVisibility;
  }

  public boolean isModerator()
  {
    return state.isModerator();
  }

  public void setForEditProfile()
  {
    buttonsDisabled.set(true);
    state.setEdit();
  }

  public BooleanProperty getBannedProperty()
  {
    return bannedProperty;
  }

  public void setModeratorInfo()
  {
    state.setLookingAtModerator(true);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {

    switch (evt.getPropertyName())
    {
      case "Notification" ->
      {
        if (emailProperty.get()
            .equals(((Notification) evt.getNewValue()).getReceiver()))
        {
          notificationsButtonBackgroundProperty.set(
              "-fx-background-color: #EEEEEE; ");
        }
      }
      case "Ban", "Reset" ->
      {
        if (emailProperty.get().equals(evt.getNewValue()))
          bannedProperty.set(true);
      }
    }
  }
}
