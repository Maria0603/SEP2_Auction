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

/**
 * The AllAccounts_NotificationsViewModel class is responsible for managing the
 * data and actions related to notifications and user accounts in the view.
 */
public class AllAccounts_NotificationsViewModel
        implements PropertyChangeListener
{
  private final ObservableList<NotificationViewModel> notifications;
  private ObservableList<AccountViewModel> allAccounts;
  private final ObjectProperty<NotificationViewModel> selectedRowNotificationProperty;
  private final ObjectProperty<AccountViewModel> selectedRowAccountProperty;
  private final UserListModel model;
  private final ViewModelState viewModelState;
  private final StringProperty errorProperty;
  private final BooleanProperty allFieldsVisibility;
  private final StringProperty firstColumnNameProperty;
  private final StringProperty secondColumnNameProperty;
  private final StringProperty searchFieldProperty;
  private final StringProperty reasonProperty;

  /**
   * Constructs an AllAccounts_NotificationsViewModel with the specified model and view model state.
   *
   * @param model the user list model
   * @param viewModelState the view model state
   */
  public AllAccounts_NotificationsViewModel(UserListModel model,
                                            ViewModelState viewModelState)
  {
    this.model = model;
    this.model.addListener("Notification", this);
    this.viewModelState = viewModelState;
    notifications = FXCollections.observableArrayList();
    allAccounts = FXCollections.observableArrayList();
    selectedRowNotificationProperty = new SimpleObjectProperty<>();
    selectedRowAccountProperty = new SimpleObjectProperty<>();
    errorProperty = new SimpleStringProperty();
    searchFieldProperty = new SimpleStringProperty();
    reasonProperty = new SimpleStringProperty();

    // To control the visibility from the view model
    allFieldsVisibility = new SimpleBooleanProperty();
    firstColumnNameProperty = new SimpleStringProperty();
    secondColumnNameProperty = new SimpleStringProperty();
    reset();
  }

  /**
   * Gets the list of notifications.
   *
   * @return the list of notifications
   */
  public ObservableList<NotificationViewModel> getNotifications()
  {
    return notifications;
  }

  /**
   * Gets the list of all accounts.
   *
   * @return the list of all accounts
   */
  public ObservableList<AccountViewModel> getAllAccounts()
  {
    return allAccounts;
  }

  /**
   * Resets the view model, clearing errors and loading data if a user email is available.
   */
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

  /**
   * Loads all user accounts into the view model.
   */
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
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }
  }

  /**
   * Sets the selected notification.
   *
   * @param notification the notification to select
   */
  public void setSelectedNotification(NotificationViewModel notification)
  {
    selectedRowNotificationProperty.set(notification);
  }

  /**
   * Sets the selected account and loads the banning reason if applicable.
   *
   * @param account the account to select
   */
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
      catch (IllegalArgumentException e)
      {
        errorProperty.set(e.getMessage());
      }
    }
  }

  /**
   * Sets the view model for displaying notifications.
   */
  public void setForNotifications()
  {
    allFieldsVisibility.set(false);
    firstColumnNameProperty.set("Date and time");
    secondColumnNameProperty.set("Content");
  }

  /**
   * Sets the view model for displaying user accounts.
   */
  public void setForAccounts()
  {
    allFieldsVisibility.set(true);
    firstColumnNameProperty.set("First name");
    secondColumnNameProperty.set("Last name");
  }

  /**
   * Loads the notifications for the current user.
   */
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
      catch (IllegalArgumentException e)
      {
        errorProperty.set(e.getMessage());
      }
    }
  }

  /**
   * Searches for accounts based on the query in the search field.
   */
  public void search()
  {
    ObservableList<AccountViewModel> searchedAccounts = searchAccounts(
            searchFieldProperty.get());
    allAccounts.clear();
    allAccounts.addAll(searchedAccounts);
  }

  /**
   * Searches for accounts that match the query.
   *
   * @param query the search query
   * @return the list of matching accounts
   */
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
    catch (IllegalArgumentException e)
    {
      errorProperty.set(e.getMessage());
    }

    return results;
  }

  /**
   * Bans the selected account with the provided reason.
   */
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
      catch (IllegalArgumentException e)
      {
        errorProperty.set(e.getMessage());
      }
    }
  }

  /**
   * Unbans the selected account.
   */
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
      catch (IllegalArgumentException e)
      {
        errorProperty.set(e.getMessage());
      }
    }
  }

  /**
   * Gets the error property.
   *
   * @return the error property
   */
  public StringProperty getErrorProperty()
  {
    return errorProperty;
  }

  /**
   * Gets the visibility property for all fields.
   *
   * @return the visibility property for all fields
   */
  public BooleanProperty getAllFieldsVisibility()
  {
    return allFieldsVisibility;
  }

  /**
   * Gets the property for the first column name.
   *
   * @return the property for the first column name
   */
  public StringProperty getFirstColumnNameProperty()
  {
    return firstColumnNameProperty;
  }

  /**
   * Gets the property for the second column name.
   *
   * @return the property for the second column name
   */
  public StringProperty getSecondColumnNameProperty()
  {
    return secondColumnNameProperty;
  }

  /**
   * Gets the property for the search field.
   *
   * @return the property for the search field
   */
  public StringProperty getSearchFieldProperty()
  {
    return searchFieldProperty;
  }

  /**
   * Gets the property for the reason field.
   *
   * @return the property for the reason field
   */
  public StringProperty getReasonProperty()
  {
    return reasonProperty;
  }

  /**
   * Adds a notification to the list of notifications.
   *
   * @param notification the notification to add
   */
  public void addNotification(NotificationViewModel notification)
  {
    notifications.add(notification);
  }

  /**
   * Handles property change events for notifications.
   *
   * @param evt the property change event
   */
  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("Notification"))
    {
      if (evt.getNewValue() != null)
      {
        Notification notification = (Notification) evt.getNewValue();
        if (notification.getReceiver().equals(viewModelState.getUserEmail()))
          Platform.runLater(
                  () -> addNotification(new NotificationViewModel(notification)));
      }
    }
  }
}
