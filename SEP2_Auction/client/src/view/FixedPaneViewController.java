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

public class FixedPaneViewController
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
  private Region root;

  //we have access to the ViewModelFactory because this controller is kind of ViewHandler for its embedded views
  //we pass the id to pass it to the right controller;
  //that controller will set the scene for displaying or starting an auction, depending on the id
  private ViewModelFactory viewModelFactory;

  public void init(ViewHandler viewHandler,
      FixedPaneViewModel fixedPaneViewModel, ViewModelFactory viewModelFactory,
      Region root, String id)
  {
    this.root = root;
    this.viewModelFactory = viewModelFactory;
    this.fixedPaneViewModel = fixedPaneViewModel;
    this.viewHandler = viewHandler;
    emailLabel.textProperty()
        .bindBidirectional(fixedPaneViewModel.getEmailProperty());
    reset(id);
  }

  public Region getRoot()
  {
    return root;
  }

  public void reset(String id)
  {

    fixedPaneViewModel.reset();

    ///////////////////////////
    //sprint 1 focus
    switch (id)
    {
      case "startAuction" -> sellItemButtonPressed();
      case "displayAuction" ->
      {
        try
        {
          FXMLLoader loader = new FXMLLoader(
              getClass().getResource("AuctionView.fxml"));
          Region root = loader.load();
          borderPane.setCenter(root);
          auctionViewController = loader.getController();
          auctionViewController.init(viewHandler,
              viewModelFactory.getAuctionViewModel(), root, id);

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
      case "allAuctions" -> allAuctionsButtonPressed();
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
            viewModelFactory.getAuctionViewModel(), root, "startAuction");
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      borderPane.setCenter(auctionViewController.getRoot());
      auctionViewController.reset("startAuction");

    }
    return auctionViewController.getRoot();
  }

  @FXML Region allAuctionsButtonPressed()
  {
    return loadGrid("allAuctions");
  }

  private Region loadGrid(String id)
  {
    if(auctionViewController!=null)
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

        allAuctionsViewController.init(viewHandler,
            viewModelFactory, root, id);

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

    }
    else
    {
      borderPane.setCenter(allAuctionsViewController.getRoot());
      allAuctionsViewController.reset(id);
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

  @FXML void notificationsButtonPressed(ActionEvent event)
  {

  }

  public void myBidsButtonPressed(ActionEvent actionEvent)
  {
    //return loadGrid("myBids");
  }

}
