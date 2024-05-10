package view;

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

public class FixedPaneViewHandler
{
  @FXML private Button allAuctionsButton;
  @FXML private BorderPane borderPane;
  @FXML private Pane changePane;
  @FXML private Button logOutButton;
  @FXML private Button moderatorInfoButton;
  @FXML private Button myAuctions_allAccountsButton;
  @FXML private Button myBidsButton;
  @FXML private Button myProfile_settingsButton;
  @FXML private Label emailLabel;
  @FXML private Button notificationsButton;
  @FXML private Button sellItemButton;
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
      Region root, WindowType windowType)
  {
    this.root = root;
    this.viewModelFactory = viewModelFactory;
    this.fixedPaneViewModel = fixedPaneViewModel;
    this.viewHandler = viewHandler;
    emailLabel.textProperty()
        .bindBidirectional(fixedPaneViewModel.getEmailProperty());
    notificationsButton.styleProperty().bindBidirectional(
        fixedPaneViewModel.getNotificationsButtonBackgroundProperty());
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
    reset(windowType);
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(WindowType windowType)
  {
    fixedPaneViewModel.reset();
    notificationsButton.setStyle("");
    switch (windowType)
    {
      case START_AUCTION -> sellItemButtonPressed();
      case DISPLAY_AUCTION -> displayAuction();
      case ALL_AUCTIONS -> allAuctionsButtonPressed();
      case NOTIFICATIONS -> notificationsButtonPressed();
      case BIDS -> myBidsButtonPressed();
      case DISPLAY_PROFILE -> myProfile_settingsButtonPressed();
    }

  }

  @FXML Region sellItemButtonPressed()
  {
    fixedPaneViewModel.sellItem();

    if (auctionViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AuctionView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        auctionViewController = loader.getController();

        auctionViewController.init(viewHandler,
            viewModelFactory.getAuctionViewModel(), root,
            WindowType.START_AUCTION);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      borderPane.setCenter(auctionViewController.getRoot());
      auctionViewController.reset(WindowType.START_AUCTION);

    }
    return auctionViewController.getRoot();
  }

  @FXML Region allAuctionsButtonPressed()
  {
    fixedPaneViewModel.allAuctions();
    return loadGrid(WindowType.ALL_AUCTIONS);
  }

  private void displayAuction()
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("AuctionView.fxml"));
      Region root = loader.load();
      borderPane.setCenter(root);
      auctionViewController = loader.getController();
      auctionViewController.init(viewHandler,
          viewModelFactory.getAuctionViewModel(), root,
          WindowType.DISPLAY_AUCTION);

      fixedPaneViewModel.leaveAuctionView();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void leaveAuction()
  {
    if (auctionViewController != null)
      auctionViewController.leaveAuctionView();
  }

  private Region loadGrid(WindowType windowType)
  {
    leaveAuction();
    fixedPaneViewModel.leaveAuctionView();

    if (allAuctionsViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AllAuctions_MyAuctions_MyBidsView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        allAuctionsViewController = loader.getController();

        allAuctionsViewController.init(viewHandler, viewModelFactory, root,
            windowType);

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

    }
    else
    {
      borderPane.setCenter(allAuctionsViewController.getRoot());
      allAuctionsViewController.reset(windowType);
    }
    //allAuctionsViewController.loadAuctions();
    return allAuctionsViewController.getRoot();
  }

  @FXML void logOutButtonPressed(ActionEvent event)
  {
    leaveAuction();
    viewHandler.openView(WindowType.LOG_IN);
  }

  @FXML void moderatorInfoButtonPressed(ActionEvent event)
  {
    leaveAuction();

  }

  @FXML void myAuctions_allAccountsButtonPressed(ActionEvent event)
  {
    fixedPaneViewModel.myCreatedAuctions();
  }

  @FXML Region myProfile_settingsButtonPressed()
  {
    leaveAuction();
    fixedPaneViewModel.setForDisplayProfile();

    if (createLoginViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CreateAccountEditProfileView.fxml"));
        Region root = loader.load();
        createLoginViewController = loader.getController();

        createLoginViewController.init(viewHandler,
            viewModelFactory.getCreateLoginViewModel(), root, WindowType.DISPLAY_PROFILE);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      createLoginViewController.reset(WindowType.DISPLAY_PROFILE);
    }
    borderPane.setCenter(createLoginViewController.getRoot());
    return createLoginViewController.getRoot();
  }

  @FXML Region notificationsButtonPressed()
  {
    leaveAuction();

    //notificationsButton.setStyle("-fx-background-color: #ffffff;");
    notificationsButton.setStyle("");
    if (allAccountsNotificationsViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AllAccounts_NotificationsView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        allAccountsNotificationsViewController = loader.getController();

        allAccountsNotificationsViewController.init(viewHandler,
            viewModelFactory.getAllAccountsNotificationsViewModel(), root,
            WindowType.NOTIFICATIONS);

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

    }
    else
    {
      borderPane.setCenter(allAccountsNotificationsViewController.getRoot());
      allAccountsNotificationsViewController.reset(WindowType.NOTIFICATIONS);
    }
    return allAuctionsViewController.getRoot();
  }

  @FXML public Region myBidsButtonPressed()
  {
    fixedPaneViewModel.myBids();
    return loadGrid(WindowType.BIDS);
  }

}





