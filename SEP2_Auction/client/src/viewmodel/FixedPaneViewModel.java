package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import model.AuctionModel;

public class FixedPaneViewModel
{
  private StringProperty emailProperty, myProfile_settingsProperty, myAuctions_allAccountsProperty, myBids_bannedUsers;
  private AuctionModel model;
  private ViewModelState state;

  public FixedPaneViewModel(AuctionModel model, ViewModelState state)
  {
    this.state = state;
    this.model = model;
    emailProperty = new SimpleStringProperty();
  }

  public void reset()
  {
    //when we will have accounts:
    //emailProperty.set(state.getAccount().getEmail());
    emailProperty.set("bob_the_bidder@gmail.com");
  }

  public StringProperty getEmailProperty()
  {
    return emailProperty;
  }
}
