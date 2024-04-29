package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import model.Auction;
import model.AuctionModel;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AuctionViewModel
    implements PropertyChangeListener, NamedPropertyChangeSubject {
  private StringProperty descriptionProperty, errorProperty, headerProperty, reasonProperty, titleProperty,
      timerProperty;
  private IntegerProperty idProperty, bidProperty, buyoutPriceProperty, incrementProperty, ratingProperty,
      reservePriceProperty, timeProperty;
  private AuctionModel model;
  private ViewModelState state;
  private PropertyChangeSupport property;

  public AuctionViewModel(AuctionModel model, ViewModelState state) {
    property = new PropertyChangeSupport(this);
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

    model.addListener("Auction", this);
    model.addListener("Time", this);
    model.addListener("End", this);
    reset("startAuction");
  }

  public void startAuction(byte[] imageData) {
    errorProperty.set("");
    try {
      state.setAuction(model.startAuction(titleProperty.get().trim(),
          descriptionProperty.get().trim(), reservePriceProperty.get(),
          buyoutPriceProperty.get(), incrementProperty.get(),
          timeProperty.get(), imageData));
    } catch (IllegalArgumentException | SQLException | ClassNotFoundException e) {
      errorProperty.set(e.getMessage());
      titleProperty.set(titleProperty.get().trim());
      descriptionProperty.set(descriptionProperty.get().trim());
    }
  }

  public void reset(String id) {
    Auction selectedAuction = state.getSelectedAuction();
    if (selectedAuction != null && id.equals("displayAuction")) {
      headerProperty.set("Auction ID:");
      idProperty.set(state.getSelectedAuction().getID());
      titleProperty.set(state.getSelectedAuction().getItem().getTitle());
      descriptionProperty.set(state.getSelectedAuction().getItem().getDescription());
      reservePriceProperty.set(state.getSelectedAuction().getPriceConstraint().getReservePrice());
      buyoutPriceProperty.set(state.getSelectedAuction().getPriceConstraint().getBuyoutPrice());
      incrementProperty.set(state.getSelectedAuction().getPriceConstraint().getMinimumIncrement());
    } else {
      wipe();
    }
  }

  private void wipe() {
    state.setAuction(null);
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

  public IntegerProperty getIdProperty() {
    return idProperty;
  }

  public IntegerProperty getRatingProperty() {
    return ratingProperty;
  }

  public StringProperty getReasonProperty() {
    return reasonProperty;
  }

  public IntegerProperty getReservePriceProperty() {
    return reservePriceProperty;
  }

  public IntegerProperty getTimeProperty() {
    return timeProperty;
  }

  public StringProperty getTimerProperty() {
    return timerProperty;
  }

  public StringProperty getTitleProperty() {
    return titleProperty;
  }

  public IntegerProperty getBidProperty() {
    return bidProperty;
  }

  public IntegerProperty getBuyoutPriceProperty() {
    return buyoutPriceProperty;
  }

  public StringProperty getDescriptionProperty() {
    return descriptionProperty;
  }

  public StringProperty getErrorProperty() {
    return errorProperty;
  }

  public StringProperty getHeaderProperty() {
    return headerProperty;
  }

  public IntegerProperty getIncrementProperty() {
    return incrementProperty;
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    switch (event.getPropertyName()) {
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
        property.firePropertyChange(event);
        break;
      case "Auction":
        Auction auction = ((Auction) event.getNewValue());
        // System.out.println((Auction)event.getNewValue());

        Platform.runLater(() -> {
          headerProperty.set("Auction ID:");
          idProperty.set(auction.getID());
          try {
            titleProperty.set(model.getAuction(auction.getID()).getItem().getTitle());
            descriptionProperty.set(
                model.getAuction(auction.getID()).getItem().getDescription());
            reservePriceProperty.set(
                model.getAuction(auction.getID()).getPriceConstraint().getReservePrice());
            buyoutPriceProperty.set(
                model.getAuction(auction.getID()).getPriceConstraint().getBuyoutPrice());
            incrementProperty.set(
                model.getAuction(auction.getID()).getPriceConstraint().getMinimumIncrement());
          } catch (SQLException e) {
            // put it in the error label
            e.printStackTrace();
          }

          // Time end=((Auction) event.getNewValue()).getEnd().getHour()
          // LocalTime timeAuction = LocalTime.ofSecondOfDay(((Auction)
          // event.getNewValue()).getEnd().getHour());
          // timerProperty.set(timeAuction.format(timeFormatter));
          byte[] imageData = ((Auction) event.getNewValue()).getImageData();
          property.firePropertyChange("Auction", null, imageData);
        });

        /*
         * Platform.runLater(() -> {
         * headerProperty.set("Auction ID:");
         * idProperty.set(((Auction) event.getNewValue()).getID());
         * titleProperty.set(((Auction) event.getNewValue()).getItem().getTitle());
         * descriptionProperty.set(
         * ((Auction) event.getNewValue()).getItem().getDescription());
         * reservePriceProperty.set(
         * ((Auction) event.getNewValue()).getPriceConstraint().getReservePrice());
         * buyoutPriceProperty.set(
         * ((Auction) event.getNewValue()).getPriceConstraint().getBuyoutPrice());
         * incrementProperty.set(
         * ((Auction) event.getNewValue()).getPriceConstraint().getMinimumIncrement());
         * 
         * //Time end=((Auction) event.getNewValue()).getEnd().getHour()
         * //LocalTime timeAuction = LocalTime.ofSecondOfDay(((Auction)
         * event.getNewValue()).getEnd().getHour());
         * //timerProperty.set(timeAuction.format(timeFormatter));
         * byte[] imageData = ((Auction) event.getNewValue()).getImageData();
         * property.firePropertyChange("Auction", null, imageData);
         * });
         */
        break;
    }
  }

  @Override
  public void addListener(String propertyName,
      PropertyChangeListener listener) {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override
  public void removeListener(String propertyName,
      PropertyChangeListener listener) {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
