package viewmodel;

import javafx.beans.InvalidationListener;
import model.Auction;
import model.User;
import view.WindowType;

import java.util.Observable;

public class ViewModelState
{
  private Auction selectedAuction;
  private String userEmail;
  private boolean bids, createdAuctions, allAuctions;

  public ViewModelState()
  {
    selectedAuction = null;
    userEmail=null;
    setAllAuctions();
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
  public void setBids()
  {
    this.bids=true;
    this.allAuctions=false;
    this.createdAuctions=false;
  }

  public void setCreatedAuctions()
  {
    this.bids=false;
    this.allAuctions=false;
    this.createdAuctions=true;
  }


  public void setAllAuctions()
  {
    this.bids=false;
    this.allAuctions=true;
    this.createdAuctions=false;
  }

  public boolean getBids()
  {
    return bids;
  }

  public boolean getCreatedAuctions()
  {
    return createdAuctions;
  }

  public boolean getAllAuctions()
  {
    return allAuctions;
  }


}
