package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.Notification;

public class NotificationViewModel
{
  private final StringProperty contentProperty;
  private final StringProperty dateTimeProperty;

  public NotificationViewModel(Notification notification)
  {
    dateTimeProperty = new SimpleStringProperty(notification.getDateTime());
    contentProperty = new SimpleStringProperty(notification.getContent());
  }

  public StringProperty getContentProperty()
  {
    return contentProperty;
  }

  public StringProperty getDateTimeProperty()
  {
    return dateTimeProperty;
  }
}




