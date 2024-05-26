package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import model.domain.Auction;
import model.domain.AuctionList;
import model.AuctionListModel;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AllAuctionsViewModel
    implements PropertyChangeListener, NamedPropertyChangeSubject
{
  private AuctionListModel model;
  private ViewModelState state;
  private PropertyChangeSupport property;
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  @FXML private StringProperty searchInputField;
  @FXML private ObservableList<Auction> auctionCards;

  public AllAuctionsViewModel(AuctionListModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;

    property = new PropertyChangeSupport(this);

    model.addListener("Auction", this);
    model.addListener("End", this);

    auctionCards = FXCollections.observableArrayList();
    searchInputField = new SimpleStringProperty();
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

  public AuctionList getAllAuctions()
  {
    try
    {
      return model.getAllAuctions(state.getUserEmail());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public AuctionList getCreatedAuctions()
  {
    try
    {
      return model.getCreatedAuctions(state.getUserEmail());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public ObservableList<Auction> searchAuctions()
  {
    AuctionList upToDateCards = getAuctionListByState();
    String mask = searchInputField.get();
    if (mask == null || mask.isEmpty())
      return null;

    ObservableList<Auction> result = FXCollections.observableArrayList();

    for (int i = 0; i < upToDateCards.getSize(); i++)
    {
      Auction auction = upToDateCards.getAuction(i);

      if (auction.isMatchesSearchMask(mask))
      {
        result.add(auction);
      }
    }
    return result;
  }

  public AuctionList getPreviousBids()
  {
    try
    {
      return model.getPreviousBids(state.getUserEmail());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public void fillAuctionCards()
  {
    auctionCards.clear();
    AuctionList list=getAuctionListByState();
    if(list!=null)
    {
      for (int i = 0; i < list.getSize(); i++)
      {
        auctionCards.add(list.getAuction(i));
      }
    }
  }

  private AuctionList getAuctionListByState()
  {
    AuctionList list = new AuctionList();
    if (state.getAllAuctions())
    {
      if (state.isModerator())
        list = getAllAuctions();
      else
        list = getOngoingAuctions();
    }
    else if (state.getBids())
    {
      list = getPreviousBids();

    }
    else if (state.getCreatedAuctions())
    {
      list = getCreatedAuctions();
    }
    return list;
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName())
    {
      case "Auction":
        if (state.getAllAuctions())
          auctionCards.add((Auction) evt.getNewValue());
        property.firePropertyChange(evt);
        break;
      case "End":
        property.firePropertyChange(evt);
    }
  }

  public ObservableList<Auction> getAuctionCards()
  {
    return auctionCards;
  }

  public StringProperty getSearchInputField()
  {
    return searchInputField;
  }

  @Override synchronized public void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
