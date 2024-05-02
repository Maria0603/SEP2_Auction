package view;

import javafx.collections.FXCollections;
import javafx.beans.binding.Bindings;
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

import java.io.IOException;

public class AllAuctionsViewController {
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  private Region root;
  private AllAuctionsViewModel allAuctionsViewModel;
  private ViewHandler viewHandler;
  private ObservableList<Auction> auctionCards;
  private ViewModelFactory viewModelFactory;

  public void init(ViewHandler viewHandler, ViewModelFactory viewModelFactory,
      Region root, WindowType windowType) {
    this.root = root;
    this.viewHandler = viewHandler;
    this.viewModelFactory = viewModelFactory;
    this.allAuctionsViewModel = viewModelFactory.getAllAuctionsViewModel();

    auctionCards = FXCollections.observableArrayList();
    Bindings.bindContent(auctionCards, allAuctionsViewModel.getAuctionCards());

    reset(windowType);
    loadOngoingAuctions();
    //other bindings to be inserted

  }

  public void reset(WindowType type) {
    switch (type) {
      case ALL_AUCTIONS -> {
        auctionCards.clear();
        //loadOngoingAuctions();
      }

    }
  }

  public Region getRoot() {
    return root;
  }

  public void loadOngoingAuctions() {
    clearGrid();

    try {

      int totalElements = auctionCards.size();
      int numColumns = 4;
      int numRows = (totalElements + numColumns - 1) / numColumns;

      int listIndex = auctionCards.size() - 1;

      for (int row = 0; row < numRows; row++) {
        for (int column = 0; column < numColumns; column++) {

          if(listIndex >= 0){

            AuctionCardViewController newCardController = initNewCard();
            newCardController.setData(auctionCards.get(listIndex));

            auctionsGrid.add(newCardController.getRoot(), column, row);
            GridPane.setMargin(newCardController.getRoot(), new Insets(-1));

            listIndex--;
          } else {
           ///
          }

        }
      }
    }
    catch (IOException e) {
      ///
    }
  }

  private AuctionCardViewController initNewCard() throws IOException {
    FXMLLoader load = new FXMLLoader();
    load.setLocation(getClass().getResource("AuctionCardView.fxml"));
    Region innerRoot = load.load();
    AuctionCardViewController auctionCardViewController = load.getController();
    auctionCardViewController.init(viewHandler,
        viewModelFactory.getAuctionCardViewModel(), innerRoot);
    return auctionCardViewController;
  }

  private void clearGrid() {
    auctionsGrid.getChildren().clear();
    auctionsGrid.getRowConstraints().clear();
    auctionsGrid.getColumnConstraints().clear();
  }

}
