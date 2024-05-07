package viewmodel;

import model.Auction;

public class ViewModelState
{
  private Auction selectedAuction;

  public ViewModelState()
  {
    selectedAuction = null;
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
}
