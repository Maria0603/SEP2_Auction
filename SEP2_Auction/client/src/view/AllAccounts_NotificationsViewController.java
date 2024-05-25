

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
  @FXML private TableView<AccountViewModel> accountTableView;
  @FXML private TableColumn<AccountViewModel, String> emailColumn;
  @FXML private TableColumn<AccountViewModel, String> firstNameColumn;
  @FXML private TableColumn<AccountViewModel, String> lastNameColumn;
  @FXML private TableColumn<AccountViewModel, String> phoneColumn;

  @FXML private TableView<NotificationViewModel> notificationsTableView;
  @FXML private TableColumn<NotificationViewModel, String> dateTimeColumn;
  @FXML private TableColumn<NotificationViewModel, String> contentColumn;


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

    initAccountTableView();
    setAccountTableViewData();

    bindValues();
    bindVisibleProperty();
    reset(windowType);
  }

  public void reset(WindowType windowType)
  {
    allAccountsNotificationsViewModel.reset();
    notificationsTableView.getSelectionModel().clearSelection();
    switch (windowType)
    {
      case NOTIFICATIONS -> setForNotifications();
      case ALL_ACCOUNTS -> setForAccounts();
    }
  }

  private void setForNotifications()
  {
    tableViewVBox.getChildren().remove(0);  //  changed from 1 to 0
    setDataForNotificationTable();
    tableViewVBox.getChildren().add(notificationsTableView);
    notificationsTableView.getSelectionModel().selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> allAccountsNotificationsViewModel.setSelectedNotification(
                newVal));
    contentColumn.setPrefWidth(930);
    dateTimeColumn.setPrefWidth(120);
    tableViewVBox.setLayoutX(15);
    //notificationsTableView.setLayoutX(15);
    tableViewVBox.setPrefWidth(1050);
    notificationsTableView.setPrefWidth(1050);
  }

  private void setDataForNotificationTable()
  {
    notificationsTableView.setItems(
        allAccountsNotificationsViewModel.getNotifications());
    dateTimeColumn.setCellValueFactory(
        cellData -> cellData.getValue().getDateTimeProperty());
    contentColumn.setCellValueFactory(
        cellData -> cellData.getValue().getContentProperty());
    allAccountsNotificationsViewModel.setForNotifications();
  }

  private void setForAccounts()
  {
    allAccountsNotificationsViewModel.setForAccounts();

    tableViewVBox.getChildren().remove(0);
    accountTableView.setItems(
        allAccountsNotificationsViewModel.getAllAccounts());
    tableViewVBox.getChildren().add(accountTableView);
    accountTableView.getSelectionModel().selectedItemProperty()
        .addListener(
            (obs, oldVal, newVal) -> allAccountsNotificationsViewModel.setSelectedAccount(
                newVal));
    emailColumn.setPrefWidth(330);
    firstNameColumn.setPrefWidth(210);
    phoneColumn.setPrefWidth(100);
    tableViewVBox.setLayoutX(285);
    tableViewVBox.setPrefWidth(800);
    accountTableView.setPrefWidth(800);
  }

  private void initAccountTableView()
  {
    this.accountTableView = new TableView<>();

    accountTableView.setPrefHeight(640);
    this.emailColumn = new TableColumn<>("Email");
    this.firstNameColumn = new TableColumn<>("First name");
    this.lastNameColumn = new TableColumn<>("Last Name");
    this.phoneColumn=new TableColumn<>("Phone");
    accountTableView.getColumns()
        .addAll(emailColumn, firstNameColumn, lastNameColumn, phoneColumn);

  }

  private void setAccountTableViewData()
  {
    accountTableView.setItems(
        allAccountsNotificationsViewModel.getAllAccounts());
    emailColumn.setCellValueFactory(
        cellData -> cellData.getValue().getEmailProperty());
    firstNameColumn.setCellValueFactory(
        cellData -> cellData.getValue().getFirstNameProperty());
    lastNameColumn.setCellValueFactory(
        cellData -> cellData.getValue().getLastNameProperty());
    phoneColumn.setCellValueFactory(cellData -> cellData.getValue().getPhoneProperty());
  }

  public Region getRoot()
  {
    return root;
  }

  @FXML void searchButtonPressed(ActionEvent event)
  {
    allAccountsNotificationsViewModel.search();
    setAccountTableViewData();
  }

  public void ban_openNotificationButtonPressed()
  {
    allAccountsNotificationsViewModel.ban();
  }

  public void unbanButtonPressed()
  {
    allAccountsNotificationsViewModel.unban();
  }

  private void bindValues()
  {

    dateTimeColumn.textProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getFirstColumnNameProperty());
    contentColumn.textProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getSecondColumnNameProperty());
    errorLabel.textProperty().bindBidirectional(
        this.allAccountsNotificationsViewModel.getErrorProperty());
    searchTextField.textProperty().bindBidirectional(
        this.allAccountsNotificationsViewModel.getSearchFieldProperty());
    reason_contentTextArea.textProperty().bindBidirectional(
        allAccountsNotificationsViewModel.getReasonProperty());
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
    phoneColumn.visibleProperty().bindBidirectional(
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
