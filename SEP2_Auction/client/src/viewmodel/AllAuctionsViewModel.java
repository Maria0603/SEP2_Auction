package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import model.Auction;
import model.AuctionList;
import model.AuctionModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AllAuctionsViewModel implements PropertyChangeListener
{
  private AuctionModel model;
  private ViewModelState state;
  private PropertyChangeSupport property;
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  @FXML private ObservableList<Auction> auctionCards;

  public AllAuctionsViewModel(AuctionModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;

    property = new PropertyChangeSupport(this);

    model.addListener("Auction", this);
    model.addListener("End", this);

    auctionCards = FXCollections.observableArrayList();
    fillAuctionCards();
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

  private void fillAuctionCards(){
    AuctionList list = this.getOngoingAuctions();
    for (int i = 0; i < list.getSize(); i++)
    {
      auctionCards.add(list.getAuction(i));
    }
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName()) {
      case "Auction":
        System.out.println("all auctions view model received event");
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
}
