package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.Auction;
import model.AuctionModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AuctionCardViewModel implements PropertyChangeListener
{
  private IntegerProperty currentBidProperty, idProperty;
  private StringProperty endTimeProperty, titleProperty;
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

    //model.addListener("Auction", this);
    //model.addListener("Time", this);
    //model.addListener("End", this);
    //reset(null);
  }

  public void setData(Auction auction)
  {
    idProperty.set(auction.getID());
    titleProperty.set(auction.getItem().getTitle());
    currentBidProperty.set(auction.getCurrentBid());
    endTimeProperty.set("Ends: "+ auction.getEndTime().toString());
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

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    switch (evt.getPropertyName())
    {
      case "Time":
        if (idProperty.equals(evt.getOldValue())) //we should only update one auction for each event
        {
          LocalTime time = LocalTime.ofSecondOfDay((int) evt.getNewValue());
          //Platform.runLater(() -> timerCountdownProperty.set(time.format(timeFormatter)));
        }
    }
  }
}
