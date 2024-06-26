package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.domain.Auction;
import model.AuctionModel;
import model.domain.Bid;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * The AuctionViewModel class is responsible for managing the data and actions
 * related to displaying an auction in the view.
 */
public class AuctionViewModel implements PropertyChangeListener
{
  private final StringProperty descriptionProperty;
  private final StringProperty errorProperty;
  private final StringProperty headerProperty;
  private final StringProperty reasonProperty;
  private final StringProperty titleProperty;
  private final StringProperty timerProperty;
  private final StringProperty currentBidderProperty;
  private final IntegerProperty idProperty;
  private final IntegerProperty buyoutPriceProperty;
  private final IntegerProperty incrementProperty;
  private final IntegerProperty reservePriceProperty;
  private final IntegerProperty timeProperty;
  private final IntegerProperty incomingBidProperty;
  private final IntegerProperty currentBidProperty;
  private final StringProperty sellerProperty;
  private final BooleanProperty startAuctionVisibility;
  private final BooleanProperty disableAsInDisplay;
  private final BooleanProperty moderatorVisibility;
  private final ObjectProperty<Image> imageProperty;
  private final AuctionModel model;
  private final ViewModelState state;
  private final BooleanProperty isSold;

  /**
   * Constructs an AuctionViewModel with the specified model and view model state.
   *
   * @param model the auction model
   * @param state the view model state
   */
  public AuctionViewModel(AuctionModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;
    idProperty = new SimpleIntegerProperty();
    buyoutPriceProperty = new SimpleIntegerProperty();
    descriptionProperty = new SimpleStringProperty();
    errorProperty = new SimpleStringProperty();
    headerProperty = new SimpleStringProperty();
    incrementProperty = new SimpleIntegerProperty();
    reasonProperty = new SimpleStringProperty();
    reservePriceProperty = new SimpleIntegerProperty();
    timeProperty = new SimpleIntegerProperty();
    timerProperty = new SimpleStringProperty();
    titleProperty = new SimpleStringProperty();
    imageProperty = new SimpleObjectProperty<>();
    sellerProperty = new SimpleStringProperty();
    incomingBidProperty = new SimpleIntegerProperty();
    currentBidProperty = new SimpleIntegerProperty();
    currentBidderProperty = new SimpleStringProperty();
    startAuctionVisibility = new SimpleBooleanProperty();
    disableAsInDisplay = new SimpleBooleanProperty();
    moderatorVisibility = new SimpleBooleanProperty();
    isSold = new SimpleBooleanProperty();
    model.addListener("Bid", this);
    model.addListener("Edit", this);

    reset();
  }

  /**
   * Checks if the selected auction is sold.
   *
   * @return true if the auction is sold, false otherwise
   */
  private boolean isSoldSelected()
  {
    return state.getSelectedAuction().getStatus().equals("CLOSED");
  }

  /**
   * Sets the view model for starting an auction.
   */
  public void setForStart()
  {
    startAuctionVisibility.set(true);
    disableAsInDisplay.set(false);
    moderatorVisibility.set(false);
  }

  /**
   * Starts an auction.
   */
  public void startAuction()
  {
    errorProperty.set("");
    try
    {
      // We cannot send a null image through the model, because it cannot be converted into bytes, so:
      if (imageProperty.get() == null)
        throw new IllegalArgumentException("Please upload an image.");
      state.setAuction(model.getAuction(
              model.startAuction(titleProperty.get().trim(),
                      descriptionProperty.get().trim(), reservePriceProperty.get(),
                      buyoutPriceProperty.get(), incrementProperty.get(),
                      timeProperty.get(), imageToByteArray(imageProperty.get()),
                      state.getUserEmail()).getID()));
    }
    catch (IllegalArgumentException | ClassNotFoundException |
           IOException e)
    {
      errorProperty.set(e.getMessage());
      titleProperty.set(titleProperty.get().trim());
      descriptionProperty.set(descriptionProperty.get().trim());
    }
  }

  /**
   * Places a bid on the auction.
   */
  public void placeBid()
  {
    errorProperty.set("");
    Bid bid = null;
    try
    {
      bid = model.placeBid(state.getUserEmail(), incomingBidProperty.get(),
              idProperty.get());
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
      incomingBidProperty.set(0);
    }
    if (errorProperty.get().isEmpty() && bid != null
            && idProperty.get() == bid.getAuctionId())
    {
      currentBidProperty.set(bid.getBidAmount());
      currentBidderProperty.set(bid.getBidder());
      incomingBidProperty.set(0);
    }
  }

