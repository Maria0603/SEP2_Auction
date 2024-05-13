package view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import utility.IntStringConverter;
import viewmodel.AuctionViewModel;

import java.io.File;
import java.util.Optional;

public class AuctionViewController
{
  @FXML public TextField incomingBidTextField;
  @FXML private Label currentBidLabel;

  @FXML private ScrollPane auctionScrollPane;
  @FXML private Button backButton;
  @FXML private Label bidLabel;
  @FXML private Button buyNowButton;
  @FXML private TextField buyoutPriceTextField;
  @FXML private Button cancelButton;
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
  private FileChooser fileChooser;

  //TODO: this class needs cleaning; visible property must be set in the view model instead
  //initializations and bindings
  public void init(ViewHandler viewHandler, AuctionViewModel auctionViewModel,
      Region root, WindowType windowType)
  {
    this.root = root;
    this.viewHandler = viewHandler;
    this.auctionViewModel = auctionViewModel;

    Bindings.bindBidirectional(idLabel.textProperty(),
        this.auctionViewModel.getIdProperty(), new IntStringConverter());
    headerLabel.textProperty()
        .bindBidirectional(this.auctionViewModel.getHeaderProperty());
    titleTextArea.textProperty()
        .bindBidirectional(this.auctionViewModel.getTitleProperty());
    descriptionTextArea.textProperty()
        .bindBidirectional(this.auctionViewModel.getDescriptionProperty());
    errorLabel.textProperty()
        .bindBidirectional(this.auctionViewModel.getErrorProperty());
    Bindings.bindBidirectional(incrementTextField.textProperty(),
        this.auctionViewModel.getIncrementProperty(), new IntStringConverter());
    reasonTextArea.textProperty()
        .bindBidirectional(this.auctionViewModel.getReasonProperty());
    Bindings.bindBidirectional(reservePriceTextField.textProperty(),
        this.auctionViewModel.getReservePriceProperty(),
        new IntStringConverter());
    errorLabel.textProperty()
        .bindBidirectional(this.auctionViewModel.getErrorProperty());
    Bindings.bindBidirectional(timeTextField.textProperty(),
        this.auctionViewModel.getTimeProperty(), new IntStringConverter());
    timerCountdownLabel.textProperty()
        .bindBidirectional(this.auctionViewModel.getTimerProperty());
    Bindings.bindBidirectional(buyoutPriceTextField.textProperty(),
        this.auctionViewModel.getBuyoutPriceProperty(),
        new IntStringConverter());
    Bindings.bindBidirectional(imageImageView.imageProperty(),
        auctionViewModel.getImageProperty());
    currentBidderLabel.textProperty().bindBidirectional(this.auctionViewModel.getCurrentBidderProperty());
    //other bindings to be inserted

    Bindings.bindBidirectional(currentBidLabel.textProperty(), this.auctionViewModel.getCurrentBidProperty(), new IntStringConverter());
    Bindings.bindBidirectional(incomingBidTextField.textProperty(), this.auctionViewModel.getIncomingBidProperty(), new IntStringConverter());


    errorLabel.setText("");
    fileChooser = new FileChooser();

    reset(windowType);
    //TODO: everything must be bound to the view model, so we won't have setForStart(), setForDisplay() in the controllers
    /*importButton.visibleProperty().bindBidirectional(this.auctionViewModel.getImportButtonVisible());
    timeLabel.visibleProperty().bindBidirectional(this.auctionViewModel.timeLabelVisibleProperty());
    timeTextField.visibleProperty().bindBidirectional(this.auctionViewModel.timeTextFieldVisibleProperty());
    hoursLabel.visibleProperty().bindBidirectional(this.auctionViewModel.hoursLabelVisibleProperty());
    startAuctionButton.visibleProperty().bindBidirectional(this.auctionViewModel.startAuctionButtonVisibleProperty());
    cancelButton.visibleProperty().bindBidirectional(this.auctionViewModel.cancelButtonVisibleProperty());
    timerCountdownLabel.visibleProperty().bindBidirectional(this.auctionViewModel.timerCountdownLabelVisibleProperty());
    currentBidderLabel.visibleProperty().bindBidirectional(this.auctionViewModel.currentBidderLabelVisibleProperty());
    currentBidLabel.visibleProperty().bindBidirectional(this.auctionViewModel.currentBidLabelVisibleProperty());
    bidLabel.visibleProperty().bindBidirectional(this.auctionViewModel.bidLabelVisibleProperty());
    placeBidButton.visibleProperty().bindBidirectional(this.auctionViewModel.placeBidButtonVisibleProperty());
    buyNowButton.visibleProperty().bindBidirectional(this.auctionViewModel.buyNowButtonVisibleProperty());
    somethingWrongLabel.visibleProperty().bindBidirectional(this.auctionViewModel.somethingWrongLabelVisibleProperty());
    sellerRateLabel.visibleProperty().bindBidirectional(this.auctionViewModel.sellerRateLabelVisibleProperty());
    ratingLabel.visibleProperty().bindBidirectional(this.auctionViewModel.ratingLabelVisibleProperty());
    deleteButton.visibleProperty().bindBidirectional(this.auctionViewModel.deleteButtonVisibleProperty());
    reasonTextArea.visibleProperty().bindBidirectional(this.auctionViewModel.reasonTextAreaVisibleProperty());
    incomingBidTextField.visibleProperty().bindBidirectional(this.auctionViewModel.incomingBidTextFieldVisibleProperty());
    currentBidTextLabel.visibleProperty().bindBidirectional(this.auctionViewModel.currentBidTextLabelVisibleProperty());
    currentBidderTextLabel.visibleProperty().bindBidirectional(this.auctionViewModel.currentBidderTextLabelVisibleProperty());*/
  }

