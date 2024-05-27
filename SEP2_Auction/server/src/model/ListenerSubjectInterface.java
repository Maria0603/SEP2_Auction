package model;

import java.beans.PropertyChangeListener;

public interface ListenerSubjectInterface
{
  void addListener(String propertyName, PropertyChangeListener listener);
  void removeListener(String propertyName, PropertyChangeListener listener);
}
