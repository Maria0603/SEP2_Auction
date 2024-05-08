package viewmodel;

import model.AuctionModel;

public class ViewModelFactory
{
  private AuctionViewModel auctionViewModel;
  private FixedPaneViewModel fixedPaneViewModel;
  private AllAuctionsViewModel allAuctionsViewModel;
  private AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel;
  private ViewModelState viewModelState;
  private AuctionModel model;

  public ViewModelFactory(AuctionModel model)
  {
    this.model = model;
    viewModelState = new ViewModelState();
    auctionViewModel = new AuctionViewModel(this.model, viewModelState);
    fixedPaneViewModel = new FixedPaneViewModel(this.model, viewModelState);
    allAuctionsViewModel = new AllAuctionsViewModel(this.model, viewModelState);
    allAccountsNotificationsViewModel=new AllAccounts_NotificationsViewModel(this.model, viewModelState);
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
  public AllAccounts_NotificationsViewModel getAllAccountsNotificationsViewModel()
  {
    return allAccountsNotificationsViewModel;
  }
}
