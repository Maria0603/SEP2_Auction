package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import viewmodel.AuctionViewModel;
import viewmodel.FixedPaneViewModel;
import viewmodel.ViewModelFactory;

import javax.swing.text.View;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FixedPaneViewController //implements Initializable
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
  private String id;
  private Region root;
  private ViewModelFactory viewModelFactory;
  public void init(ViewHandler viewHandler, FixedPaneViewModel fixedPaneViewModel, ViewModelFactory viewModelFactory, Region root, String id)
  {
    this.id=id;
    this.root=root;
    this.viewModelFactory=viewModelFactory;
    this.fixedPaneViewModel=fixedPaneViewModel;
    this.viewHandler=viewHandler;

    emailLabel.textProperty().bindBidirectional(fixedPaneViewModel.getEmailProperty());
    reset();

    ///////////////////////////
    sellItemButtonPressed();
    //////////////////////////
  }
  public Region getRoot()
  {
    return root;
  }
  public void reset()
  {
    fixedPaneViewModel.reset();
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

  @FXML Region sellItemButtonPressed()
  {
    /*try
    {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("AuctionView.fxml"));
      Parent newContent = loader.load();
      borderPane.setCenter(newContent);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
*/


    if (auctionViewController == null)
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
    else
    {
      auctionViewController.reset();
    }
    return auctionViewController.getRoot();
  }

  public void myBidsButtonPressed(ActionEvent actionEvent)
  {
  }

}
