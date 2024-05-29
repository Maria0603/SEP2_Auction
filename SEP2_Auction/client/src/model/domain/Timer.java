package model.domain;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * The Timer class represents a countdown timer that notifies listeners of its remaining time.
 * It implements Runnable for running the timer in a separate thread and NamedPropertyChangeSubject
 * for property change support.
 */
public class Timer implements Runnable, NamedPropertyChangeSubject, Serializable {

  private long timerSeconds;
  private final int id;
  private final PropertyChangeSupport property;

  /**
   * Constructs a new Timer object with the specified timer duration and ID.
   *
   * @param timerSeconds the duration of the timer in seconds.
   * @param id the unique identifier for the timer.
   */
  public Timer(long timerSeconds, int id) {
    this.timerSeconds = timerSeconds;
    this.id = id;
    property = new PropertyChangeSupport(this);
  }

  /**
   * Gets the ID of the timer.
   *
   * @return the ID of the timer.
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the remaining time of the timer in seconds.
   *
   * @return the remaining time of the timer in seconds.
   */
  public long getTimerSeconds() {
    return timerSeconds;
  }

  /**
   * Runs the timer, counting down every second and firing property change events.
   */
  @Override
  public void run() {
    while (timerSeconds >= 0) {
      property.firePropertyChange("Time", id, timerSeconds);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // Handle the exception
      }
      timerSeconds--;
    }
    PropertyChangeListener[] listeners = property.getPropertyChangeListeners();
    for (PropertyChangeListener listener : listeners) {
      removeListener("Time", listener);
    }
  }

  /**
   * Adds a listener for a specific property change event.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to add.
   */
  @Override
  public void addListener(String propertyName, PropertyChangeListener listener) {
    property.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a listener for a specific property change event.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to remove.
   */
  @Override
  public void removeListener(String propertyName, PropertyChangeListener listener) {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
