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
  //for windows
  private boolean bids, createdAuctions, allAuctions;
  private boolean create, login, resetPassword, edit, display;
  private boolean moderator;

  public ViewModelState()
  {
    selectedAuction = null;
    userEmail=null;
    moderator=false;
    setAllAuctions();
    setCreate();
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
  public void setModerator(boolean isModerator)
  {
    this.moderator=isModerator;
  }
  public boolean isModerator()
  {
    return moderator;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }
  private void setAllCreatedBids(boolean all, boolean created, boolean bids)
  {
    this.allAuctions=all;
    this.createdAuctions=created;
    this.bids=bids;
  }
  public void setBids()
  {
    setAllCreatedBids(false, false, true);
  }

  public void setCreatedAuctions()
  {
    setAllCreatedBids(false, true, false);
  }

  public void setAllAuctions()
  {
    setAllCreatedBids(true, false, false);
  }
  private void setCreateLoginResetEditDisplay(boolean create, boolean login, boolean reset, boolean edit, boolean display)
  {
    this.create=create;
    this.login=login;
    this.resetPassword=reset;
    this.edit=edit;
    this.display=display;
  }
  public void setCreate()
  {
    setCreateLoginResetEditDisplay(true, false, false, false, false);
  }
  public void setLogin()
  {
    setCreateLoginResetEditDisplay(false, true, false, false, false);
  }
  public void setResetPassword()
  {
    setCreateLoginResetEditDisplay(false, false, true, false, false);
  }
  public void setEdit()
  {
    setCreateLoginResetEditDisplay(false, false, false, true, false);
  }
  public void setDisplay()
  {
    setCreateLoginResetEditDisplay(false, false, false, false, true);
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
  public boolean isCreate()
  {
    return create;
  }

  public boolean isLogin()
  {
    return login;
  }

  public boolean isResetPassword()
  {
    return resetPassword;
  }

  public boolean isEdit()
  {
    return edit;
  }
  public boolean isDisplay()
  {
    return display;
  }


}
