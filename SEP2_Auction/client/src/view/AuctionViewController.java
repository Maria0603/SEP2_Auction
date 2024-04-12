package view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import utility.IntStringConverter;
import viewmodel.AuctionViewModel;

import java.util.Optional;

public class AuctionViewController
{
  @FXML private ScrollPane auctionScrollPane;
  @FXML private Button backButton;
  @FXML private Label bidLabel;
  @FXML private TextField bidTextField;
  @FXML private Button buyNowButton;
  @FXML private TextField buyoutPriceTextField;
  @FXML private Button cancelButton;
  @FXML private SplitMenuButton categorySplitMenuButton;
  @FXML private Label currentBidLabel;
  @FXML private Label currentBidTextLabel;
  @FXML private Label currentBidderLabel;
  @FXML private Label currentBidderTextLabel;
  @FXML private TextArea descriptionTextArea;
  @FXML private Label emailLabel;
  @FXML private Label emailTextLabel;
  @FXML private Label errorLabel;
  @FXML private Label headerLabel;
  @FXML private Label hoursLabel;
  @FXML private ImageView imageImageView;
  @FXML private Button importButton;
  @FXML private TextField incrementTextField;
  @FXML private Button placeBidButton;
  @FXML private Label ratingLabel;
  @FXML private Label ratingTextLabel;
  @FXML private TextArea reasonTextArea;
  @FXML private Button deleteButton;
  @FXML private TextField reservePriceTextField;
  @FXML private Label sellerRateLabel;
  @FXML private Label somethingWrongLabel;
  @FXML private Button startAuctionButton;
  @FXML private Label timeLabel;
  @FXML private TextField timeTextField;
  @FXML private Label timerCountdownLabel;
  @FXML private TextArea titleTextArea;
  @FXML private Label idLabel;
  @FXML private AnchorPane anchorPane;
  
  private Region root;
  private AuctionViewModel auctionViewModel;
  private ViewHandler viewHandler;

