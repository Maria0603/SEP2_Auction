package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.Auction;
import model.AuctionModel;
import model.Bid;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AuctionViewModel implements PropertyChangeListener
{

  private StringProperty descriptionProperty, errorProperty, headerProperty, reasonProperty, titleProperty, timerProperty, currentBidderProperty;
  private IntegerProperty idProperty, buyoutPriceProperty, incrementProperty, reservePriceProperty, timeProperty, incomingBidProperty, currentBidProperty;
  private StringProperty sellerProperty;
  private BooleanProperty startAuctionVisibility, disableAsInDisplay;
  private BooleanProperty moderatorVisibility;
  private ObjectProperty<Image> imageProperty;
  private AuctionModel model;
  private ViewModelState state;
  private BooleanProperty isSold;

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

    sellerProperty=new SimpleStringProperty();

    incomingBidProperty = new SimpleIntegerProperty();
    currentBidProperty = new SimpleIntegerProperty();
    currentBidderProperty = new SimpleStringProperty();


    startAuctionVisibility = new SimpleBooleanProperty();
    disableAsInDisplay = new SimpleBooleanProperty();
    moderatorVisibility=new SimpleBooleanProperty();

    isSold = new SimpleBooleanProperty();
    model.addListener("Bid", this);
    model.addListener("Edit", this);
    model.addListener("BuyOut", this);

    reset();
  }

  private boolean isSoldSelected()
  {
    return state.getSelectedAuction().getStatus().equals("CLOSED");
  }

  public void setForStart()
  {
    startAuctionVisibility.set(true);
    disableAsInDisplay.set(false);
    moderatorVisibility.set(false);
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
              timeProperty.get(), imageToByteArray(imageProperty.get()),
              state.getUserEmail()).getID()));
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
      bid = model.placeBid(state.getUserEmail(), incomingBidProperty.get(),
          idProperty.get());
    }
    catch (SQLException e)
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

  public void buyout()
  {
    errorProperty.set("");
    try
    {
      //if (!isSold.get()) {
      model.buyout(state.getUserEmail(), idProperty.get());
      //isSold.set(true);
      //removing listeners
      //model.removeListener("Time", this);
      //model.removeListener("End", this);
      //Platform.runLater(() -> timerProperty.set("CLOSED"));
      //reset(); //disable
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
    }

  }

  public void deleteAuction()
  {
    if (!errorProperty.get().contains("closed"))
      errorProperty.set("");
    try
    {
      model.deleteAuction(state.getUserEmail(), idProperty.get(),
          reasonProperty.get().trim());
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
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
      if(isSold.get())
      {
        leaveAuctionView();
        errorProperty.set("Auction is Closed.");
      }
      else
      {
        model.addListener("Time", this);
        model.addListener("End", this);
      }
      System.out.println(isSold.get());
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

  public void leaveAuctionView()
  {
    //when we leave the auction, or we start another one, we remove ourselves from the list of listeners
    model.removeListener("Time", this);
    model.removeListener("End", this);
    errorProperty.set("");
    //////////////////////////////////////////////////////
    timerProperty.set("");
  }

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
  public boolean isModerator()
  {
    return state.isModerator();
  }

  public ObjectProperty<Image> getImageProperty()
  {
    return imageProperty;
  }

  public IntegerProperty getIdProperty()
  {
    return idProperty;
  }

  public StringProperty getSellerProperty()
  {
    return sellerProperty;
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

  public StringProperty getCurrentBidderProperty()
  {
    return currentBidderProperty;
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

  public IntegerProperty getIncomingBidProperty()
  {
    return incomingBidProperty;
  }

  public BooleanProperty getStartAuctionVisibility()
  {
    return startAuctionVisibility;
  }
  public BooleanProperty getModeratorVisibility()
  {
    return moderatorVisibility;
  }

  public BooleanProperty getDisableAsInDisplay()
  {
    return disableAsInDisplay;
  }

  @Override public void propertyChange(PropertyChangeEvent event)
  {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    System.out.println(event.getPropertyName() + " received in view model");

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
          if(event.getNewValue() instanceof Bid)
          {
            Bid buyout=(Bid)event.getNewValue();
            Platform.runLater(() -> {
              currentBidProperty.set(buyout.getBidAmount());
              currentBidderProperty.set(buyout.getBidder());
            });
          }
          //model.removeListener("Time", this);

          //Auction boughtOutAuction = (Auction) event.getNewValue();
          //boughtOutAuction.setStatus("CLOSED");
          //isSold.set(true);
          //state.setAuction(boughtOutAuction);
        }
      }
      case "Bid" ->
      {
        Bid bid = (Bid) event.getNewValue();
        /*if ((state.getSelectedAuction().getID()) == bid.getAuctionId()) {
          try {
            state.setAuction(model.getAuction(bid.getAuctionId()));
          }
          catch (SQLException e) {
            e.printStackTrace();
          }
        }*/
        if (idProperty.get() == bid.getAuctionId())
        {
          try
          {
            state.setAuction(model.getAuction(bid.getAuctionId()));
          }
          catch (SQLException e)
          {
            Platform.runLater(()->errorProperty.set(e.getMessage()));
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
          Platform.runLater(()->currentBidderProperty.set(event.getNewValue().toString()));
        }
        if(sellerProperty.get().equals(event.getNewValue().toString()))
        {
          state.getSelectedAuction().setSeller(event.getNewValue().toString());
          Platform.runLater(()->sellerProperty.set(event.getNewValue().toString()));
        }
      }
      case "BuyOut" ->
      {
        Auction boughtOutAuction = (Auction) event.getNewValue();
        boughtOutAuction.setStatus("CLOSED");
        isSold.set(true);
        leaveAuctionView();
        state.setAuction(boughtOutAuction);
        reset();
      }
    }
  }

}
