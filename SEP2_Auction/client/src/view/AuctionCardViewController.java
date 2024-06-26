package view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import model.domain.Auction;
import utility.IntStringConverter;
import viewmodel.AuctionCardViewModel;

/**
 * The AuctionCardViewController class is responsible for controlling the view
 * of an individual auction card. It handles user interactions and updates the view
 * based on data from the view model.
 */
public class AuctionCardViewController {

  @FXML private Label currentBidLabel;
  @FXML private Label idLabel;
  @FXML private ImageView imageImageView;
  @FXML private Label endTimeLabel;
  @FXML private Label titleLabel;

  private Region root;
  private ViewHandler viewHandler;
  private AuctionCardViewModel auctionCardViewModel;

  /**
   * Initializes the view controller with the provided view handler, view model, and root region.
   *
   * @param viewHandler the view handler for managing views.
   * @param auctionCardViewModel the view model for managing auction card data.
   * @param root the root region of the view.
   */
  public void init(ViewHandler viewHandler, AuctionCardViewModel auctionCardViewModel, Region root) {
    this.viewHandler = viewHandler;
    this.root = root;
    this.auctionCardViewModel = auctionCardViewModel;
    bindValues();
  }

  /**
   * Sets the data for the auction card.
   *
   * @param auction the auction data to set.
   */
  public void setData(Auction auction) {
    auctionCardViewModel.setData(auction);
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
   * Handles the action when the auction card is selected.
   */
  @FXML public void cardSelected() {
    auctionCardViewModel.cardSelected();
    viewHandler.openView(WindowType.DISPLAY_AUCTION);
  }

  /**
   * Binds the values from the view model to the view components.
   */
  private void bindValues() {
    Bindings.bindBidirectional(idLabel.textProperty(), auctionCardViewModel.getIdProperty(), new IntStringConverter());
    Bindings.bindBidirectional(currentBidLabel.textProperty(), auctionCardViewModel.getCurrentBidProperty(), new IntStringConverter());
    endTimeLabel.textProperty().bindBidirectional(auctionCardViewModel.getTimerCountdownProperty());
    titleLabel.textProperty().bindBidirectional(auctionCardViewModel.getTitleProperty());
    Bindings.bindBidirectional(imageImageView.imageProperty(), auctionCardViewModel.getImageProperty());
  }
}
