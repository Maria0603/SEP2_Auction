package viewmodel;

import model.AuctionModel;

public class ViewModelFactory
{
  private AuctionViewModel auctionViewModel;
  private FixedPaneViewModel fixedPaneViewModel;
  private ViewModelState viewModelState;

  public ViewModelFactory(AuctionModel model)
  {
    viewModelState = new ViewModelState();
    auctionViewModel=new AuctionViewModel(model, viewModelState);
    fixedPaneViewModel=new FixedPaneViewModel(model, viewModelState);
  }
  public AuctionViewModel getAuctionViewModel()
  {
    return auctionViewModel;
  }

  public FixedPaneViewModel getFixedPaneViewModel()
  {
    return fixedPaneViewModel;
  }
}
