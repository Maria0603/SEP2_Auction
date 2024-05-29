package model.domain;

import java.io.Serial;
import java.io.Serializable;

/**
 * The Notification class represents a notification with a date and time, content, and receiver.
 * It implements Serializable for object serialization.
 */
public class Notification implements Serializable {

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private final String dateTime;
  private final String content;
  private final String receiver;

  /**
   * Constructs a new Notification object with the specified date and time, content, and receiver.
   *
   * @param dateTime the date and time of the notification.
   * @param content the content of the notification.
   * @param receiver the receiver of the notification.
   */
  public Notification(String dateTime, String content, String receiver) {
    this.dateTime = dateTime;
    this.content = content;
    this.receiver = receiver;
  }

  /**
   * Gets the date and time of the notification.
   *
   * @return the date and time of the notification.
   */
  public String getDateTime() {
    return dateTime;
  }

  /**
   * Gets the content of the notification.
   *
   * @return the content of the notification.
   */
  public String getContent() {
    return content;
  }

  /**
   * Gets the receiver of the notification.
   *
   * @return the receiver of the notification.
   */
  public String getReceiver() {
    return receiver;
  }
}
