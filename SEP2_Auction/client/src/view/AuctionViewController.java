package view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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

  //initializations and bindings
  public void init(ViewHandler viewHandler, AuctionViewModel auctionViewModel,
      Region root, WindowType windowType)
  {
    this.root = root;
    this.viewHandler = viewHandler;
    this.auctionViewModel = auctionViewModel;
    fileChooser = new FileChooser();

    bindValues();
    bindVisibleProperty();
    bindDisableProperty();

    reset(windowType);
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
        break;
    }
  }

  private void setForStart()
  {
    auctionViewModel.setForStart();
    auctionViewModel.wipe();

    anchorPane.setPrefHeight(680);
    startAuctionButton.setLayoutY(625);
    cancelButton.setLayoutY(625);

    titleTextArea.requestFocus();
  }

  private void setForDisplay()
  {
    anchorPane.setPrefHeight(960);
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
    if (result.isPresent() && result.get() == ButtonType.OK)
    {
      leaveAuctionView();
      viewHandler.openView(WindowType.ALL_AUCTIONS);
    }
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

  @FXML
  void buyNowButtonPressed(ActionEvent event) {
    auctionViewModel.buyOut();
    if (auctionViewModel.isSold()) {
      setAsSold();
    }
  }

  @FXML void onEnter(ActionEvent event)
  {

  }

  @FXML void placeBidButtonPressed(ActionEvent event)
  {
    auctionViewModel.placeBid();
  }

  public void deleteButtonPressed(ActionEvent actionEvent)
  {
  }

  private void bindValues()
  {
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
    currentBidderLabel.textProperty()
        .bindBidirectional(this.auctionViewModel.getCurrentBidderProperty());

    Bindings.bindBidirectional(currentBidLabel.textProperty(),
        this.auctionViewModel.getCurrentBidProperty(),
        new IntStringConverter());
    Bindings.bindBidirectional(incomingBidTextField.textProperty(),
        this.auctionViewModel.getIncomingBidProperty(),
        new IntStringConverter());
  }

  private void bindVisibleProperty()
  {
    importButton.visibleProperty()
        .bindBidirectional(this.auctionViewModel.getStartAuctionVisibility());
    timeLabel.visibleProperty()
        .bindBidirectional(this.auctionViewModel.getStartAuctionVisibility());
    timeTextField.visibleProperty()
        .bindBidirectional(this.auctionViewModel.getStartAuctionVisibility());
    hoursLabel.visibleProperty()
        .bindBidirectional(this.auctionViewModel.getStartAuctionVisibility());
    startAuctionButton.visibleProperty()
        .bindBidirectional(this.auctionViewModel.getStartAuctionVisibility());
    cancelButton.visibleProperty()
        .bindBidirectional(this.auctionViewModel.getStartAuctionVisibility());

    //when startAuctionVisibility is true, invertedBinding will be false
    BooleanBinding invertedBinding = Bindings.createBooleanBinding(
        () -> !this.auctionViewModel.getStartAuctionVisibility().get(),
        this.auctionViewModel.getStartAuctionVisibility());

    timerCountdownLabel.visibleProperty().bind(invertedBinding);
    currentBidderLabel.visibleProperty().bind(invertedBinding);
    currentBidLabel.visibleProperty().bind(invertedBinding);
    bidLabel.visibleProperty().bind(invertedBinding);
    placeBidButton.visibleProperty().bind(invertedBinding);
    buyNowButton.visibleProperty().bind(invertedBinding);
    somethingWrongLabel.visibleProperty().bind(invertedBinding);
    sellerRateLabel.visibleProperty().bind(invertedBinding);
    ratingLabel.visibleProperty().bind(invertedBinding);
    deleteButton.visibleProperty().bind(invertedBinding);
    reasonTextArea.visibleProperty().bind(invertedBinding);
    incomingBidTextField.visibleProperty().bind(invertedBinding);
    currentBidTextLabel.visibleProperty().bind(invertedBinding);
    currentBidderTextLabel.visibleProperty().bind(invertedBinding);
  }

  private void bindDisableProperty()
  {
    titleTextArea.disableProperty()
        .bindBidirectional(auctionViewModel.getDisableAsInDisplay());
    descriptionTextArea.disableProperty()
        .bindBidirectional(auctionViewModel.getDisableAsInDisplay());
    reservePriceTextField.disableProperty()
        .bindBidirectional(auctionViewModel.getDisableAsInDisplay());
    buyoutPriceTextField.disableProperty()
        .bindBidirectional(auctionViewModel.getDisableAsInDisplay());
    incrementTextField.disableProperty()
        .bindBidirectional(auctionViewModel.getDisableAsInDisplay());
  }

  public void setAsSold() {
    try {
      placeBidButton.setDisable(true);
      buyNowButton.setDisable(true);
      incomingBidTextField.setDisable(true);
      reasonTextArea.setDisable(true);

      errorLabel.setText("This item is sold.");

    } catch (Exception e) {
      e.printStackTrace();
      errorLabel.setText("Error occurred while marking the item as sold.");
    }
  }
}
