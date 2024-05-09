package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;

public class ServerTimer
    implements Runnable, NamedPropertyChangeSubject, Serializable
{
  private int id;
  private PropertyChangeSupport property;
  Time start, end;
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  //private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public ServerTimer(Time start, Time end, int id)
  {
    this.start = start;
    this.end = end;
    this.id = id;
    property = new PropertyChangeSupport(this);
  }

  public int getId()
  {
    return id;
  }

  @Override public void run()
  {
    while (timeLeft(start, end) > 0)
    {
      try
      {
        Thread.sleep(timeLeft(start, end) * 1000);
      }
      catch (InterruptedException e)
      {
        //
      }
      if (LocalTime.now().isAfter(end.toLocalTime()) || start.equals(end))
      {
        property.firePropertyChange("End", id, 0);
        break;
      }
    }
  }

  private long timeLeft(Time currentTime, Time end)
  {
    long currentSeconds = currentTime.toLocalTime().toSecondOfDay();
    long endSeconds = end.toLocalTime().toSecondOfDay();
    if (currentSeconds >= endSeconds)
      return 60 * 60 * 24 - (currentSeconds - endSeconds);
    else
      return endSeconds - currentSeconds;
  }

  @Override public void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
