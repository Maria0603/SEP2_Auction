package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import model.Auction;
import model.AuctionModel;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AuctionViewModel implements PropertyChangeListener,
    NamedPropertyChangeSubject
{
  private StringProperty descriptionProperty, errorProperty, headerProperty, reasonProperty,  titleProperty, pathProperty, timerProperty;
  private IntegerProperty idProperty, bidProperty, buyoutPriceProperty, incrementProperty, ratingProperty, reservePriceProperty, timeProperty;
  private AuctionModel model;
  private ViewModelState state;
  private PropertyChangeSupport property;

  public AuctionViewModel(AuctionModel model, ViewModelState state)
  {
    property=new PropertyChangeSupport(this);
    this.model=model;
    this.state=state;
    idProperty=new SimpleIntegerProperty();
    bidProperty=new SimpleIntegerProperty();
    buyoutPriceProperty=new SimpleIntegerProperty();
    descriptionProperty=new SimpleStringProperty();
    errorProperty=new SimpleStringProperty();
    headerProperty=new SimpleStringProperty("");
    incrementProperty=new SimpleIntegerProperty();
    ratingProperty=new SimpleIntegerProperty();
    reasonProperty=new SimpleStringProperty();
    reservePriceProperty=new SimpleIntegerProperty();
    timeProperty=new SimpleIntegerProperty();
    timerProperty=new SimpleStringProperty();
    titleProperty=new SimpleStringProperty();
    pathProperty=new SimpleStringProperty();

    model.addListener("Auction", this);
    reset("startAuction");
  }
  public void startAuction()
  {
    System.out.println("2. Start auction in view model");

    errorProperty.set("");
    try
    {
      state.setAuction(model.startAuction(idProperty.get(), titleProperty.get(), descriptionProperty.get(),
          reservePriceProperty.get(), buyoutPriceProperty.get(),
          incrementProperty.get(), timeProperty.get(), pathProperty.get()));
      model.addListener("Time", this);
      model.addListener("End", this);
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
        headerProperty.set("Auction ID:");
        idProperty.set(state.getSelectedAuction().getID());
        titleProperty.set(state.getSelectedAuction().getTitle());
        descriptionProperty.set(state.getSelectedAuction().getDescription());
        reservePriceProperty.set(state.getSelectedAuction().getReservePrice());
        buyoutPriceProperty.set(state.getSelectedAuction().getBuyoutPrice());
        incrementProperty.set(state.getSelectedAuction().getMinimumIncrement());

      }
      //else if(id.equals("startAuction"))
      else
      {
        wipe();
      }

    }
    else wipe();

  }
  private void wipe()
  {
    System.out.println("10. wiped");

    state.setAuction(null);
    headerProperty.set("Start auction");
    idProperty.set(0);
    bidProperty.set(0);
    buyoutPriceProperty.set(0);
    descriptionProperty.set("");
    errorProperty.set("");
    incrementProperty.set(1);
    ratingProperty.set(0);
    reasonProperty.set("");
    reservePriceProperty.set(0);
    timeProperty.set(0);
    titleProperty.set("");
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
        //System.out.println(event.getNewValue());
        break;
      case "End":
        Platform.runLater(()->errorProperty.set("Auction closed."));
        property.firePropertyChange(event);
        model.removeListener("Time", this);
        model.removeListener("End", this);
        break;
      case "Auction":
        //add auction to the list of auctions

    }
  }

  @Override public void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
