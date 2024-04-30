package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.Auction;
import model.AuctionModel;
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
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class AuctionViewModel
    implements PropertyChangeListener
{
  private StringProperty descriptionProperty, errorProperty, headerProperty, reasonProperty, titleProperty, timerProperty;
  private IntegerProperty idProperty, bidProperty, buyoutPriceProperty, incrementProperty, ratingProperty, reservePriceProperty, timeProperty;
  private ObjectProperty<Image> imageProperty;
  private AuctionModel model;
  private ViewModelState state;

  public AuctionViewModel(AuctionModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;
    idProperty = new SimpleIntegerProperty();
    bidProperty = new SimpleIntegerProperty();
    buyoutPriceProperty = new SimpleIntegerProperty();
    descriptionProperty = new SimpleStringProperty();
    errorProperty = new SimpleStringProperty();
    headerProperty = new SimpleStringProperty("");
    incrementProperty = new SimpleIntegerProperty();
    ratingProperty = new SimpleIntegerProperty();
    reasonProperty = new SimpleStringProperty();
    reservePriceProperty = new SimpleIntegerProperty();
    timeProperty = new SimpleIntegerProperty();
    timerProperty = new SimpleStringProperty();
    titleProperty = new SimpleStringProperty();
    imageProperty=new SimpleObjectProperty<>();

    model.addListener("Auction", this);
    //model.addListener("Time", this);
    model.addListener("End", this);
    reset("startAuction");
  }

  public void startAuction()
  {
    errorProperty.set("");
    try
    {
      //we cannot send the null image through model, because it cannot be converted into bytes, so:
      if(imageProperty.get()==null)
        throw new IllegalArgumentException("Please upload an image.");
      state.setAuction(model.startAuction(titleProperty.get().trim(),
          descriptionProperty.get().trim(), reservePriceProperty.get(),
          buyoutPriceProperty.get(), incrementProperty.get(),
          timeProperty.get(), imageToByteArray(imageProperty.get())));
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

  public void reset(String id)
  {
    Auction selectedAuction = state.getSelectedAuction();
    if (selectedAuction != null && id.equals("displayAuction"))
    {
      model.addListener("Time", this);
      headerProperty.set("Auction ID:");
      idProperty.set(state.getSelectedAuction().getID());
      titleProperty.set(state.getSelectedAuction().getItem().getTitle());
      descriptionProperty.set(
          state.getSelectedAuction().getItem().getDescription());
      reservePriceProperty.set(
          state.getSelectedAuction().getPriceConstraint().getReservePrice());
      buyoutPriceProperty.set(
          state.getSelectedAuction().getPriceConstraint().getBuyoutPrice());
      incrementProperty.set(state.getSelectedAuction().getPriceConstraint()
          .getMinimumIncrement());
    }
    else
    {
      wipe();
    }
  }

  private void wipe()
  {
    state.setAuction(null);
    model.removeListener("Time", this);
    headerProperty.set("Start auction");
    idProperty.set(0);
    bidProperty.set(0);
    buyoutPriceProperty.set(0);
    descriptionProperty.set("");
    errorProperty.set("");
    incrementProperty.set(0);
    ratingProperty.set(0);
    reasonProperty.set("");
    reservePriceProperty.set(0);
    timeProperty.set(0);
    titleProperty.set("");
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

  public IntegerProperty getBidProperty()
  {
    return bidProperty;
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

  @Override public void propertyChange(PropertyChangeEvent event)
  {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    switch (event.getPropertyName())
    {
      case "Time":
        // if(idProperty.equals(event.getOldValue())) //we should only update one
        // auction for each event
      {
        LocalTime time = LocalTime.ofSecondOfDay((long) event.getNewValue());
        Platform.runLater(() -> timerProperty.set(time.format(timeFormatter)));
      }
      break;
      case "End":
        Platform.runLater(() -> errorProperty.set("Auction closed."));
        break;
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
        break;
    }
  }

}
