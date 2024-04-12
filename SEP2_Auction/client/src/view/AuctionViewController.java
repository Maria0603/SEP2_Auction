package view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import utility.IntStringConverter;
import viewmodel.AuctionViewModel;

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
    switch (id)
    {
      case "displayAuction":
        setForDisplay();
        break;
      case "startAuction":
        setForStart();
        break;
    }
    reset();
  }
  public void reset()
  {
    auctionViewModel.reset();
  }
  public void setForStart()
  {
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
  public void setForDisplay()
  {
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

  public Region getRoot()
  {
    return root;
  }

  @FXML void backButtonPressed(ActionEvent event)
  {
    //auctionViewModel.back();
  }

  @FXML void buyNowButtonPressed(ActionEvent event)
  {

  }

  @FXML void cancelButtonPressed(ActionEvent event)
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

  @FXML void report_deleteButtonPressed(ActionEvent event)
  {

  }

  @FXML
  void startAuctionButtonPressed(ActionEvent event)
  {
    auctionViewModel.startAuction();
  }

  public void deleteButtonPressed(ActionEvent actionEvent)
  {
  }
}
