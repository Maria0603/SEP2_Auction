package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AuctionModel;
import model.NotificationList;
import view.AllAccounts_NotificationsViewController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

public class AllAccounts_NotificationsViewModel implements
    PropertyChangeListener
{
  private ObservableList<NotificationViewModel> notifications;
  private ObjectProperty<NotificationViewModel> selectedRowProperty;
  private final AuctionModel model;
  private ViewModelState viewModelState;
  private StringProperty errorProperty;
  public AllAccounts_NotificationsViewModel(AuctionModel model, ViewModelState viewModelState)
  {
    this.model=model;
    this.model.addListener("Notification", this);
    this.viewModelState=viewModelState;
    notifications= FXCollections.observableArrayList();
    selectedRowProperty=new SimpleObjectProperty<>();
    errorProperty=new SimpleStringProperty();
    loadNotifications();
  }
  public ObservableList<NotificationViewModel> getNotifications()
  {
    return notifications;
  }
  public void reset()
  {
    errorProperty.set(null);
    loadNotifications();
  }
  public void setSelected(NotificationViewModel notification)
  {
    selectedRowProperty.set(notification);
  }
  private void loadNotifications()
  {
    notifications.clear();
    NotificationList list;
    try
    {
      //we need the accounts
      list = model.getNotifications("mr. kaplan");
      for(int i=0; i<list.getSize(); i++)
      {
        notifications.add(new NotificationViewModel(list.getNotification(i)));
      }
    }
    catch(SQLException e)
    {
      errorProperty.set(e.getMessage());
    }
  }
  public StringProperty getErrorProperty()
  {
    return errorProperty;
  }
  public void addNotification(NotificationViewModel notification)
  {
    notifications.add(notification);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    Platform.runLater(() -> {
      switch (evt.getPropertyName())
      {
        case "Notification":
          addNotification((NotificationViewModel) evt.getNewValue());
          break;
      }
    });
  }
}

