package viewmodel;

import model.Auction;

public class ViewModelState
{
  private Auction selectedAuction;
  private boolean displayAuction;
  //private Account account;
  public ViewModelState()
  {
    selectedAuction=null;
    displayAuction=false;
    //account=null;
  }
  public void setAuction(Auction auction)
  {
    this.selectedAuction=auction;
  }
  public Auction getSelectedAuction()
  {
    return selectedAuction;
  }
  public boolean getDisplayAuction()
  {
    return displayAuction;
  }
  public void setDisplayAuction(boolean display)
  {
    this.displayAuction=display;
  }
}
