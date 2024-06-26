package view;

import javafx.collections.FXCollections;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import model.domain.Auction;
import viewmodel.AllAuctionsViewModel;
import viewmodel.ViewModelFactory;
import javafx.geometry.Insets;

import java.io.IOException;

/**
 * The AllAuctionsViewController class is responsible for controlling the view
 * that displays all auctions in a grid format. It handles user interactions
 * and updates the view based on data from the view model.
 */
public class AllAuctionsViewController {

  @FXML private GridPane auctionsGrid;
  @FXML public TextField searchInputField;

  private final int NUMBER_OF_COLUMNS = 4;

  private Region root;
  private AllAuctionsViewModel allAuctionsViewModel;
  private ViewHandler viewHandler;
  private ObservableList<Auction> auctionCards;
  private ViewModelFactory viewModelFactory;

  /**
   * Initializes the view controller with the provided view handler, view model factory, and root region.
   *
   * @param viewHandler the view handler for managing views.
   * @param viewModelFactory the factory for creating view models.
   * @param root the root region of the view.
   * @param windowType the type of window to initialize.
   */
  public void init(ViewHandler viewHandler, ViewModelFactory viewModelFactory, Region root, WindowType windowType) {
    this.root = root;
    this.viewHandler = viewHandler;
    this.viewModelFactory = viewModelFactory;
    this.allAuctionsViewModel = viewModelFactory.getAllAuctionsViewModel();

    auctionCards = FXCollections.observableArrayList();
    Bindings.bindContent(auctionCards, this.allAuctionsViewModel.getAuctionCards());

    searchInputField.textProperty().bindBidirectional(this.allAuctionsViewModel.getSearchInputField());

    reset(windowType);
  }

  /**
   * Resets the view based on the specified window type.
   *
   * @param type the type of window to reset.
   */
  public void reset(WindowType type) {
    switch (type) {
      case ALL_AUCTIONS, MY_BIDS, MY_AUCTIONS -> loadAuctions();
    }
  }

  /**
   * Gets the root region of the view.
   *
   * @return the root region.
   */
  public Region getRoot() {
    return root;
  }

  /**
   * Loads the auctions into the view.
   */
  public void loadAuctions() {
    allAuctionsViewModel.fillAuctionCards();
    clearGrid();
    renderGridWithCards(this.auctionCards);
  }

  /**
   * Renders the grid with auction cards.
   *
   * @param auctionCards the list of auction cards to render.
   */
  private void renderGridWithCards(ObservableList<Auction> auctionCards) {
    try {
      int totalElements = auctionCards.size();
      int numRows = (totalElements + NUMBER_OF_COLUMNS - 1) / NUMBER_OF_COLUMNS;

      int listIndex = auctionCards.size() - 1;

      for (int row = 0; row < numRows; row++) {
        for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
          if (listIndex >= 0) {
            addNewCardToGrid(auctionCards.get(listIndex), column, row);
            listIndex--;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds a new auction card to the grid at the specified column and row.
   *
   * @param auction the auction to add.
   * @param column the column index.
   * @param row the row index.
   * @throws IOException if an I/O error occurs.
   */
  private void addNewCardToGrid(Auction auction, int column, int row) throws IOException {
    AuctionCardViewController newCardController = initNewCard();
    newCardController.setData(auction);

    auctionsGrid.add(newCardController.getRoot(), column, row);
    GridPane.setMargin(newCardController.getRoot(), new Insets(-1));
  }

  /**
   * Initializes a new auction card.
   *
   * @return the controller for the new auction card.
   * @throws IOException if an I/O error occurs.
   */
  private AuctionCardViewController initNewCard() throws IOException {
    FXMLLoader load = new FXMLLoader();
    load.setLocation(getClass().getResource("AuctionCardView.fxml"));

    Region innerRoot = load.load();
    AuctionCardViewController auctionCardViewController = load.getController();

    auctionCardViewController.init(viewHandler, viewModelFactory.getAuctionCardViewModel(), innerRoot);

    return auctionCardViewController;
  }

  /**
   * Clears the grid of all children, row constraints, and column constraints.
   */
  private void clearGrid() {
    auctionsGrid.getChildren().clear();
    auctionsGrid.getRowConstraints().clear();
    auctionsGrid.getColumnConstraints().clear();
  }

  /**
   * Handles the action of the search button being pressed.
   *
   * @throws IOException if an I/O error occurs.
   */
  @FXML public void searchPressed() throws IOException {
    clearGrid();
    ObservableList<Auction> searchResults = allAuctionsViewModel.searchAuctions();
    renderGridWithCards(searchResults);
  }
}
