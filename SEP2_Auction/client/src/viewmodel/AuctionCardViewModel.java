package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import model.Auction;
import model.AuctionModel;
import model.AuctionShortVersion;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class AuctionCardViewModel
{
  private IntegerProperty currentBidProperty, idProperty;
  private StringProperty endTimeProperty, titleProperty;
  private ObjectProperty<Image>  imageProperty;
  private AuctionModel model;
  private ViewModelState state;

  public AuctionCardViewModel(AuctionModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;
    idProperty = new SimpleIntegerProperty();
    currentBidProperty = new SimpleIntegerProperty();
    titleProperty = new SimpleStringProperty();
    endTimeProperty=new SimpleStringProperty();
    imageProperty=new SimpleObjectProperty<>();
  }

  public void setData(AuctionShortVersion auction)
  {
      idProperty.set(auction.getId());
      titleProperty.set(auction.getTitle());
      currentBidProperty.set(auction.getCurrentBid());
      endTimeProperty.set("End: "+ auction.getEnd());
      imageProperty.set(byteArrayToImage(auction.getImageData()));
  }
  public void cardSelected()
  {
    try
    {
      state.setAuction(model.getAuction(idProperty.get()));
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  private Image byteArrayToImage(byte[] imageBytes)
  {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
    return new Image(inputStream);
  }
  public ObjectProperty<Image> getImageProperty()
  {
    return imageProperty;
  }
  public StringProperty getTitleProperty()
  {
    return titleProperty;
  }
  public StringProperty getTimerCountdownProperty()
  {
    return endTimeProperty;
  }
  public IntegerProperty getIdProperty()
  {
    return idProperty;
  }
  public IntegerProperty getCurrentBidProperty()
  {
    return currentBidProperty;
  }
}
