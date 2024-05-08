package viewmodel;

import model.Auction;

public class ViewModelState
{
  private Auction selectedAuction;
  private String email;

  public ViewModelState()
  {
    selectedAuction = null;
    email=null;
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
  public void setEmail(String email)
  {
    this.email=email;
  }
  public String getEmail()
  {
    return email;
  }

}
