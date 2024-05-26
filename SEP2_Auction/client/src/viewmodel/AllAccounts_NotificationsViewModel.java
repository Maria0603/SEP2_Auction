package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import model.domain.Notification;
import model.domain.NotificationList;
import model.domain.User;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class AllAccounts_NotificationsViewModel
    implements PropertyChangeListener
{
  private ObservableList<NotificationViewModel> notifications;
  private ObservableList<AccountViewModel> allAccounts;
  private ObjectProperty<NotificationViewModel> selectedRowNotificationProperty;
  private ObjectProperty<AccountViewModel> selectedRowAccountProperty;
  private final UserListModel model;
  private ViewModelState viewModelState;
  private StringProperty errorProperty;
  private BooleanProperty allFieldsVisibility;
  private StringProperty firstColumnNameProperty, secondColumnNameProperty;
  private StringProperty searchFieldProperty, reasonProperty;

  public AllAccounts_NotificationsViewModel(UserListModel model,
      ViewModelState viewModelState)
  {
    this.model = model;
    this.model.addListener("Notification", this);
    this.model.addListener("Ban", this);
    this.viewModelState = viewModelState;
    notifications = FXCollections.observableArrayList();
    allAccounts = FXCollections.observableArrayList();
    selectedRowNotificationProperty = new SimpleObjectProperty<>();
    selectedRowAccountProperty = new SimpleObjectProperty<>();
    errorProperty = new SimpleStringProperty();
    searchFieldProperty = new SimpleStringProperty();
    reasonProperty = new SimpleStringProperty();

    //to control the visibility from the view model
    allFieldsVisibility = new SimpleBooleanProperty();
    firstColumnNameProperty = new SimpleStringProperty();
    secondColumnNameProperty = new SimpleStringProperty();
    reset();
  }

  public ObservableList<NotificationViewModel> getNotifications()
  {
    return notifications;
  }

  public ObservableList<AccountViewModel> getAllAccounts()
  {
    return allAccounts;
  }

  public void reset()
  {
    errorProperty.set("");
    reasonProperty.set("");
    if (viewModelState.getUserEmail() != null)
    {
      loadNotifications();
      loadAllAccounts();
    }
  }

  private void loadAllAccounts()
  {
    try
    {
      allAccounts.clear();
      ArrayList<User> updatedUserList = model.getAllUsers();

      for (User user : updatedUserList)
      {
        allAccounts.add(new AccountViewModel(user));
      }
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
    }
  }

  public void setSelectedNotification(NotificationViewModel notification)
  {
    selectedRowNotificationProperty.set(notification);
  }

  public void setSelectedAccount(AccountViewModel account)
  {
    selectedRowAccountProperty.set(account);
    if (selectedRowAccountProperty.getValue() == null)
      errorProperty.set("Select an account.");
    else
    {
      try
      {
        reasonProperty.set(model.extractBanningReason(
            selectedRowAccountProperty.getValue().getEmailProperty().get()));
        errorProperty.set("");
      }
      catch (SQLException e)
      {
        errorProperty.set(e.getMessage());
      }
    }

  }

  public void setForNotifications()
  {
    allFieldsVisibility.set(false);
    firstColumnNameProperty.set("Date and time");
    secondColumnNameProperty.set("Content");
  }

  public void setForAccounts()
  {

    allFieldsVisibility.set(true);
    firstColumnNameProperty.set("First name");
    secondColumnNameProperty.set("Last name");
  }

  private void loadNotifications()
  {
    if (viewModelState.getUserEmail() != null)
    {
      notifications.clear();
      NotificationList list;
      try
      {
        list = model.getNotifications(viewModelState.getUserEmail());
        if (list != null)
        {
          for (int i = list.getSize() - 1; i >= 0; i--)
          {
            notifications.add(
                new NotificationViewModel(list.getNotification(i)));
          }
        }
      }
      catch (SQLException e)
      {
        errorProperty.set(e.getMessage());
      }
    }
  }

  public void search()
  {
    ObservableList<AccountViewModel> searchedAccounts = searchAccounts(
        searchFieldProperty.get());
    allAccounts.clear();
    allAccounts.addAll(searchedAccounts);
  }

  private ObservableList<AccountViewModel> searchAccounts(String query)
  {
    ObservableList<AccountViewModel> results = FXCollections.observableArrayList();
    String lowerCaseQuery = query.toLowerCase();

    try
    {
      for (User account : model.getAllUsers())
      {
        if (account.getEmail().toLowerCase().contains(lowerCaseQuery)
            || account.getFirstname().toLowerCase().contains(lowerCaseQuery)
            || account.getLastname().toLowerCase().contains(lowerCaseQuery))
        {
          results.add(new AccountViewModel(account));
          System.out.println(account);
        }
      }
    }
    catch (SQLException e)
    {
      errorProperty.set(e.getMessage());
    }

    return results;
  }

  public void ban()
  {
    if (selectedRowAccountProperty.getValue() == null)
      errorProperty.set("Select an account.");
    else
    {
      errorProperty.set("");
      try
      {
        model.banParticipant(viewModelState.getUserEmail(),
            selectedRowAccountProperty.getValue().getEmailProperty().get(),
            reasonProperty.get());
        reasonProperty.set("");
      }
      catch (SQLException e)
      {
        errorProperty.set(e.getMessage());
      }
    }
  }

  public void unban()
  {
    if (selectedRowAccountProperty.getValue() == null)
      errorProperty.set("Select an account.");
    else
    {
      errorProperty.set("");
      try
      {
        model.unbanParticipant(viewModelState.getUserEmail(),
            selectedRowAccountProperty.getValue().getEmailProperty().get());
        reasonProperty.set("");
      }
      catch (SQLException e)
      {
        errorProperty.set(e.getMessage());
      }
    }
  }

  public StringProperty getErrorProperty()
  {
    return errorProperty;
  }

  public BooleanProperty getAllFieldsVisibility()
  {
    return allFieldsVisibility;
  }

  public StringProperty getFirstColumnNameProperty()
  {
    return firstColumnNameProperty;
  }

  public StringProperty getSecondColumnNameProperty()
  {
    return secondColumnNameProperty;
  }

  public StringProperty getSearchFieldProperty()
  {
    return searchFieldProperty;
  }

  public StringProperty getReasonProperty()
  {
    return reasonProperty;
  }

  public void setAllAccounts(ObservableList<AccountViewModel> newData)
  {
    allAccounts = newData;
  }

  public void addNotification(NotificationViewModel notification)
  {
    notifications.add(notification);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName())
    {
      case "Notification":
        if (evt.getNewValue() != null)
        {
          Notification notification = (Notification) evt.getNewValue();
          if (notification.getReceiver().equals(viewModelState.getUserEmail()))
            Platform.runLater(
                () -> addNotification(new NotificationViewModel(notification)));
        }
        break;
    }
  }
}

