package model.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class NotificationList implements Serializable
{
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  @Serial private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////
  private final ArrayList<Notification> notifications;
  public NotificationList()
  {
    notifications=new ArrayList<>();
  }
  public void addNotification(Notification notification)
  {
    if(notification!=null)
      notifications.add(notification);
  }
  public Notification getNotification(int index)
  {
    return notifications.get(index);
  }
  public int getSize()
  {
    return notifications.size();
  }
}



