package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import model.domain.Auction;
import model.AuctionModel;
import model.domain.Bid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;

/**
 * The AuctionCardViewModel class is responsible for managing the data and actions
 * related to displaying an auction card in the view.
 */
public class AuctionCardViewModel implements PropertyChangeListener
{
  private final IntegerProperty currentBidProperty;
  private final IntegerProperty idProperty;
  private final StringProperty endTimeProperty;
  private final StringProperty titleProperty;
  private final ObjectProperty<Image> imageProperty;
  private final AuctionModel model;
  private final ViewModelState state;

  /**
   * Constructs an AuctionCardViewModel with the specified model and view model state.
   *
   * @param model the auction model
   * @param state the view model state
   */
  public AuctionCardViewModel(AuctionModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;
    idProperty = new SimpleIntegerProperty();
    currentBidProperty = new SimpleIntegerProperty();
    titleProperty = new SimpleStringProperty();
    endTimeProperty = new SimpleStringProperty();
    imageProperty = new SimpleObjectProperty<>();
    model.addListener("Bid", this);
  }

  /**
   * Sets the data for the auction card view model based on the specified auction.
   *
   * @param auction the auction
   */
  public void setData(Auction auction)
  {
    idProperty.set(auction.getID());
    titleProperty.set(auction.getItem().getTitle());
    currentBidProperty.set(auction.getCurrentBid());
    endTimeProperty.set("End: " + auction.getEndTime());
    imageProperty.set(byteArrayToImage(auction.getImageData()));
  }

  /**
   * Handles the selection of the auction card.
   */
  public void cardSelected()
  {
    try
    {
      state.setAuction(model.getAuction(idProperty.get()));
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
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
   * Gets the image property.
   *
   * @return the image property
   */
  public ObjectProperty<Image> getImageProperty()
  {
    return imageProperty;
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
   * Gets the timer countdown property.
   *
   * @return the timer countdown property
   */
  public StringProperty getTimerCountdownProperty()
  {
    return endTimeProperty;
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
   * Gets the current bid property.
   *
   * @return the current bid property
   */
  public IntegerProperty getCurrentBidProperty()
  {
    return currentBidProperty;
  }

  /**
   * Handles property change events for bids.
   *
   * @param evt the property change event
   */
  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    Bid bid = (Bid) evt.getNewValue();
    if (idProperty.get() == bid.getAuctionId())
      Platform.runLater(() -> currentBidProperty.set(bid.getBidAmount()));
  }
}
