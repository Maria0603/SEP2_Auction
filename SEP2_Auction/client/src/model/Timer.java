package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Timer implements Runnable, NamedPropertyChangeSubject, Serializable
{
  private long timerSeconds;
  private int id;
  private PropertyChangeSupport property;
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  //private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public Timer(long timerSeconds, int id)
  {
    this.timerSeconds = timerSeconds;
    this.id = id;
    property = new PropertyChangeSupport(this);
  }

  public int getId()
  {
    return id;
  }

  public long getTimerSeconds()
  {
    return timerSeconds;
  }

  @Override public void run()
  {
    //timerSeconds=timerSeconds/3600;
    while (timerSeconds >= 0)
    {
      property.firePropertyChange("Time", id, timerSeconds);
      try
      {
        Thread.sleep(1000);
      }
      catch (InterruptedException e)
      {
        //
      }
      timerSeconds--;
    }
    PropertyChangeListener[] listeners = property.getPropertyChangeListeners();
    for (int i = 0; i < listeners.length; i++)
    {
      removeListener("Time", listeners[i]);
    }
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
