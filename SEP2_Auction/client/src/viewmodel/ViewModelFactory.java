package viewmodel;

import model.AuctionModel;

public class ViewModelFactory
{
  private AuctionViewModel auctionViewModel;
  private FixedPaneViewModel fixedPaneViewModel;
  private AllAuctionsViewModel allAuctionsViewModel;
  private AuctionCardViewModel auctionCardViewModel;
  private ViewModelState viewModelState;

  public ViewModelFactory(AuctionModel model)
  {
    viewModelState = new ViewModelState();
    auctionViewModel = new AuctionViewModel(model, viewModelState);
    fixedPaneViewModel = new FixedPaneViewModel(model, viewModelState);
    allAuctionsViewModel=new AllAuctionsViewModel(model, viewModelState);
    auctionCardViewModel=new AuctionCardViewModel(model, viewModelState);
  }

  public AuctionViewModel getAuctionViewModel()
  {
    return auctionViewModel;
  }

  public FixedPaneViewModel getFixedPaneViewModel()
  {
    return fixedPaneViewModel;
  }

  public AllAuctionsViewModel getAllAuctionsViewModel()
  {
    return allAuctionsViewModel;
  }
  public AuctionCardViewModel getAuctionCardViewModel()
  {
    return auctionCardViewModel;
  }
}
