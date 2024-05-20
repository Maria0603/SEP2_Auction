package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AuctionModel;
import model.Notification;
import model.NotificationList;
import model.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class AllAccounts_NotificationsViewModel
    implements PropertyChangeListener {
  private ObservableList<NotificationViewModel> notifications;
  private ObservableList<AccountViewModel> allAccounts;
  private ObjectProperty<NotificationViewModel> selectedRowNotificationProperty;
  private ObjectProperty<AccountViewModel> selectedRowAccountProperty;
  private final AuctionModel model;
  private ViewModelState viewModelState;
  private StringProperty errorProperty;
  private BooleanProperty allFieldsVisibility;
  private StringProperty firstColumnNameProperty, secondColumnNameProperty;
  private StringProperty searchFieldProperty, reasonProperty;

  public AllAccounts_NotificationsViewModel(AuctionModel model,
      ViewModelState viewModelState) {
    this.model = model;
    this.model.addListener("Notification", this);
    this.model.addListener("Ban", this);
    this.viewModelState = viewModelState;
    notifications = FXCollections.observableArrayList();
    allAccounts = FXCollections.observableArrayList();
    selectedRowNotificationProperty = new SimpleObjectProperty<>();
    selectedRowAccountProperty=new SimpleObjectProperty<>();
    errorProperty = new SimpleStringProperty();
    searchFieldProperty = new SimpleStringProperty();
    reasonProperty=new SimpleStringProperty();

    //to control the visibility from the view model
    allFieldsVisibility = new SimpleBooleanProperty();
    firstColumnNameProperty = new SimpleStringProperty();
    secondColumnNameProperty = new SimpleStringProperty();
    reset();
  }

  public ObservableList<NotificationViewModel> getNotifications() {
    return notifications;
  }

  public ObservableList<AccountViewModel> getAllAccounts() {
    return allAccounts;
  }

  public void reset() {
    errorProperty.set(null);
    loadNotifications();
    loadAllAccounts();
  }

  private void loadAllAccounts() {
    try {
      allAccounts.clear();
      ArrayList<User> updatedUserList = model.getAllUsers();

      for (User user : updatedUserList) {
        allAccounts.add(new AccountViewModel(user));
      }
    }
    catch (SQLException e) {
      errorProperty.set(e.getMessage());
    }
  }

  public void setSelectedNotification(NotificationViewModel notification) {
    selectedRowNotificationProperty.set(notification);
  }
  public void setSelectedAccount(AccountViewModel account) {
    selectedRowAccountProperty.set(account);
  }

  public void setForNotifications() {
    allFieldsVisibility.set(false);
    firstColumnNameProperty.set("Date and time");
    secondColumnNameProperty.set("Content");
  }

  public void setForAccounts() {
    allFieldsVisibility.set(true);
  }

  private void loadNotifications() {
    if (viewModelState.getUserEmail() != null) {
      notifications.clear();
      NotificationList list;
      try {
        list = model.getNotifications(viewModelState.getUserEmail());
        if (list != null) {
          for (int i = 0; i < list.getSize(); i++) {
            notifications.add(
                new NotificationViewModel(list.getNotification(i)));
          }
        }
      }
      catch (SQLException e) {
        errorProperty.set(e.getMessage());
      }
    }
  }

  public void search() {
    ObservableList<AccountViewModel> searchedAccounts = searchAccounts(searchFieldProperty.get());
    allAccounts.clear();
    allAccounts.addAll(searchedAccounts);
  }

  private ObservableList<AccountViewModel> searchAccounts(String query) {
    ObservableList<AccountViewModel> results = FXCollections.observableArrayList();
    String lowerCaseQuery = query.toLowerCase();

    for (AccountViewModel account : allAccounts) {
      if (account.getEmailProperty().get().toLowerCase()
          .contains(lowerCaseQuery) || account.getFirstNameProperty().get()
          .toLowerCase().contains(lowerCaseQuery)
          || account.getLastNameProperty().get().toLowerCase()
          .contains(lowerCaseQuery)) {
        results.add(account);
        System.out.println(account);
      }
    }

    return results;
  }
  public void ban()
  {
    try
    {
      ///////////////////////////////////////////////////////////////////
      model.banParticipant(viewModelState.getUserEmail(), selectedRowAccountProperty.getValue().getEmailProperty().get(),reasonProperty.get().trim());
      /////////////////////////////////////////////////////////////////
    }
    catch(SQLException e)
    {
      errorProperty.set(e.getMessage());
    }
  }
  public void unban()
  {
    try
    {
      ///////////////////////////////////////////////////////////////////
      model.unbanParticipant(viewModelState.getUserEmail(), selectedRowAccountProperty.getValue().getEmailProperty().get());
      /////////////////////////////////////////////////////////////////
    }
    catch(SQLException e)
    {
      errorProperty.set(e.getMessage());
    }
  }

  public StringProperty getErrorProperty() {
    return errorProperty;
  }

  public BooleanProperty getAllFieldsVisibility() {
    return allFieldsVisibility;
  }

  public StringProperty getFirstColumnNameProperty() {
    return firstColumnNameProperty;
  }

  public StringProperty getSecondColumnNameProperty() {
    return secondColumnNameProperty;
  }

  public StringProperty getSearchFieldProperty() {
    return searchFieldProperty;
  }
  public StringProperty getReasonProperty()
  {
    return reasonProperty;
  }

  public void setAllAccounts(ObservableList<AccountViewModel> newData) {
    allAccounts = newData;
  }


  public void addNotification(NotificationViewModel notification) {
    notifications.add(notification);
  }

  @Override public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case "Notification":
        if (evt.getNewValue() != null) {
          Notification notification = (Notification) evt.getNewValue();
          if (notification.getReceiver().equals(viewModelState.getUserEmail()))
            Platform.runLater(
                () -> addNotification(new NotificationViewModel(notification)));
        }
        break;
    }
  }
}

