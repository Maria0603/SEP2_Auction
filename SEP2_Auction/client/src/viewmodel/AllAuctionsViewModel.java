package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import model.Auction;
import model.AuctionList;
import model.AuctionModel;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AllAuctionsViewModel
    implements PropertyChangeListener, NamedPropertyChangeSubject {
  private AuctionModel model;
  private ViewModelState state;
  private PropertyChangeSupport property;
  @FXML
  private ScrollPane allAuctionsScrollPane;
  @FXML
  private GridPane auctionsGrid;

  @FXML
  private StringProperty searchInputField;
  @FXML
  private ObservableList<Auction> auctionCards;

  public AllAuctionsViewModel(AuctionModel model, ViewModelState state) {
    this.model = model;
    this.state = state;

    property = new PropertyChangeSupport(this);

    model.addListener("Auction", this);
    model.addListener("End", this);

    auctionCards = FXCollections.observableArrayList();
    searchInputField = new SimpleStringProperty();
    fillAuctionCardsWithCache();
  }

  private void fillAuctionCardsWithCache() {
    AuctionList list = this.getOngoingAuctions();
    for (int i = 0; i < list.getSize(); i++) {
      auctionCards.add(list.getAuction(i));
    }
  }

  public AuctionList getOngoingAuctions() {
    try {
      return model.getOngoingAuctions();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ObservableList<Auction> searchAuctions() {
    AuctionList upToDateCards = getAuctionListByState();
    String mask = searchInputField.get();

    ObservableList<Auction> result = FXCollections.observableArrayList();

    for (int i = 0; i < upToDateCards.getSize(); i++) {
      Auction auction = upToDateCards.getAuction(i);

      if (auction.isMatchesSearchMask(mask)) {
        result.add(auction);
      }
    }
    return result;
  }

  public AuctionList getPreviousBids() {
    try {
      return model.getPreviousBids(state.getUserEmail());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
  /*
   * public AuctionList getCreatedAuctions()
   * {
   * try
   * {
   * return model.getCreatedAuctions(state.getUserEmail());
   * }
   * catch (SQLException e)
   * {
   * e.printStackTrace();
   * }
   * return null;
   * }
   */

  public void fillAuctionCards() {
    auctionCards.clear();
    AuctionList list = new AuctionList();

    if (state.getAllAuctions()) {
      list = getOngoingAuctions();
      for (int i = 0; i < list.getSize(); i++) {
        auctionCards.add(list.getAuction(i));
      }
    } else if (state.getBids()) {
      list = getPreviousBids();
      for (int i = 0; i < list.getSize(); i++) {
        auctionCards.add(list.getAuction(i));
      }
    }
    /////////////////////////////////////////////////////
    else if (state.getCreatedAuctions()) {
      list = this.getOngoingAuctions();
      for (int i = 0; i < list.getSize(); i++) {
        auctionCards.add(list.getAuction(i));
      }
    }
    //////////////////////////////////////////////////////
  }

  private AuctionList getAuctionListByState(){
    AuctionList list = new AuctionList();
    if (state.getAllAuctions()) {
      list = getOngoingAuctions();

    } else if (state.getBids()) {
      list = getPreviousBids();

    }
    else if (state.getCreatedAuctions()) {
      list = this.getOngoingAuctions();

    }
    return list;
  }



  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "Auction":
        if (state.getAllAuctions()) {
          auctionCards.add((Auction) evt.getNewValue());
        }
        property.firePropertyChange(evt);
        break;
      case "End":
        property.firePropertyChange(evt);
    }
  }

  public ObservableList<Auction> getAuctionCards() {
    return auctionCards;
  }

  public StringProperty getSearchInputField() {
    return searchInputField;
  }

  @Override
  synchronized public void addListener(String propertyName,
      PropertyChangeListener listener) {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override
  public synchronized void removeListener(String propertyName,
      PropertyChangeListener listener) {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
