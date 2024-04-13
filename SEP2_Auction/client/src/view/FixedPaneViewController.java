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
  @FXML private Button myBids_bannedUsersButton;
  @FXML private Button myProfile_settingsButton;
  @FXML private Label emailLabel;
  @FXML private Button notificationsButton;
  @FXML private Button sellItemButton;
  private ViewHandler viewHandler;
  private FixedPaneViewModel fixedPaneViewModel;
  private AuctionViewController auctionViewController;
  private Region root;

  //we have access to the ViewModelFactory because this controller is kind of ViewHandler for its embedded views
  //we pass the id to pass it to the right controller; that controller will set the scene for displaying or starting an auction, depending on the id
  private ViewModelFactory viewModelFactory;
  public void init(ViewHandler viewHandler, FixedPaneViewModel fixedPaneViewModel, ViewModelFactory viewModelFactory, Region root, String id)
  {
    this.root=root;
    this.viewModelFactory=viewModelFactory;
    this.fixedPaneViewModel=fixedPaneViewModel;
    this.viewHandler=viewHandler;
    emailLabel.textProperty().bindBidirectional(fixedPaneViewModel.getEmailProperty());
    reset(id);
  }
  public Region getRoot()
  {
    return root;
  }
  public void reset(String id)
  {
    fixedPaneViewModel.reset();
    sellItemButton.setText("Sell item");

    ///////////////////////////
    //sprint 1 focus
    if(id.equals("startAuction"))
    {
      sellItemButtonPressed();
    }
    else if(id.equals("displayAuction"))
    {
      try
      {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AuctionView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        auctionViewController = loader.getController();
        auctionViewController.init(viewHandler, viewModelFactory.getAuctionViewModel(), root, id);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

  }

  @FXML Region sellItemButtonPressed()
  {
    sellItemButton.setText("Clear");
    //the logic we would have in the ViewHandler - kind of
    if (auctionViewController == null)
    {
      try
      {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AuctionView.fxml"));
        Region root = loader.load();
        borderPane.setCenter(root);
        auctionViewController = loader.getController();
        auctionViewController.init(viewHandler, viewModelFactory.getAuctionViewModel(), root, "startAuction");
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      auctionViewController.reset("startAuction");
    }
    return auctionViewController.getRoot();
  }
  @FXML void allAuctionsButtonPressed(ActionEvent event)
  {
  }

  @FXML
  void logOutButtonPressed(ActionEvent event)
  {

  }

  @FXML
  void moderatorInfoButtonPressed(ActionEvent event)
  {

  }

  @FXML
  void myAuctions_allAccountsButtonPressed(ActionEvent event)
  {

  }

  @FXML
  void myProfile_settingsButtonPressed(ActionEvent event)
  {

  }

  @FXML
  void notificationsButtonPressed(ActionEvent event)
  {

  }


  public void myBidsButtonPressed(ActionEvent actionEvent)
  {
  }

}
