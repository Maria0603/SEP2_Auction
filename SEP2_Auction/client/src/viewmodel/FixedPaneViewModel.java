package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import model.AuctionModel;
import model.Notification;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FixedPaneViewModel implements PropertyChangeListener
{
  private StringProperty emailProperty, myProfile_settingsProperty, myAuctions_allAccountsProperty, myBids_bannedUsers, notificationsButtonBackgroundProperty;
  private AuctionModel model;
  private ViewModelState state;

  public FixedPaneViewModel(AuctionModel model, ViewModelState state)
  {
    this.state = state;
    this.model = model;
    model.addListener("Notification", this);
    emailProperty = new SimpleStringProperty();
    notificationsButtonBackgroundProperty=new SimpleStringProperty();
    notificationsButtonBackgroundProperty.set("");
  }

  public void reset()
  {
    //  Just setting the email
    emailProperty.set(state.getUserEmail());
  }
  public void allAuctions()
  {
    state.setAllAuctions();
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

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    if(emailProperty.get().equals(((Notification)evt.getNewValue()).getReceiver()))
      notificationsButtonBackgroundProperty.set("-fx-background-color: #ff0000; ");
  }
}
