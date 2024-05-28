package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import model.domain.Auction;
import model.domain.AuctionList;
import model.AuctionListModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

public class AllAuctionsViewModel implements PropertyChangeListener
{
  private final AuctionListModel model;
  private final ViewModelState state;

  @FXML private StringProperty searchInputField;
  @FXML private ObservableList<Auction> auctionCards;

  public AllAuctionsViewModel(AuctionListModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;

    model.addListener("Auction", this);

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
    catch (IllegalArgumentException e)
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
    catch (IllegalArgumentException e)
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
    catch (IllegalArgumentException e)
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
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public void fillAuctionCards()
  {
    auctionCards.clear();
    AuctionList list = getAuctionListByState();
    if (list != null)
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

  public ObservableList<Auction> getAuctionCards()
  {
    return auctionCards;
  }

  public StringProperty getSearchInputField()
  {
    return searchInputField;
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("Auction"))
    {
      if (state.getAllAuctions())
        auctionCards.add((Auction) evt.getNewValue());
    }
  }

}