  /**
   * Buys out the auction.
   */
  public void buyout()
  {
    errorProperty.set("");
    try
    {
      model.buyout(state.getUserEmail(), idProperty.get());
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
  }

  /**
   * Deletes the auction.
   */
  public void deleteAuction()
  {
    if (!errorProperty.get().contains("closed"))
      errorProperty.set("");
    try
    {
      model.deleteAuction(state.getUserEmail(), idProperty.get(),
              reasonProperty.get().trim());
    }
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
  }

  /**
   * Converts an Image object to a byte array.
   *
   * @param image the image to convert
   * @return the byte array representation of the image
   * @throws IOException if an I/O error occurs
   */
  private byte[] imageToByteArray(Image image) throws IOException
  {
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    javax.imageio.ImageIO.write(bufferedImage, "png", outputStream);
    return outputStream.toByteArray();
  }

  /**
   * Converts a byte array to an Image object.
   *
   * @param imageBytes the byte array of the image
   * @return the Image object
   */
  private Image byteArrayToImage(byte[] imageBytes)
  {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
    return new Image(inputStream);
  }

  /**
   * Resets the view model to its initial state.
   */
  public void reset()
  {
    errorProperty.set("");
    reasonProperty.set("");
    moderatorVisibility.set(state.isModerator());

    Auction selectedAuction = state.getSelectedAuction();

    if (selectedAuction != null)
    {
      isSold.set(isSoldSelected());
    }

    if (selectedAuction != null)
    {
      if (isSold.get())
      {
        leaveAuctionView();
        errorProperty.set("This auction is closed.");
      }
      else
      {
        model.addListener("Time", this);
        model.addListener("End", this);
      }
      startAuctionVisibility.set(false);
      disableAsInDisplay.set(true);

      headerProperty.set("Auction ID:");
      idProperty.set(selectedAuction.getID());
      titleProperty.set(selectedAuction.getItem().getTitle());
      descriptionProperty.set(selectedAuction.getItem().getDescription());
      reservePriceProperty.set(
              selectedAuction.getPriceConstraint().getReservePrice());
      buyoutPriceProperty.set(
              selectedAuction.getPriceConstraint().getBuyoutPrice());
      incrementProperty.set(
              selectedAuction.getPriceConstraint().getMinimumIncrement());
      imageProperty.set(byteArrayToImage(selectedAuction.getImageData()));
      currentBidderProperty.set(selectedAuction.getCurrentBidder());
      currentBidProperty.set(selectedAuction.getCurrentBid());

      sellerProperty.set(selectedAuction.getSeller());
    }
    else
    {
      wipe();
    }
  }

  /**
   * Leaves the auction view, removing listeners.
   */
  public void leaveAuctionView()
  {
    // When we leave the auction, or we start another one, we remove ourselves from the list of listeners
    model.removeListener("Time", this);
    model.removeListener("End", this);
    errorProperty.set("");
    timerProperty.set("");
  }

  /**
   * Wipes the view model, resetting all properties to their default values.
   */
  public void wipe()
  {
    headerProperty.set("Start auction");
    idProperty.set(0);
    buyoutPriceProperty.set(0);
    descriptionProperty.set("");
    errorProperty.set("");
    incrementProperty.set(0);
    reasonProperty.set("");
    reservePriceProperty.set(0);
    timeProperty.set(0);
    titleProperty.set("");
    imageProperty.set(null);
  }

  /**
   * Checks if the current user is a moderator.
   *
   * @return true if the user is a moderator, false otherwise
   */
  public boolean isModerator()
  {
    return state.isModerator();
  }

  /**
   * Gets the image property.
   *
   * @return the image property
   */
  public ObjectProperty<Image> getImageProperty()
  {
    return imageProperty;
  }

  /**
   * Gets the ID property.
   *
   * @return the ID property
   */
  public IntegerProperty getIdProperty()
  {
    return idProperty;
  }

  /**
   * Gets the seller property.
   *
   * @return the seller property
   */
  public StringProperty getSellerProperty()
  {
    return sellerProperty;
  }

  /**
   * Gets the reason property.
   *
   * @return the reason property
   */
  public StringProperty getReasonProperty()
  {
    return reasonProperty;
  }

  /**
   * Gets the reserve price property.
   *
   * @return the reserve price property
   */
  public IntegerProperty getReservePriceProperty()
  {
    return reservePriceProperty;
  }

  /**
   * Gets the time property.
   *
   * @return the time property
   */
  public IntegerProperty getTimeProperty()
  {
    return timeProperty;
  }

  /**
   * Gets the timer property.
   *
   * @return the timer property
   */
  public StringProperty getTimerProperty()
  {
    return timerProperty;
  }

  /**
   * Gets the title property.
   *
   * @return the title property
   */
  public StringProperty getTitleProperty()
  {
    return titleProperty;
  }

  /**
   * Gets the current bid property.
   *
   * @return the current bid property
   */
  public IntegerProperty getCurrentBidProperty()
  {
    return currentBidProperty;
  }

  /**
   * Gets the current bidder property.
   *
   * @return the current bidder property
   */
  public StringProperty getCurrentBidderProperty()
  {
    return currentBidderProperty;
  }

  /**
   * Gets the buyout price property.
   *
   * @return the buyout price property
   */
  public IntegerProperty getBuyoutPriceProperty()
  {
    return buyoutPriceProperty;
  }

  /**
   * Gets the description property.
   *
   * @return the description property
   */
  public StringProperty getDescriptionProperty()
  {
    return descriptionProperty;
  }

  /**
   * Gets the error property.
   *
   * @return the error property
   */
  public StringProperty getErrorProperty()
  {
    return errorProperty;
  }

  /**
   * Gets the header property.
   *
   * @return the header property
   */
  public StringProperty getHeaderProperty()
  {
    return headerProperty;
  }

  /**
   * Gets the increment property.
   *
   * @return the increment property
   */
  public IntegerProperty getIncrementProperty()
  {
    return incrementProperty;
  }

  /**
   * Gets the incoming bid property.
   *
   * @return the incoming bid property
   */
  public IntegerProperty getIncomingBidProperty()
  {
    return incomingBidProperty;
  }

  /**
   * Gets the start auction visibility property.
   *
   * @return the start auction visibility property
   */
  public BooleanProperty getStartAuctionVisibility()
  {
    return startAuctionVisibility;
  }

  /**
   * Gets the moderator visibility property.
   *
   * @return the moderator visibility property
   */
  public BooleanProperty getModeratorVisibility()
  {
    return moderatorVisibility;
  }

  /**
   * Gets the disable as in display property.
   *
   * @return the disable as in display property
   */
  public BooleanProperty getDisableAsInDisplay()
  {
    return disableAsInDisplay;
  }

  /**
   * Handles property change events.
   *
   * @param event the property change event
   */
  @Override public void propertyChange(PropertyChangeEvent event)
  {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    switch (event.getPropertyName())
    {
      case "Time" ->
      {
        if (!isSold.get() && idProperty.get() == Integer.parseInt(
                event.getOldValue().toString()))
        {
          LocalTime time = LocalTime.ofSecondOfDay((long) event.getNewValue());
          Platform.runLater(
                  () -> timerProperty.set(time.format(timeFormatter)));
        }
      }
      case "End" ->
      {
        if (idProperty.get() == Integer.parseInt(
                event.getOldValue().toString()))
        {
          Platform.runLater(() ->
          {
            leaveAuctionView();
            reset();
          });
          if (event.getNewValue() instanceof Bid)
          {
            Bid buyout = (Bid) event.getNewValue();
            Platform.runLater(() -> {
              currentBidProperty.set(buyout.getBidAmount());
              currentBidderProperty.set(buyout.getBidder());
            });
          }
        }
      }
      case "Bid" ->
      {
        Bid bid = (Bid) event.getNewValue();
        if (idProperty.get() == bid.getAuctionId())
        {
          try
          {
            state.setAuction(model.getAuction(bid.getAuctionId()));
          }
          catch (IllegalArgumentException e)
          {
            Platform.runLater(() -> errorProperty.set(e.getMessage()));
          }
          Platform.runLater(() -> {
            currentBidProperty.set(bid.getBidAmount());
            currentBidderProperty.set(bid.getBidder());
          });
        }
      }
      case "Edit" ->
      {
        if (currentBidderProperty.get().equals(event.getOldValue()))
        {
          state.getSelectedAuction()
                  .setCurrentBidder(event.getNewValue().toString());
          Platform.runLater(() -> currentBidderProperty.set(event.getNewValue().toString()));
        }
        if (sellerProperty.get().equals(event.getNewValue().toString()))
        {
          state.getSelectedAuction().setSeller(event.getNewValue().toString());
          Platform.runLater(() -> sellerProperty.set(event.getNewValue().toString()));
        }
      }
    }
  }
}
