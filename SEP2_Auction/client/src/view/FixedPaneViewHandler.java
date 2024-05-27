package view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import viewmodel.FixedPaneViewModel;
import viewmodel.ViewModelFactory;

public class FixedPaneViewHandler {
  @FXML private Button allAuctionsButton;
  @FXML private BorderPane borderPane;
  @FXML private Pane changePane;
  @FXML private Button logOutButton;
  @FXML private Button moderatorInfoButton;
  @FXML private Button myBidsButton;
  @FXML private Button myProfile_settingsButton;
  @FXML private Label emailLabel;
  @FXML private Button notificationsButton;
  @FXML private Button sellItemButton;

  @FXML private Button myAuctions_allAccountsButton;

  private ViewHandler viewHandler;
  private FixedPaneViewModel fixedPaneViewModel;
  private AuctionViewController auctionViewController;
  private AllAuctionsViewController allAuctionsViewController;
  private AllAccounts_NotificationsViewController allAccountsNotificationsViewController;
  private CreateLoginViewController createLoginViewController;
  private Region root;
  private ViewModelFactory viewModelFactory;

  public void init(ViewHandler viewHandler,
      FixedPaneViewModel fixedPaneViewModel, ViewModelFactory viewModelFactory,
      Region root, WindowType windowType) {
    this.root = root;
    this.viewModelFactory = viewModelFactory;
    this.fixedPaneViewModel = fixedPaneViewModel;
    this.viewHandler = viewHandler;

    emailLabel.textProperty()
        .bindBidirectional(fixedPaneViewModel.getEmailProperty());

    fixedPaneViewModel.getBannedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!oldValue && newValue) {
          Platform.runLater(() -> logOutButtonPressed());
        }
      }
    });
    notificationsButton.styleProperty().bindBidirectional(
        fixedPaneViewModel.getNotificationsButtonBackgroundProperty());
    notificationsButton.setStyle("");

    setModeratorAppearanceRelatedBindings();
    bindDisableProperty();

    reset(windowType);
  }

  public Region getRoot() {
    return root;
  }

  public void reset(WindowType windowType) {
    fixedPaneViewModel.reset();
    switch (windowType) {
      case START_AUCTION -> sellItemButtonPressed();
      case DISPLAY_AUCTION -> displayAuction();
      case NOTIFICATIONS -> notificationsButtonPressed();
      case ALL_AUCTIONS -> allAuctionsButtonPressed();
      case MY_BIDS -> myBidsButtonPressed();
      case MY_AUCTIONS, ALL_ACCOUNTS -> myAuctions_allAccountsButtonPressed();
      case DISPLAY_PROFILE -> myProfile_settingsButtonPressed();
      case EDIT_PROFILE -> editProfile();
      case RESET_PASSWORD -> resetPassword();
    }
  }

  private Region loadAuctionView(WindowType windowType) {
    if (auctionViewController == null) {
      try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AuctionView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        auctionViewController = loader.getController();

        auctionViewController.init(viewHandler,
            viewModelFactory.getAuctionViewModel(), root, windowType);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      borderPane.setCenter(auctionViewController.getRoot());
      auctionViewController.reset(windowType);

    }
    return auctionViewController.getRoot();
  }

  private void displayAuction() {
    fixedPaneViewModel.leaveAuctionView();
    loadAuctionView(WindowType.DISPLAY_AUCTION);
  }

  private void editProfile() {
    leaveAuction();
    fixedPaneViewModel.setForEditProfile();
    loadProfile(WindowType.EDIT_PROFILE);
  }

  private void resetPassword() {
    leaveAuction();
    fixedPaneViewModel.setForResetPassword();
    loadProfile(WindowType.RESET_PASSWORD);
  }

  private void leaveAuction() {
    if (auctionViewController != null)
      auctionViewController.leaveAuctionView();
  }

  private Region loadGrid(WindowType windowType) {
    leaveAuction();
    fixedPaneViewModel.leaveAuctionView();

    if (allAuctionsViewController == null) {
      try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AllAuctions_MyAuctions_MyBidsView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        allAuctionsViewController = loader.getController();

        allAuctionsViewController.init(viewHandler, viewModelFactory, root,
            windowType);

      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }
    else {
      borderPane.setCenter(allAuctionsViewController.getRoot());
      allAuctionsViewController.reset(windowType);
    }
    return allAuctionsViewController.getRoot();
  }

  @FXML private Region allAuctionsButtonPressed() {
    fixedPaneViewModel.allAuctions();
    return loadGrid(WindowType.ALL_AUCTIONS);
  }

  @FXML Region sellItemButtonPressed() {
    fixedPaneViewModel.sellItem();
    return loadAuctionView(WindowType.START_AUCTION);
  }

  @FXML private void logOutButtonPressed() {
    leaveAuction();
    viewHandler.openView(WindowType.LOG_IN);
  }

  @FXML private void moderatorInfoButtonPressed(ActionEvent event) {
    leaveAuction();
    fixedPaneViewModel.setModeratorInfo();
    reset(WindowType.DISPLAY_PROFILE);
  }

  @FXML private Region myAuctions_allAccountsButtonPressed() {
    if(fixedPaneViewModel.isModerator()){
      return loadAllAccounts();
    }

    fixedPaneViewModel.myCreatedAuctions();
    return loadGrid(WindowType.MY_AUCTIONS);
  }

  private Region loadAllAccounts(){
    leaveAuction();
    notificationsButton.setStyle("");
    if (allAccountsNotificationsViewController == null) {
      try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AllAccounts_NotificationsView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        allAccountsNotificationsViewController = loader.getController();

        allAccountsNotificationsViewController.init(viewHandler,
            viewModelFactory.getAllAccountsNotificationsViewModel(), root,
            WindowType.ALL_ACCOUNTS);

      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }
    else {
      borderPane.setCenter(allAccountsNotificationsViewController.getRoot());
      allAccountsNotificationsViewController.reset(WindowType.ALL_ACCOUNTS);
    }
    return allAuctionsViewController.getRoot();
  }
  @FXML private Region myProfile_settingsButtonPressed() {
    leaveAuction();
    fixedPaneViewModel.setForDisplayProfile();
    return loadProfile(WindowType.DISPLAY_PROFILE);
  }

  private Region loadProfile(WindowType windowType) {
    leaveAuction();
    //fixedPaneViewModel.setForDisplayProfile();

    if (createLoginViewController == null) {
      try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(
            getClass().getResource("CreateAccountEditProfileView.fxml"));
        Region root = loader.load();
        createLoginViewController = loader.getController();

        createLoginViewController.init(viewHandler,
            viewModelFactory.getCreateLoginViewModel(), root, windowType);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      createLoginViewController.reset(windowType);
    }
    borderPane.setCenter(createLoginViewController.getRoot());
    return createLoginViewController.getRoot();
  }



  @FXML Region notificationsButtonPressed() {
    leaveAuction();
    notificationsButton.setStyle("");
    if (allAccountsNotificationsViewController == null) {
      try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AllAccounts_NotificationsView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        allAccountsNotificationsViewController = loader.getController();

        allAccountsNotificationsViewController.init(viewHandler,
            viewModelFactory.getAllAccountsNotificationsViewModel(), root,
            WindowType.NOTIFICATIONS);

      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }
    else {
      borderPane.setCenter(allAccountsNotificationsViewController.getRoot());
      allAccountsNotificationsViewController.reset(WindowType.NOTIFICATIONS);
    }
    return allAuctionsViewController.getRoot();
  }

  @FXML public Region myBidsButtonPressed() {
    fixedPaneViewModel.myBids();
    return loadGrid(WindowType.MY_BIDS);
  }

  private void bindDisableProperty() {
    allAuctionsButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
    myAuctions_allAccountsButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
    myBidsButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
    myProfile_settingsButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
    notificationsButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
    logOutButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
    moderatorInfoButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
    sellItemButton.disableProperty()
        .bindBidirectional(fixedPaneViewModel.getButtonsDisabled());
  }

  private void setModeratorAppearanceRelatedBindings() {
    notificationsButton.visibleProperty().bindBidirectional(
        fixedPaneViewModel.getNotificationsButtonVisibility());
    sellItemButton.visibleProperty()
        .bindBidirectional(fixedPaneViewModel.getSellItemButtonVisibility());
    myBidsButton.visibleProperty()
        .bind(fixedPaneViewModel.getMyBidsButtonVisibility());

    myAuctions_allAccountsButton.textProperty().bindBidirectional(
        fixedPaneViewModel.getTitleOf_myAuctions_allAuctionsButton());

  }


}





