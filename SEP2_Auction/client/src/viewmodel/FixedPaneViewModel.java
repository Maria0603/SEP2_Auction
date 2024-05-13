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

public class FixedPaneViewModel implements PropertyChangeListener
{
  private StringProperty emailProperty, myProfile_settingsProperty, myAuctions_allAccountsProperty, myBids_bannedUsers, notificationsButtonBackgroundProperty;
  private AuctionModel model;
  private ViewModelState state;

  //all button in the fixed pane
  private BooleanProperty buttonsDisabled;
  //notification and sell item buttons, for moderator
  private BooleanProperty displayButtons;

  public FixedPaneViewModel(AuctionModel model, ViewModelState state)
  {
    this.state = state;
    this.model = model;
    model.addListener("Notification", this);
    emailProperty = new SimpleStringProperty();
    notificationsButtonBackgroundProperty=new SimpleStringProperty();
    notificationsButtonBackgroundProperty.set("");
    buttonsDisabled=new SimpleBooleanProperty();
    displayButtons =new SimpleBooleanProperty();
  }
  public BooleanProperty getDisplayButtons()
  {
    return displayButtons;
  }


  public void reset()
  {
    //  Just setting the email
    emailProperty.set(state.getUserEmail());
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
  public void setForEditProfile()
  {
    buttonsDisabled.set(true);
    state.setEdit();
  }
  public void leaveAuctionView()
  {
      buttonsDisabled.set(false);
      if(state.isModerator())
        displayButtons.set(false);
      else displayButtons.set(true);
  }
  public void allAuctions()
  {
    state.setAllAuctions();
    try
    {
      if(model.isModerator(state.getUserEmail()))
      {
        state.setModerator(true);
        //buttonsDisabled.set(true);
      }
    }
    catch (SQLException e)
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
  public BooleanProperty getButtonsDisabled()
  {
    return buttonsDisabled;
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    if(emailProperty.get().equals(((Notification)evt.getNewValue()).getReceiver()))
      notificationsButtonBackgroundProperty.set("-fx-background-color: #ff0000; ");
  }
}
