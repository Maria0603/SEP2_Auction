package viewmodel;

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
    implements PropertyChangeListener, NamedPropertyChangeSubject
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
  public AuctionList getAllAuctions()
  {
    try
    {
      return model.getAllAuctions();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return null;
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

  public AuctionList getCreatedAuctions()
  {
    try
    {
      System.out.println("created auctions from" + state.getUserEmail());
      return model.getCreatedAuctions(state.getUserEmail());
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
    AuctionList list;
    if (state.getAllAuctions())
    {
      if(state.isModerator())
        list=getAllAuctions();
      else
        list = getOngoingAuctions();
      if (list != null)
      {
        for (int i = 0; i < list.getSize(); i++)
        {
          auctionCards.add(list.getAuction(i));
        }
      }
    }
    else if (state.getBids())
    {
      list = getPreviousBids();
      if (list != null)
      {
        for (int i = 0; i < list.getSize(); i++)
        {
          auctionCards.add(list.getAuction(i));
        }
      }
    }
    else if (state.getCreatedAuctions())
    {
      list = getCreatedAuctions();
      if (list != null)
      {
        for (int i = 0; i < list.getSize(); i++)
        {
          auctionCards.add(list.getAuction(i));
        }
      }
    }
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
