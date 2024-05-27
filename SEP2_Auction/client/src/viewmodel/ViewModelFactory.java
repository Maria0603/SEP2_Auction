package viewmodel;

import model.*;

public class ViewModelFactory
{
  private final AuctionViewModel auctionViewModel;
  private final FixedPaneViewModel fixedPaneViewModel;
  private final AllAuctionsViewModel allAuctionsViewModel;

  private final AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel;

  private final CreateLoginViewModel createLoginViewModel;

  private final ViewModelState viewModelState;
  private final AuctionModel auctionModel;

  public ViewModelFactory(AuctionModel auctionModel,
      AuctionListModel auctionListModel, UserModel userModel,
      UserListModel userListModel)
  {
    this.auctionModel = auctionModel;
    viewModelState = new ViewModelState();
    auctionViewModel = new AuctionViewModel(this.auctionModel, viewModelState);
    fixedPaneViewModel = new FixedPaneViewModel(userModel, viewModelState);
    allAuctionsViewModel = new AllAuctionsViewModel(auctionListModel,
        viewModelState);
    allAccountsNotificationsViewModel = new AllAccounts_NotificationsViewModel(
        userListModel, viewModelState);

    createLoginViewModel = new CreateLoginViewModel(userModel, viewModelState);

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
    return new AuctionCardViewModel(auctionModel, viewModelState);
  }

  public AllAccounts_NotificationsViewModel getAllAccountsNotificationsViewModel()
  {
    return allAccountsNotificationsViewModel;
  }

  public CreateLoginViewModel getCreateLoginViewModel()
  {
    return createLoginViewModel;
  }

}
