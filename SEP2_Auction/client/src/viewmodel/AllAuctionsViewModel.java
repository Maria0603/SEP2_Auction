package viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import model.Auction;
import model.AuctionList;
import model.AuctionModel;

import java.sql.SQLException;

public class AllAuctionsViewModel
{
  private AuctionModel model;
  private ViewModelState state;
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;
  public AllAuctionsViewModel(AuctionModel model, ViewModelState state)
  {
    this.model=model;
    this.state=state;
  }
  public void reset(String id)
  {
    switch (id)
    {
      case "allAuctions":
        //nothing to do here
      break;
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
}
