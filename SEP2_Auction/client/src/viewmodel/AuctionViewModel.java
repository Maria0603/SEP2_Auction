package viewmodel;

import javafx.beans.property.*;
import model.Auction;
import model.AuctionModel;

public class AuctionViewModel
{
  private StringProperty descriptionProperty, errorProperty, headerProperty, reasonProperty,  titleProperty, pathProperty;
  private IntegerProperty idProperty, bidProperty, buyoutPriceProperty, incrementProperty, ratingProperty, reservePriceProperty, timeProperty;
  private AuctionModel model;
  private ViewModelState state;

  public AuctionViewModel(AuctionModel model, ViewModelState state)
  {
    this.model=model;
    this.state=state;
    idProperty=new SimpleIntegerProperty(model.generateID());
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
    titleProperty=new SimpleStringProperty();
  }
  public void startAuction()
  {
    try
    {
      model.startAuction(idProperty.get(), titleProperty.get(), descriptionProperty.get(),
          reservePriceProperty.get(), buyoutPriceProperty.get(),
          incrementProperty.get(), timeProperty.get(), pathProperty.get());
    }
    catch(IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
  }
  public void reset()
  {
    Auction selectedAuction=state.getSelectedAuction();
    if(selectedAuction!=null)
    {
      if(state.getDisplayAuction())
      {

      }
      else
      {

      }

    }


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
}
