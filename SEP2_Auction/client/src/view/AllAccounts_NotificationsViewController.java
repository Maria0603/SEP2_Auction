package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import model.AuctionModel;
import model.Notification;
import viewmodel.AllAccounts_NotificationsViewModel;
import viewmodel.AuctionViewModel;
import viewmodel.NotificationViewModel;

public class AllAccounts_NotificationsViewController
{
  @FXML private Label errorLabel;
  @FXML private TableView<NotificationViewModel> notifications_accountsTableView;
  @FXML private Button ban_openNotificationButton;
  @FXML private Pane ban_unban_notificationsPane;
  @FXML private TableColumn<NotificationViewModel, String> emailColumn;
  @FXML private TableColumn<NotificationViewModel, String> firstName_dateTimeColumn;
  @FXML private TableColumn<NotificationViewModel, String> lastName_contentColumn;
  @FXML private TableColumn<NotificationViewModel, String> ratingColumn;
  @FXML private Label reason_contentLabel;
  @FXML private TextArea reason_contentTextArea;
  @FXML private Button searchButton;
  @FXML private TextField searchTextField;
  @FXML private Button unbanButton;
  private Region root;
  private AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel;
  private ViewHandler viewHandler;

  public void init(ViewHandler viewHandler,
      AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel,
      Region root, WindowType windowType)
  {
    this.root = root;
    this.allAccountsNotificationsViewModel = allAccountsNotificationsViewModel;
    this.viewHandler = viewHandler;
    errorLabel.textProperty().bindBidirectional(
        this.allAccountsNotificationsViewModel.getErrorProperty());
    reset(windowType);
    notifications_accountsTableView.getSelectionModel().selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> allAccountsNotificationsViewModel.setSelected(
                newVal));
    //visibility controlled from the view model
    ban_openNotificationButton.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());
    unbanButton.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());
    emailColumn.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());
    ratingColumn.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());
    reason_contentLabel.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());
    reason_contentTextArea.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());
    searchButton.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());
    searchTextField.visibleProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getAllFieldsVisibility());

    firstName_dateTimeColumn.textProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getFirstColumnNameProperty());
    lastName_contentColumn.textProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getSecondColumnNameProperty());
  }

  public void reset(WindowType windowType)
  {
    allAccountsNotificationsViewModel.reset();
    notifications_accountsTableView.getSelectionModel().clearSelection();
    switch (windowType)
    {
      case NOTIFICATIONS:
        setForNotifications();
        break;
      //case ACCOUNTS:
      //setForAccounts();
      //break;
    }
  }

  private void setForNotifications()
  {
    notifications_accountsTableView.setItems(
        allAccountsNotificationsViewModel.getNotifications());
    firstName_dateTimeColumn.setCellValueFactory(
        cellData -> cellData.getValue().getDateTimeProperty());
    lastName_contentColumn.setPrefWidth(865);
    lastName_contentColumn.setCellValueFactory(
        cellData -> cellData.getValue().getContentProperty());
    allAccountsNotificationsViewModel.setForNotifications();

    notifications_accountsTableView.setLayoutX(15);
    notifications_accountsTableView.setPrefWidth(1050);
  }

  private void setForAccounts()
  {
    //take a look here:
    allAccountsNotificationsViewModel.setForAccounts();
  }

  public Region getRoot()
  {
    return root;
  }

  @FXML void searchButtonPressed(ActionEvent event)
  {

  }

  @FXML void searchField(ActionEvent event)
  {

  }

  public void ban_openNotificationButtonPressed(ActionEvent actionEvent)
  {
  }

  public void unbanButtonPressed(ActionEvent actionEvent)
  {
  }
}