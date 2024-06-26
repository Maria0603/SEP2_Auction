package viewmodel;

import model.*;

/**
 * Factory class for creating various view model instances.
 */
public class ViewModelFactory {
  private final AuctionViewModel auctionViewModel;
  private final FixedPaneViewModel fixedPaneViewModel;
  private final AllAuctionsViewModel allAuctionsViewModel;
  private final AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel;
  private final CreateLoginViewModel createLoginViewModel;
  private final ViewModelState viewModelState;
  private final AuctionModel auctionModel;

  /**
   * Constructs a ViewModelFactory with the given models.
   * @param auctionModel the auction model
   * @param auctionListModel the auction list model
   * @param userModel the user model
   * @param userListModel the user list model
   */
  public ViewModelFactory(AuctionModel auctionModel, AuctionListModel auctionListModel, UserModel userModel, UserListModel userListModel) {
    this.auctionModel = auctionModel;
    viewModelState = new ViewModelState();
    auctionViewModel = new AuctionViewModel(this.auctionModel, viewModelState);
    fixedPaneViewModel = new FixedPaneViewModel(userModel, viewModelState);
    allAuctionsViewModel = new AllAuctionsViewModel(auctionListModel, viewModelState);
    allAccountsNotificationsViewModel = new AllAccounts_NotificationsViewModel(userListModel, viewModelState);
    createLoginViewModel = new CreateLoginViewModel(userModel, viewModelState);
  }

  /**
   * Gets the AuctionViewModel instance.
   * @return the AuctionViewModel
   */
  public AuctionViewModel getAuctionViewModel() {
    return auctionViewModel;
  }

  /**
   * Gets the FixedPaneViewModel instance.
   * @return the FixedPaneViewModel
   */
  public FixedPaneViewModel getFixedPaneViewModel() {
    return fixedPaneViewModel;
  }

  /**
   * Gets the AllAuctionsViewModel instance.
   * @return the AllAuctionsViewModel
   */
  public AllAuctionsViewModel getAllAuctionsViewModel() {
    return allAuctionsViewModel;
  }

  /**
   * Gets the AuctionCardViewModel instance.
   * @return the AuctionCardViewModel
   */
  public AuctionCardViewModel getAuctionCardViewModel() {
    return new AuctionCardViewModel(auctionModel, viewModelState);
  }

  /**
   * Gets the AllAccounts_NotificationsViewModel instance.
   * @return the AllAccounts_NotificationsViewModel
   */
  public AllAccounts_NotificationsViewModel getAllAccountsNotificationsViewModel() {
    return allAccountsNotificationsViewModel;
  }

  /**
   * Gets the CreateLoginViewModel instance.
   * @return the CreateLoginViewModel
   */
  public CreateLoginViewModel getCreateLoginViewModel() {
    return createLoginViewModel;
  }
}
