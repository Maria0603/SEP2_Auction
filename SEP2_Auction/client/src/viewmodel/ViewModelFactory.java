package viewmodel;

import model.AuctionModel;

public class ViewModelFactory
{
  private AuctionViewModel auctionViewModel;
  private FixedPaneViewModel fixedPaneViewModel;
  private AllAuctionsViewModel allAuctionsViewModel;
  private CreateLoginViewModel createLoginViewModel;
  private ViewModelState viewModelState;
  private AuctionModel model;

  public ViewModelFactory(AuctionModel model)
  {
    this.model = model;
    viewModelState = new ViewModelState();
    auctionViewModel = new AuctionViewModel(this.model, viewModelState);
    fixedPaneViewModel = new FixedPaneViewModel(this.model, viewModelState);
    allAuctionsViewModel = new AllAuctionsViewModel(this.model, viewModelState);
    createLoginViewModel = new CreateLoginViewModel(this.model);
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
    return new AuctionCardViewModel(model, viewModelState);
  }
  public CreateLoginViewModel getCreateLoginViewModel() {return createLoginViewModel;}
}
