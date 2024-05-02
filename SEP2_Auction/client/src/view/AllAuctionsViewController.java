package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import model.Auction;
import model.AuctionList;
import viewmodel.AllAuctionsViewModel;
import viewmodel.ViewModelFactory;

import java.util.ArrayList;

public class AllAuctionsViewController
{
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  private Region root;
  private AllAuctionsViewModel allAuctionsViewModel;
  private ViewHandler viewHandler;
  private ObservableList<Auction> auctionCards;
  private ViewModelFactory viewModelFactory;

  public void init(ViewHandler viewHandler, ViewModelFactory viewModelFactory,
      Region root, String id)
  {
    this.root = root;
    this.viewHandler = viewHandler;
    this.viewModelFactory = viewModelFactory;
    this.allAuctionsViewModel = viewModelFactory.getAllAuctionsViewModel();
    auctionCards = FXCollections.observableArrayList();

    reset(id);
    loadOngoingAuctions();
    //other bindings to be inserted

  }

  public void reset(String id)
  {
    switch (id)
    {
      case "allAuctions":
        auctionCards.clear();
        //loadOngoingAuctions();
        break;
    }
  }

  public Region getRoot()
  {
    return root;
  }

  public void loadOngoingAuctions()
  {
    //TODO: make the view jump back to the beginning of the list when All auctions button is pressed
    auctionCards.clear();
    AuctionList list = allAuctionsViewModel.getOngoingAuctions();
    for (int i = 0; i < list.getSize(); i++)
    {
      auctionCards.add(list.getAuction(i));
    }

    int row = 0;
    int column = 0;

    auctionsGrid.getChildren().clear();
    auctionsGrid.getRowConstraints().clear();
    auctionsGrid.getColumnConstraints().clear();
    {
      for (int i = auctionCards.size() - 1; i >= 0; i--)
      {
        try
        {
          FXMLLoader load = new FXMLLoader();
          load.setLocation(getClass().getResource("AuctionCardView.fxml"));
          Region innerRoot = load.load();
          AuctionCardViewController auctionCardViewController = load.getController();
          auctionCardViewController.init(viewHandler,
              viewModelFactory.getAuctionCardViewModel(), innerRoot);
          auctionCardViewController.setData(auctionCards.get(i));
          if (column == 4)
          {
            column = 0;
            row++;
          }
          auctionsGrid.add(innerRoot, column++, row);
          GridPane.setMargin(innerRoot, new Insets(-1));
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }

}