  public void reset(WindowType type)
  {
    auctionViewModel.reset();
    switch (type)
    {
      case DISPLAY_AUCTION:
        setForDisplay();
        break;
      case START_AUCTION:
        setForStart();
        auctionViewModel.wipe();
        break;
    }
  }

  private void setForStart()
  {
    //auctionViewModel.setForStart();

    anchorPane.setPrefHeight(690);
    startAuctionButton.setLayoutY(625);
    cancelButton.setLayoutY(625);

    imageImageView.setImage(null);

    titleTextArea.setDisable(false);
    titleTextArea.requestFocus();

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
    //bidTextField.setVisible(false);
    placeBidButton.setVisible(false);
    buyNowButton.setVisible(false);
    somethingWrongLabel.setVisible(false);
    sellerRateLabel.setVisible(false);
    ratingLabel.setVisible(false);
    deleteButton.setVisible(false);
    reasonTextArea.setVisible(false);

    incomingBidTextField.setVisible(false);

  }

  private void setForDisplay()
  {
    anchorPane.setPrefHeight(960);

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
    incomingBidTextField.setVisible(true);
    //bidTextField.setVisible(true);
    //bidTextField.requestFocus();
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

  public void leaveAuctionView()
  {
    auctionViewModel.leaveAuctionView();
  }

  public Region getRoot()
  {
    return root;
  }

  @FXML void startAuctionButtonPressed(ActionEvent event)
  {
    auctionViewModel.startAuction();
    if (errorLabel.getText().isEmpty())
    {
      viewHandler.openView(WindowType.DISPLAY_AUCTION);
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
      //we want the reset in the view model, so:
     // reset("");
      leaveAuctionView();
      viewHandler.openView(WindowType.ALL_AUCTIONS);
    }
    ////////////////////////////////////////////////////////
  }

  @FXML void importButtonPressed(ActionEvent event)
  {
    imageImageView.setImage(null);
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Open Image File", "*png", "*jpg"));

    File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());

    if (file != null)
    {
      Image image = new Image(file.toURI().toString(), -1, -1, true, true);
      imageImageView.setImage(image);
    }
  }

  @FXML void buyNowButtonPressed(ActionEvent event)
  {

  }

  @FXML void onEnter(ActionEvent event)
  {

  }

  @FXML
  void placeBidButtonPressed(ActionEvent event) {

    auctionViewModel.placeBid();
  }

  public void deleteButtonPressed(ActionEvent actionEvent)
  {
  }

}
