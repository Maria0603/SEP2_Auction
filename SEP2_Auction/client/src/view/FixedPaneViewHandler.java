
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
  private Region root;

  //we have access to the ViewModelFactory because this controller is kind of ViewHandler for its embedded views
  //we pass the id to pass it to the right controller;
  //that controller will set the scene for displaying or starting an auction, depending on the id
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

    reset(windowType);
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(WindowType windowType)
  {

    fixedPaneViewModel.reset();

    switch (windowType)
    {
      case START_AUCTION -> sellItemButtonPressed();
      case DISPLAY_AUCTION ->
      {
        try
        {
          FXMLLoader loader = new FXMLLoader(
              getClass().getResource("AuctionView.fxml"));
          Region root = loader.load();
          borderPane.setCenter(root);
          auctionViewController = loader.getController();
          auctionViewController.init(viewHandler,
              viewModelFactory.getAuctionViewModel(), root, windowType);

          allAuctionsButton.setDisable(false);
          myAuctions_allAccountsButton.setDisable(false);
          myBidsButton.setDisable(false);
          myProfile_settingsButton.setDisable(false);
          notificationsButton.setDisable(false);
          logOutButton.setDisable(false);
          moderatorInfoButton.setDisable(false);
          sellItemButton.setDisable(false);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      case ALL_AUCTIONS -> allAuctionsButtonPressed();
      case NOTIFICATIONS -> notificationsButtonPressed();
    }

  }

  @FXML Region sellItemButtonPressed()
  {
    //to prevent leaving the auction creation, we disable the buttons;
    //the user can still leave by pressing the Cancel or Back button
    allAuctionsButton.setDisable(true);
    myAuctions_allAccountsButton.setDisable(true);
    myBidsButton.setDisable(true);
    myProfile_settingsButton.setDisable(true);
    notificationsButton.setDisable(true);
    logOutButton.setDisable(true);
    moderatorInfoButton.setDisable(true);
    sellItemButton.setDisable(true);

    //the logic we would have in the ViewHandler - kind of
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
            viewModelFactory.getAuctionViewModel(), root, WindowType.START_AUCTION);
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
    return loadGrid(WindowType.START_AUCTION);
  }

  private Region loadGrid(WindowType windowType)
  {
    if (auctionViewController != null)
      auctionViewController.leaveAuctionView();
    allAuctionsButton.setDisable(false);
    myAuctions_allAccountsButton.setDisable(false);
    myBidsButton.setDisable(false);
    myProfile_settingsButton.setDisable(false);
    notificationsButton.setDisable(false);
    logOutButton.setDisable(false);
    moderatorInfoButton.setDisable(false);
    sellItemButton.setDisable(false);

    //the logic we would have in the ViewHandler - kind of
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
    allAuctionsViewController.loadOngoingAuctions();
    return allAuctionsViewController.getRoot();
  }

  @FXML void logOutButtonPressed(ActionEvent event)
  {

  }

  @FXML void moderatorInfoButtonPressed(ActionEvent event)
  {

  }

  @FXML void myAuctions_allAccountsButtonPressed(ActionEvent event)
  {

  }

  @FXML void myProfile_settingsButtonPressed(ActionEvent event)
  {

  }

  @FXML Region notificationsButtonPressed()
  {
    if (allAccountsNotificationsViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AllAccounts_NotificationsView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        allAccountsNotificationsViewController = loader.getController();

        allAccountsNotificationsViewController.init(viewHandler, viewModelFactory.getAllAccountsNotificationsViewModel(), root, WindowType.NOTIFICATIONS);

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

  public void myBidsButtonPressed(ActionEvent actionEvent)
  {
    //return loadGrid("myBids");
  }

}





