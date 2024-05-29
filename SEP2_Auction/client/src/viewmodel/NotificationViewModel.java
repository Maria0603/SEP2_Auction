package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.domain.Notification;

/**
 * ViewModel for displaying notifications.
 */
public class NotificationViewModel {
  private final StringProperty contentProperty;
  private final StringProperty dateTimeProperty;

  /**
   * Constructs a NotificationViewModel with the given notification.
   * @param notification the notification to display
   */
  public NotificationViewModel(Notification notification) {
    dateTimeProperty = new SimpleStringProperty(notification.getDateTime());
    contentProperty = new SimpleStringProperty(notification.getContent());
  }

  /**
   * Gets the property containing the content of the notification.
   * @return the content property
   */
  public StringProperty getContentProperty() {
    return contentProperty;
  }

  /**
   * Gets the property containing the date and time of the notification.
   * @return the date and time property
   */
  public StringProperty getDateTimeProperty() {
    return dateTimeProperty;
  }
}
