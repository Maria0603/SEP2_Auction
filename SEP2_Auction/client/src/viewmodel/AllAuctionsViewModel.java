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
import java.util.ArrayList;

public class AllAuctionsViewModel implements PropertyChangeListener,
    NamedPropertyChangeSubject
{
  private AuctionModel model;
  private ViewModelState state;
  private PropertyChangeSupport property;
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  @FXML private StringProperty searchInputField;
  @FXML private ObservableList<Auction> auctionCards;

  public AllAuctionsViewModel(AuctionModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;

    property = new PropertyChangeSupport(this);

    model.addListener("Auction", this);
    model.addListener("End", this);

    auctionCards = FXCollections.observableArrayList();
    searchInputField = new SimpleStringProperty();
    fillAuctionCardsWithCache();
  }

  private void fillAuctionCardsWithCache(){
    AuctionList list = this.getOngoingAuctions();
    for (int i = 0; i < list.getSize(); i++)
    {
      auctionCards.add(list.getAuction(i));
    }
  }

  public AuctionList getOngoingAuctions()
  {
    try
    {
      return model.getOngoingAuctions();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList<Auction> searchAuctions(){
    AuctionList list = this.getOngoingAuctions();
    ArrayList<Auction> result = new ArrayList<>();
    String mask = searchInputField.get();

    for (int i = 0; i < list.getSize(); i++)
    {
      Auction auction = list.getAuction(i);
      System.out.println(auction);
      if(auction.isMatchesSearchMask(mask)){
        result.add(auction);
      }
    }
    return result;
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName()) {
      case "Auction":
        auctionCards.add((Auction) evt.getNewValue());
        property.firePropertyChange(evt);
        break;
      case "End":
        property.firePropertyChange(evt);
    }
  }

  public ObservableList<Auction> getAuctionCards() {
    return auctionCards;
  }

  public StringProperty getSearchInputField(){return searchInputField;}

  @Override synchronized public void addListener(String propertyName,
      PropertyChangeListener listener) {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void removeListener(String propertyName,
      PropertyChangeListener listener) {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
