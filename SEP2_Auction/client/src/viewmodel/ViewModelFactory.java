package viewmodel;

import model.*;

public class ViewModelFactory
{
  private AuctionViewModel auctionViewModel;
  private FixedPaneViewModel fixedPaneViewModel;
  private AllAuctionsViewModel allAuctionsViewModel;

  private AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel;

  private CreateLoginViewModel createLoginViewModel;

  private ViewModelState viewModelState;
  private AuctionModel auctionModel;
  private AuctionListModel auctionListModel;
  private UserModel userModel;
  private UserListModel userListModel;

  public ViewModelFactory(AuctionModel auctionModel, AuctionListModel auctionListModel, UserModel userModel, UserListModel userListModel)
  {
    this.auctionModel=auctionModel;
    this.auctionListModel=auctionListModel;
    this.userModel=userModel;
    this.userListModel=userListModel;
    viewModelState = new ViewModelState();
    auctionViewModel = new AuctionViewModel(this.auctionModel, viewModelState);
    fixedPaneViewModel = new FixedPaneViewModel(this.userModel, viewModelState);
    allAuctionsViewModel = new AllAuctionsViewModel(this.auctionListModel, viewModelState);
    allAccountsNotificationsViewModel = new AllAccounts_NotificationsViewModel(
        this.userListModel, viewModelState);

    createLoginViewModel = new CreateLoginViewModel(this.userModel, viewModelState);

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
