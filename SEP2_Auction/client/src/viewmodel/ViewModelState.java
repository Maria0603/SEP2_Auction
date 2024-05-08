package viewmodel;

import model.Auction;
import model.User;

public class ViewModelState
{
  private Auction selectedAuction;
  private User user;

  public ViewModelState()
  {
    selectedAuction = null;
    user=null;
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


  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