  //initializations and bindings
  public void init(ViewHandler viewHandler, Object auctionViewModel, Region root, String id)
  {
    this.root = root;
    this.viewHandler = viewHandler;
    this.auctionViewModel=(AuctionViewModel) auctionViewModel;

    Bindings.bindBidirectional(idLabel.textProperty(), this.auctionViewModel.getIdProperty(), new IntStringConverter());
    headerLabel.textProperty().bindBidirectional(this.auctionViewModel.getHeaderProperty());
    titleTextArea.textProperty().bindBidirectional(this.auctionViewModel.getTitleProperty());
    descriptionTextArea.textProperty().bindBidirectional(this.auctionViewModel.getDescriptionProperty());
    errorLabel.textProperty().bindBidirectional(this.auctionViewModel.getErrorProperty());
    Bindings.bindBidirectional(incrementTextField.textProperty(), this.auctionViewModel.getIncrementProperty(), new IntStringConverter());
    reasonTextArea.textProperty().bindBidirectional(this.auctionViewModel.getReasonProperty());
    Bindings.bindBidirectional(reservePriceTextField.textProperty(), this.auctionViewModel.getReservePriceProperty(), new IntStringConverter());
    errorLabel.textProperty().bindBidirectional(this.auctionViewModel.getErrorProperty());
    Bindings.bindBidirectional(timeTextField.textProperty(), this.auctionViewModel.getTimeProperty(), new IntStringConverter());
    Bindings.bindBidirectional(buyoutPriceTextField.textProperty(), this.auctionViewModel.getBuyoutPriceProperty(), new IntStringConverter());
    //other bindings to be inserted


    //auctionViewModel.addListener(this);

    errorLabel.setText("");
    reset(id);
  }
  public void reset(String id)
  {
    auctionViewModel.reset();
    switch (id)
    {
      case "displayAuction":
        setForDisplay();
        break;
      case "startAuction":
        wipe();
        setForStart();
        break;
    }
  }
  private void setForStart()
  {
    anchorPane.setPrefHeight(690);
    startAuctionButton.setLayoutY(625);
    cancelButton.setLayoutY(625);

    titleTextArea.setDisable(false);
    descriptionTextArea.setDisable(false);
    reservePriceTextField.setDisable(false);
    buyoutPriceTextField.setDisable(false);
    incrementTextField.setDisable(false);

    importButton.setVisible(true);
    timeLabel.setVisible(true);
    timeTextField.setVisible(true);
    hoursLabel.setVisible(true);
    startAuctionButton.setVisible(true);
    cancelButton.setVisible(true);

    timerCountdownLabel.setVisible(false);
    currentBidderLabel.setVisible(false);
    currentBidLabel.setVisible(false);
    currentBidderTextLabel.setVisible(false);
    currentBidTextLabel.setVisible(false);
    bidLabel.setVisible(false);
    bidTextField.setVisible(false);
    placeBidButton.setVisible(false);
    buyNowButton.setVisible(false);
    somethingWrongLabel.setVisible(false);
    sellerRateLabel.setVisible(false);
    ratingLabel.setVisible(false);
    deleteButton.setVisible(false);
    reasonTextArea.setVisible(false);

  }
  private void setForDisplay()
  {
    anchorPane.setPrefHeight(960);
    sellerRateLabel.setLayoutY(sellerRateLabel.getLayoutY()-100);
    ratingLabel.setLayoutY(ratingLabel.getLayoutY()-100);

    somethingWrongLabel.setLayoutY(somethingWrongLabel.getLayoutY()-100);
    reasonTextArea.setLayoutY(reasonTextArea.getLayoutY()-100);
    deleteButton.setLayoutY(deleteButton.getLayoutY()-100);


    titleTextArea.setDisable(true);
    descriptionTextArea.setDisable(true);
    reservePriceTextField.setDisable(true);
    buyoutPriceTextField.setDisable(true);
    incrementTextField.setDisable(true);


    timerCountdownLabel.setVisible(true);
    currentBidderLabel.setVisible(true);
    currentBidLabel.setVisible(true);
    currentBidderTextLabel.setVisible(true);
    currentBidTextLabel.setVisible(true);
    bidLabel.setVisible(true);
    bidTextField.setVisible(true);
    placeBidButton.setVisible(true);
    buyNowButton.setVisible(true);
    sellerRateLabel.setVisible(true);
    ratingLabel.setVisible(true);
    //////////////////////////////////////////////
    //should be only visible for moderator
    somethingWrongLabel.setVisible(true);
    deleteButton.setVisible(true);
    reasonTextArea.setVisible(true);
    /////////////////////////////////////////////

    importButton.setVisible(false);
    timeLabel.setVisible(false);
    timeTextField.setVisible(false);
    hoursLabel.setVisible(false);
    startAuctionButton.setVisible(false);
    cancelButton.setVisible(false);

  }

  private void wipe()
  {
    auctionViewModel.wipe();
  }
  public Region getRoot()
  {
    return root;
  }

  @FXML void startAuctionButtonPressed(ActionEvent event)
  {
    auctionViewModel.startAuction();
    if(errorLabel.getText().isEmpty())
    {
      viewHandler.openView("displayAuction");
    }
  }

  @FXML void backButtonPressed(ActionEvent event)
  {
    cancelButtonPressed(event);
  }

  @FXML void cancelButtonPressed(ActionEvent event)
  {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation");
    alert.setHeaderText("Are you sure you want to leave?");
    Optional<ButtonType> result = alert.showAndWait();
    ////////////////////////////////////////////////////////
    if (result.isPresent() && result.get() == ButtonType.OK)
    {
      //viewHandler.openView("allAuctions");
      wipe();
      //sprint 1 focus
      viewHandler.openView("startAuction");
    }
    ////////////////////////////////////////////////////////
  }
  @FXML void buyNowButtonPressed(ActionEvent event)
  {

  }
  @FXML void importButtonPressed(ActionEvent event)
  {

  }

  @FXML void onEnter(ActionEvent event)
  {

  }

  @FXML void placeBidButtonPressed(ActionEvent event)
  {

  }


  public void deleteButtonPressed(ActionEvent actionEvent)
  {
  }
}
