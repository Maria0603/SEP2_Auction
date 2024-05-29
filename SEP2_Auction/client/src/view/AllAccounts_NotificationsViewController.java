package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import viewmodel.*;

/**
 * The AllAccounts_NotificationsViewController class is responsible for controlling
 * the view for displaying and managing user accounts and notifications.
 * It handles user interactions and updates the view based on data from the view model.
 */
public class AllAccounts_NotificationsViewController {

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

  @FXML private Button banButton;
  @FXML private Label reasonLabel;
  @FXML private TextArea reasonTextArea;
  @FXML private Button searchButton;
  @FXML private TextField searchTextField;
  @FXML private Button unbanButton;

  private Region root;
  private AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel;

  /**
   * Initializes the view controller with the provided view model and root region.
   *
   * @param allAccountsNotificationsViewModel the view model for managing account and notification data.
   * @param root the root region of the view.
   * @param windowType the type of window to initialize.
   */
  public void init(AllAccounts_NotificationsViewModel allAccountsNotificationsViewModel, Region root, WindowType windowType) {
    this.root = root;
    this.allAccountsNotificationsViewModel = allAccountsNotificationsViewModel;

    initAccountTableView();

    bindValues();
    bindVisibleProperty();
    reset(windowType);
  }

  /**
   * Resets the view based on the specified window type.
   *
   * @param windowType the type of window to reset.
   */
  public void reset(WindowType windowType) {
    allAccountsNotificationsViewModel.reset();
    notificationsTableView.getSelectionModel().clearSelection();
    switch (windowType) {
      case NOTIFICATIONS -> setForNotifications();
      case ALL_ACCOUNTS -> setForAccounts();
    }
  }

  /**
   * Configures the view for displaying notifications.
   */
  private void setForNotifications() {
    tableViewVBox.getChildren().remove(0);
    setDataForNotificationTable();
    tableViewVBox.getChildren().add(notificationsTableView);
    notificationsTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> allAccountsNotificationsViewModel.setSelectedNotification(newVal));
    contentColumn.setPrefWidth(930);
    dateTimeColumn.setPrefWidth(120);
    tableViewVBox.setLayoutX(15);
    tableViewVBox.setPrefWidth(1050);
    notificationsTableView.setPrefWidth(1050);
  }

  /**
   * Sets the data for the notification table.
   */
  private void setDataForNotificationTable() {
    notificationsTableView.setItems(allAccountsNotificationsViewModel.getNotifications());
    dateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getDateTimeProperty());
    contentColumn.setCellValueFactory(cellData -> cellData.getValue().getContentProperty());
    allAccountsNotificationsViewModel.setForNotifications();
  }

  /**
   * Configures the view for displaying accounts.
   */
  private void setForAccounts() {
    setAccountTableViewData();
    allAccountsNotificationsViewModel.setForAccounts();

    tableViewVBox.getChildren().remove(0);
    accountTableView.setItems(allAccountsNotificationsViewModel.getAllAccounts());
    tableViewVBox.getChildren().add(accountTableView);
    accountTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> allAccountsNotificationsViewModel.setSelectedAccount(newVal));
    emailColumn.setPrefWidth(330);
    firstNameColumn.setPrefWidth(210);
    phoneColumn.setPrefWidth(100);
    tableViewVBox.setLayoutX(285);
    tableViewVBox.setPrefWidth(800);
    accountTableView.setPrefWidth(800);
  }

  /**
   * Initializes the account table view.
   */
  private void initAccountTableView() {
    this.accountTableView = new TableView<>();

    accountTableView.setPrefHeight(640);
    this.emailColumn = new TableColumn<>("Email");
    this.firstNameColumn = new TableColumn<>("First name");
    this.lastNameColumn = new TableColumn<>("Last Name");
    this.phoneColumn = new TableColumn<>("Phone");
    accountTableView.getColumns().addAll(emailColumn, firstNameColumn, lastNameColumn, phoneColumn);
  }

  /**
   * Sets the data for the account table view.
   */
  private void setAccountTableViewData() {
    accountTableView.setItems(allAccountsNotificationsViewModel.getAllAccounts());
    emailColumn.setCellValueFactory(cellData -> cellData.getValue().getEmailProperty());
    firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFirstNameProperty());
    lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().getLastNameProperty());
    phoneColumn.setCellValueFactory(cellData -> cellData.getValue().getPhoneProperty());
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
   * Handles the action of the search button being pressed.
   */
  @FXML
  void searchButtonPressed() {
    allAccountsNotificationsViewModel.search();
    setAccountTableViewData();
  }

  /**
   * Handles the action of the ban button being pressed.
   */
  @FXML
  public void banButtonPressed() {
    allAccountsNotificationsViewModel.ban();
  }

  /**
   * Handles the action of the unban button being pressed.
   */
  @FXML
  public void unbanButtonPressed() {
    allAccountsNotificationsViewModel.unban();
  }

  /**
   * Binds values from the view model to the view components.
   */
  private void bindValues() {
    dateTimeColumn.textProperty().bindBidirectional(allAccountsNotificationsViewModel.getFirstColumnNameProperty());
    contentColumn.textProperty().bindBidirectional(allAccountsNotificationsViewModel.getSecondColumnNameProperty());
    errorLabel.textProperty().bindBidirectional(this.allAccountsNotificationsViewModel.getErrorProperty());
    searchTextField.textProperty().bindBidirectional(this.allAccountsNotificationsViewModel.getSearchFieldProperty());
    reasonTextArea.textProperty().bindBidirectional(allAccountsNotificationsViewModel.getReasonProperty());
  }

  /**
   * Binds visibility properties from the view model to the view components.
   */
  private void bindVisibleProperty() {
    // Visibility controlled from the view model
    banButton.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
    unbanButton.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
    emailColumn.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
    phoneColumn.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
    reasonLabel.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
    reasonTextArea.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
    searchButton.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
    searchTextField.visibleProperty().bindBidirectional(allAccountsNotificationsViewModel.getAllFieldsVisibility());
  }
}
