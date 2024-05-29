package model.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The NotificationList class represents a list of notifications and provides methods
 * to manage the notifications within the list. It implements Serializable for object serialization.
 */
public class NotificationList implements Serializable {

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private final ArrayList<Notification> notifications;

  /**
   * Constructs a new NotificationList object.
   * Initializes an empty list of notifications.
   */
  public NotificationList() {
    notifications = new ArrayList<>();
  }

  /**
   * Adds a notification to the list.
   *
   * @param notification the notification to add.
   */
  public void addNotification(Notification notification) {
    if (notification != null)
      notifications.add(notification);
  }

  /**
   * Retrieves a notification by its index in the list.
   *
   * @param index the index of the notification to retrieve.
   * @return the Notification at the specified index.
   */
  public Notification getNotification(int index) {
    return notifications.get(index);
  }

  /**
   * Gets the number of notifications in the list.
   *
   * @return the size of the notification list.
   */
  public int getSize() {
    return notifications.size();
  }
}
