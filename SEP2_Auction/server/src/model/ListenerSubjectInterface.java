package model;

import java.beans.PropertyChangeListener;

/**
 * The ListenerSubjectInterface provides methods for adding and removing listeners
 * for property change events.
 */
public interface ListenerSubjectInterface
{
  /**
   * Adds a listener for a specific property change.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to be added.
   */
  void addListener(String propertyName, PropertyChangeListener listener);

  /**
   * Removes a listener for a specific property change.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to be removed.
   */
  void removeListener(String propertyName, PropertyChangeListener listener);
}
