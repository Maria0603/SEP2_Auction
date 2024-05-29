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

/**
 * The AllAuctionsViewModel class is responsible for managing the data and actions
 * related to displaying all auctions in the view.
 */
public class AllAuctionsViewModel implements PropertyChangeListener
{
  private final AuctionListModel model;
  private final ViewModelState state;

  @FXML private StringProperty searchInputField;
  @FXML private ObservableList<Auction> auctionCards;

  /**
   * Constructs an AllAuctionsViewModel with the specified model and view model state.
   *
   * @param model the auction list model
   * @param state the view model state
   */
  public AllAuctionsViewModel(AuctionListModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;

    model.addListener("Auction", this);

    auctionCards = FXCollections.observableArrayList();
    searchInputField = new SimpleStringProperty();
    fillAuctionCards();
  }

  /**
   * Gets the list of ongoing auctions.
   *
   * @return the list of ongoing auctions
   */
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

  /**
   * Gets the list of all auctions.
   *
   * @return the list of all auctions
   */
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

  /**
   * Gets the list of auctions created by the user.
   *
   * @return the list of created auctions
   */
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

  /**
   * Searches for auctions that match the search input field.
   *
   * @return the list of matching auctions
   */
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

  /**
   * Gets the list of previous bids made by the user.
   *
   * @return the list of previous bids
   */
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

  /**
   * Fills the auction cards list based on the current state.
   */
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

  /**
   * Gets the auction list based on the current state.
   *
   * @return the auction list
   */
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

  /**
   * Gets the list of auction cards.
   *
   * @return the list of auction cards
   */
  public ObservableList<Auction> getAuctionCards()
  {
    return auctionCards;
  }

  /**
   * Gets the search input field property.
   *
   * @return the search input field property
   */
  public StringProperty getSearchInputField()
  {
    return searchInputField;
  }

  /**
   * Handles property change events for auctions.
   *
   * @param evt the property change event
   */
  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("Auction"))
    {
      if (state.getAllAuctions())
        auctionCards.add((Auction) evt.getNewValue());
    }
  }
}
