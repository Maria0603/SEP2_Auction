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
import java.sql.SQLException;

public class AllAuctionsViewModel implements PropertyChangeListener
{
  private AuctionModel model;
  private ViewModelState state;
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  public AllAuctionsViewModel(AuctionModel model, ViewModelState state)
  {
    this.model = model;
    this.state = state;
    //to add the new auction to the list:
    model.addListener("Auction", this);
    //to remove the closed one from the list:
    model.addListener("End", this);
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

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName())
    {
      case "Auction":

        break;
    }
  }
}
