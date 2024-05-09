package viewmodel;

import javafx.beans.InvalidationListener;
import model.Auction;
import model.User;

import java.util.Observable;

public class ViewModelState
{
  private Auction selectedAuction;
  private String userEmail;

  public ViewModelState()
  {
    selectedAuction = null;
    userEmail=null;
  }

  public void setAuction(Auction auction)
  {
    this.selectedAuction = auction;
  }

  public Auction getSelectedAuction()
  {
    return selectedAuction;
  }

  public void wipeAuction()
  {
    selectedAuction = null;
  }


  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

}
