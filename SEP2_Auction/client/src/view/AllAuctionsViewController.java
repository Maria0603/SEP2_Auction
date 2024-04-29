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

public class AllAuctionsViewController
{
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  private Region root;
  private AllAuctionsViewModel allAuctionsViewModel;
  private ViewHandler viewHandler;
  private ObservableList<Auction> auctionCards;
  private AuctionCardViewController auctionCardViewController;
  private ViewModelFactory viewModelFactory;

  public void init(ViewHandler viewHandler, ViewModelFactory viewModelFactory, Region root, String id)
  {
    this.root = root;
    this.viewHandler = viewHandler;
    this.viewModelFactory=viewModelFactory;
    this.allAuctionsViewModel=viewModelFactory.getAllAuctionsViewModel();
    auctionCards = FXCollections.observableArrayList();


    auctionCards.clear();
    AuctionList list=allAuctionsViewModel.getOngoingAuctions();
    for(int i=0; i<list.getSize(); i++)
    {
      auctionCards.add(list.getAuction(i));
    }

    int row = 0;
    int column = 0;

    auctionsGrid.getChildren().clear();
    auctionsGrid.getRowConstraints().clear();
    auctionsGrid.getColumnConstraints().clear();
    //if(auctionCardViewController==null)
    {
    for (int q = 0; q < auctionCards.size(); q++)
    {
      try
      {
        FXMLLoader load = new FXMLLoader();
        load.setLocation(getClass().getResource("AuctionCardView.fxml"));
        //AnchorPane pane = load.load();
        Region root1 = load.load();

        AuctionCardViewController auctionCardViewController = load.getController();

        auctionCardViewController.init(viewHandler, viewModelFactory.getAuctionCardViewModel(), root);

        auctionCardViewController.setData(auctionCards.get(q));


        if (column == 4)
        {
          column = 0;
          row += 1;
        }

        auctionsGrid.add(root1, column++, row);

        GridPane.setMargin(root1, new Insets(-1));

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    }


    //other bindings to be inserted
    //auctionViewModel.addListener(this);
    reset(id);
  }


  public void reset(String id)
  {
    allAuctionsViewModel.reset(id);
    auctionCards.clear();
    //auctionCards.add(null);
  }

  public Region getRoot()
  {
    return root;
  }

  private void loadFromModel()
  {
    //for(int i=0; i<allAuctionsViewModel.getAllAuctions().getSize(); i++)
     auctionCards.add(null);
  }

}
