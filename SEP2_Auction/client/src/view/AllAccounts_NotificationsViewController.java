

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
import javafx.scene.layout.VBox;
import viewmodel.*;

public class AllAccounts_NotificationsViewController
{
  @FXML private Label errorLabel;

  @FXML public VBox tableViewVBox;
  @FXML private TableView<NotificationViewModel> notificationsTableView;
  @FXML private TableColumn<NotificationViewModel, String> emailColumn;
  @FXML private TableColumn<NotificationViewModel, String> dateTimeColumn;
  @FXML private TableColumn<NotificationViewModel, String> contentColumn;
  @FXML private TableColumn<NotificationViewModel, String> ratingColumn;

  @FXML private Button ban_openNotificationButton;
  @FXML private Pane ban_unban_notificationsPane;
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
    bindValues();
    bindVisibleProperty();
    reset(windowType);
  }

  public void reset(WindowType windowType)
  {
    allAccountsNotificationsViewModel.reset();
    notificationsTableView.getSelectionModel().clearSelection();
    switch (windowType) {
      case NOTIFICATIONS -> setForNotifications();
      case ALL_ACCOUNTS -> setForAccounts();
    }
  }

  private void setForNotifications()
  {
    tableViewVBox.getChildren().remove(1);
    setDataForNotificationTable();
    tableViewVBox.getChildren().add(notificationsTableView);
  }

  private void setDataForNotificationTable(){
    notificationsTableView.setItems(
        allAccountsNotificationsViewModel.getNotifications());
    dateTimeColumn.setCellValueFactory(
        cellData -> cellData.getValue().getDateTimeProperty());
    contentColumn.setPrefWidth(865);
    contentColumn.setCellValueFactory(
        cellData -> cellData.getValue().getContentProperty());
    allAccountsNotificationsViewModel.setForNotifications();

    notificationsTableView.setLayoutX(15);
    notificationsTableView.setPrefWidth(1050);
  }

  private void setForAccounts()
  {
    allAccountsNotificationsViewModel.setForAccounts();
    TableView<AccountViewModel> accountTableView = createAccountTableView();

    tableViewVBox.getChildren().remove(0);
    accountTableView.setItems(allAccountsNotificationsViewModel.getAllAccounts());
    tableViewVBox.getChildren().add(accountTableView);
  }
  

  private TableView<AccountViewModel> createAccountTableView() {
    TableView<AccountViewModel> tableView = new TableView<>();

    TableColumn<AccountViewModel, String> emailColumn = new TableColumn<>("Email");
    emailColumn.setCellValueFactory(cellData -> cellData.getValue().getEmailProperty());
    emailColumn.setPrefWidth(181);

    TableColumn<AccountViewModel, String> firstNameColumn = new TableColumn<>("First Name");
    firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFirstNameProperty());
    firstNameColumn.setPrefWidth(231);

    TableColumn<AccountViewModel, String> lastNameColumn = new TableColumn<>("Last Name");
    lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getLastNameProperty());
    emailColumn.setPrefWidth(390);

    tableView.getColumns().addAll(emailColumn, firstNameColumn, lastNameColumn);

    return tableView;
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

  private void bindValues()
  {
    notificationsTableView.getSelectionModel().selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> allAccountsNotificationsViewModel.setSelected(
                newVal));

    dateTimeColumn.textProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getFirstColumnNameProperty());
    contentColumn.textProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getSecondColumnNameProperty());
    errorLabel.textProperty().bindBidirectional(
        this.allAccountsNotificationsViewModel.getErrorProperty());
  }

  private void bindVisibleProperty()
  {
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
  }
}
