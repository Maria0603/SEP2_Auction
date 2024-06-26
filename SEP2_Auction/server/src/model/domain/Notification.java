package model.domain;

import java.io.Serial;
import java.io.Serializable;

public class Notification implements Serializable
{
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  @Serial private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////
  private final String dateTime, content, receiver;

  public Notification(String dateTime, String content, String receiver)
  {
    this.dateTime = dateTime;
    this.content = content;
    this.receiver = receiver;
  }

  public String getDateTime()
  {
    return dateTime;
  }

  public String getContent()
  {
    return content;
  }

  public String getReceiver()
  {
    return receiver;
  }
}
