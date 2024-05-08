package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.Auction;
import model.AuctionModel;
import model.Bid;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class AuctionViewModel implements PropertyChangeListener
{
  private StringProperty descriptionProperty, errorProperty, headerProperty, reasonProperty, titleProperty, timerProperty, currentBidderProperty;
  private IntegerProperty idProperty, buyoutPriceProperty, incrementProperty, ratingProperty, reservePriceProperty, timeProperty, incomingBidProperty, currentBidProperty;;
  private ObjectProperty<Image> imageProperty;
  private AuctionModel model;
  private ViewModelState state;

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
    ratingProperty = new SimpleIntegerProperty();
    reasonProperty = new SimpleStringProperty();
    reservePriceProperty = new SimpleIntegerProperty();
    timeProperty = new SimpleIntegerProperty();
    timerProperty = new SimpleStringProperty();
    titleProperty = new SimpleStringProperty();
    imageProperty = new SimpleObjectProperty<>();

    incomingBidProperty = new SimpleIntegerProperty();
    currentBidProperty = new SimpleIntegerProperty();
    currentBidderProperty=new SimpleStringProperty();
    //model.addListener("Time", this);
    //model.addListener("End", this);
    reset();
  }

  public void startAuction()
  {
    errorProperty.set("");
    try
    {
      //we cannot send the null image through model, because it cannot be converted into bytes, so:
      if (imageProperty.get() == null)
        throw new IllegalArgumentException("Please upload an image.");
      state.setAuction(model.getAuction(
          model.startAuction(titleProperty.get().trim(),
                  descriptionProperty.get().trim(), reservePriceProperty.get(),
                  buyoutPriceProperty.get(), incrementProperty.get(),
                  timeProperty.get(), imageToByteArray(imageProperty.get()))
              .getID()));
    }
    catch (IllegalArgumentException | SQLException | ClassNotFoundException |
           IOException e)
    {
      errorProperty.set(e.getMessage());
      titleProperty.set(titleProperty.get().trim());
      descriptionProperty.set(descriptionProperty.get().trim());
      //only for testing:
      //e.printStackTrace();
    }
  }
  public void placeBid()
  {
    errorProperty.set("");
    Bid bid = null;
    try
    {
      bid=model.placeBid(state.getEmail(), incomingBidProperty.get(), idProperty.get());
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
    }

    if(errorProperty.get().isEmpty() && bid!=null)
    {
      currentBidProperty.set(bid.getBidAmount());
      currentBidderProperty.set(bid.getBidder());
    }
  }

  private byte[] imageToByteArray(Image image) throws IOException
  {
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    javax.imageio.ImageIO.write(bufferedImage, "png", outputStream);
    return outputStream.toByteArray();
  }

  private Image byteArrayToImage(byte[] imageBytes)
  {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
    return new Image(inputStream);
  }

  public void reset()
  {
    Auction selectedAuction = state.getSelectedAuction();
    if (selectedAuction != null)
    {
      //when we open an auction, we listen to the updating time
      model.addListener("Time", this);
      model.addListener("End", this);

      headerProperty.set("Auction ID:");
      idProperty.set(selectedAuction.getID());
      titleProperty.set(selectedAuction.getItem().getTitle());
      descriptionProperty.set(selectedAuction.getItem().getDescription());
      reservePriceProperty.set(
          selectedAuction.getPriceConstraint().getReservePrice());
      buyoutPriceProperty.set(
          selectedAuction.getPriceConstraint().getBuyoutPrice());
      incrementProperty.set(state.getSelectedAuction().getPriceConstraint()
          .getMinimumIncrement());
      imageProperty.set(byteArrayToImage(selectedAuction.getImageData()));
      currentBidderProperty.set(selectedAuction.getCurrentBidder());
    }
    else
    {
      wipe();
    }
  }

  public void leaveAuctionView()
  {
    //when we leave the auction, or we start another one, we remove ourselves from the list of listeners
    model.removeListener("Time", this);
    model.removeListener("End", this);
  }

  public void wipe()
  {
    headerProperty.set("Start auction");
    idProperty.set(0);
    currentBidProperty.set(0);
    buyoutPriceProperty.set(0);
    descriptionProperty.set("");
    errorProperty.set("");
    incrementProperty.set(0);
    ratingProperty.set(0);
    reasonProperty.set("");
    reservePriceProperty.set(0);
    timeProperty.set(0);
    titleProperty.set("");
    currentBidderProperty.set("No bidder");
  }

  public ObjectProperty<Image> getImageProperty()
  {
    return imageProperty;
  }

  public IntegerProperty getIdProperty()
  {
    return idProperty;
  }

  public IntegerProperty getRatingProperty()
  {
    return ratingProperty;
  }

  public StringProperty getReasonProperty()
  {
    return reasonProperty;
  }

  public IntegerProperty getReservePriceProperty()
  {
    return reservePriceProperty;
  }

  public IntegerProperty getTimeProperty()
  {
    return timeProperty;
  }

  public StringProperty getTimerProperty()
  {
    return timerProperty;
  }

  public StringProperty getTitleProperty()
  {
    return titleProperty;
  }

  public IntegerProperty getCurrentBidProperty()
  {
    return currentBidProperty;
  }

  public IntegerProperty getBuyoutPriceProperty()
  {
    return buyoutPriceProperty;
  }

  public StringProperty getDescriptionProperty()
  {
    return descriptionProperty;
  }

  public StringProperty getErrorProperty()
  {
    return errorProperty;
  }

  public StringProperty getHeaderProperty()
  {
    return headerProperty;
  }

  public IntegerProperty getIncrementProperty()
  {
    return incrementProperty;
  }
  public IntegerProperty getIncomingBidProperty() {
    return incomingBidProperty;
  }

  @Override public void propertyChange(PropertyChangeEvent event)
  {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    switch (event.getPropertyName())
    {
      case "Time":
        //System.out.println(event.getOldValue());
        if (idProperty.get() == Integer.parseInt(
            event.getOldValue().toString()))
        {
          LocalTime time = LocalTime.ofSecondOfDay((long) event.getNewValue());
          Platform.runLater(
              () -> timerProperty.set(time.format(timeFormatter)));
        }
        break;
      case "End":
        if (idProperty.get() == Integer.parseInt(
            event.getOldValue().toString()))
        {
          Platform.runLater(() -> errorProperty.set("AUCTION CLOSED."));
        }
        break;
        /*
      case "Auction":
        int auctionId = ((Auction) event.getNewValue()).getID();

        Platform.runLater(() -> {
          headerProperty.set("Auction ID:");
          idProperty.set(auctionId);
          try
          {
            Auction auction=model.getAuction(auctionId);
            titleProperty.set(
                auction.getItem().getTitle());
            descriptionProperty.set(
                auction.getItem().getDescription());
            reservePriceProperty.set(
                auction.getPriceConstraint()
                    .getReservePrice());
            buyoutPriceProperty.set(
                auction.getPriceConstraint()
                    .getBuyoutPrice());
            incrementProperty.set(
                auction.getPriceConstraint()
                    .getMinimumIncrement());
            imageProperty.set(byteArrayToImage(auction.getImageData()));
          }
          catch (SQLException e)
          {
            // put it in the error label
            e.printStackTrace();
          }
        });
        break;*/
    }
  }


}
