package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import model.Auction;
import model.AuctionModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AuctionViewModel implements PropertyChangeListener
{
  private StringProperty descriptionProperty, errorProperty, headerProperty, reasonProperty,  titleProperty, pathProperty, timerProperty;
  private IntegerProperty idProperty, bidProperty, buyoutPriceProperty, incrementProperty, ratingProperty, reservePriceProperty, timeProperty;
  private BooleanProperty end;
  private AuctionModel model;
  private ViewModelState state;

  public AuctionViewModel(AuctionModel model, ViewModelState state)
  {
    this.model=model;
    this.state=state;
    idProperty=new SimpleIntegerProperty();
    bidProperty=new SimpleIntegerProperty();
    buyoutPriceProperty=new SimpleIntegerProperty();
    descriptionProperty=new SimpleStringProperty();
    errorProperty=new SimpleStringProperty();
    headerProperty=new SimpleStringProperty("Auction ID: ");
    incrementProperty=new SimpleIntegerProperty();
    ratingProperty=new SimpleIntegerProperty();
    reasonProperty=new SimpleStringProperty();
    reservePriceProperty=new SimpleIntegerProperty();
    timeProperty=new SimpleIntegerProperty();
    timerProperty=new SimpleStringProperty();
    titleProperty=new SimpleStringProperty();
    pathProperty=new SimpleStringProperty();
    //to be used when the placeBid() method is called;
    end=new SimpleBooleanProperty();
    ////////////////////////////////////
    reset("startAuction");
  }
  public void startAuction()
  {
    errorProperty.set("");
    try
    {
      state.setAuction(model.startAuction(idProperty.get(), titleProperty.get(), descriptionProperty.get(),
          reservePriceProperty.get(), buyoutPriceProperty.get(),
          incrementProperty.get(), timeProperty.get(), pathProperty.get()));
    }
    catch(IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
  }
  public void reset(String id)
  {
    Auction selectedAuction=state.getSelectedAuction();
    if(selectedAuction!=null)
    {
      if(id.equals("displayAuction"))
      {
        titleProperty.set(state.getSelectedAuction().getTitle());
        descriptionProperty.set(state.getSelectedAuction().getDescription());
        reservePriceProperty.set(state.getSelectedAuction().getReservePrice());
        buyoutPriceProperty.set(state.getSelectedAuction().getBuyoutPrice());
        incrementProperty.set(state.getSelectedAuction().getMinimumIncrement());

        /*
        titleProperty.set("iphone 7 ");
        descriptionProperty.set("used phone, it dies when it's cold outside, but people will worship you");
        reservePriceProperty.set(300);
        buyoutPriceProperty.set(999999999);
        incrementProperty.set(20);
         */
      }
      //else if(id.equals("startAuction"))
      else
      {
        wipe();
      }

    }

  }
  private void wipe()
  {
    bidProperty.set(0);
    buyoutPriceProperty.set(0);
    descriptionProperty.set("");
    errorProperty.set("");
    headerProperty=new SimpleStringProperty("Auction ID: ");
    incrementProperty.set(1);
    ratingProperty.set(0);
    reasonProperty.set("");
    reservePriceProperty.set(0);
    timeProperty.set(0);
    titleProperty.set("");
    end.set(false);
  }
  public IntegerProperty getIdProperty()
  {
    return idProperty;
  }

  public StringProperty getPathProperty()
  {
    return pathProperty;
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
    switch (event.getPropertyName())
    {
      case "Time":
        Platform.runLater(()->timerProperty.set((String)event.getNewValue()));
        System.out.println(event.getNewValue());
        break;
      case "End":
        Platform.runLater(()->end.set(true));
        Platform.runLater(()->errorProperty.set("Auction closed."));
        //Platform.runLater(()->currentBidTitle.set("Final bid: "));
        //property.firePropertyChange("The end is here", null, null);
        break;
      case "Auction":
        //add auction to the list of auctions

    }
  }
}
