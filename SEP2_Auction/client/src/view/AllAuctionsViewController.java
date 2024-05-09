package view;

import javafx.collections.FXCollections;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import model.Auction;
import viewmodel.AllAuctionsViewModel;
import viewmodel.ViewModelFactory;
import javafx.geometry.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class AllAuctionsViewController implements PropertyChangeListener {
  @FXML private ScrollPane allAuctionsScrollPane;
  @FXML private GridPane auctionsGrid;

  private final int NUMBER_OF_COLUMNS = 4;

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

    allAuctionsViewModel.addListener("Auction", this);
    allAuctionsViewModel.addListener("End", this);

    reset(windowType);
    loadAuctions();
    //other bindings to be inserted

  }

  public void reset(WindowType type) {
    switch (type) {
      case ALL_AUCTIONS, BIDS -> {
        auctionCards.clear();
        //loadOngoingAuctions();
      }

    }
  }

  public Region getRoot() {
    return root;
  }

  public void loadAuctions() {
    clearGrid();
    try {

      int totalElements = auctionCards.size();
      int numRows = (totalElements + NUMBER_OF_COLUMNS - 1) / NUMBER_OF_COLUMNS;

      int listIndex = auctionCards.size() - 1;

      for (int row = 0; row < numRows; row++) {
        for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {

          if(listIndex >= 0){

            addNewCardToGrid(auctionCards.get(listIndex), column, row);
            listIndex--;
          }
        }
      }
    }
    catch (IOException e) {
      ///
    }
  }

  private void addNewCardToGrid(Auction auction, int column, int row)
      throws IOException {

    AuctionCardViewController newCardController = initNewCard();
    newCardController.setData(auction);

    auctionsGrid.add(newCardController.getRoot(), column, row);
    GridPane.setMargin(newCardController.getRoot(), new Insets(-1));

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

  private int getNumberOfRowsInGrid(){
    int totalElements = auctionCards.size();
    return (totalElements + NUMBER_OF_COLUMNS - 1) / NUMBER_OF_COLUMNS;
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "Auction" -> {
        ///here we can trigger grid to add new card to the tail
      }
      case "End" -> {
      }
    }
  }
}
