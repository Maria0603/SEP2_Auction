package view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import model.Auction;
import utility.IntStringConverter;
import viewmodel.AuctionCardViewModel;

public class AuctionCardViewController
{
  @FXML private Pane auctionCardPane;
  @FXML private Label currentBidLabel;
  @FXML private Label idLabel;
  @FXML private ImageView imageImageView;
  @FXML private Label timerCountdownLabel;
  @FXML private Label idTextLabel;
  @FXML private Label titleLabel;

  private Region root;
  private ViewHandler viewHandler;
  private AuctionCardViewModel auctionCardViewModel;

  public void init(ViewHandler viewHandler, AuctionCardViewModel auctionCardViewModel, Region root)
  {
    this.viewHandler=viewHandler;
    this.root=root;
    this.auctionCardViewModel=auctionCardViewModel;
    Bindings.bindBidirectional(idLabel.textProperty(), auctionCardViewModel.getIdProperty(), new IntStringConverter());
    Bindings.bindBidirectional(currentBidLabel.textProperty(), auctionCardViewModel.getCurrentBidProperty(), new IntStringConverter());
    timerCountdownLabel.textProperty().bindBidirectional(auctionCardViewModel.getTimerCountdownProperty());
    titleLabel.textProperty().bindBidirectional(auctionCardViewModel.getTitleProperty());
  }
  public void setData(Auction auction)
  {
    auctionCardViewModel.reset(auction);
  }

  @FXML public void cardSelected(MouseEvent mouseEvent)
  {
    //TODO: to extract the id in the auction view model
    viewHandler.openView("displayAuction"+idLabel.getText());
  }
}
