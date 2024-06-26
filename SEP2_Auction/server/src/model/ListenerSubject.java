package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The ListenerSubject class implements the NamedPropertyChangeSubject and ListenerSubjectInterface
 * and listens to property changes in auction and user models to propagate them to other listeners.
 */
public class ListenerSubject implements NamedPropertyChangeSubject, PropertyChangeListener, ListenerSubjectInterface
{
  private final AuctionModel auctionModel;
  private final AuctionListModel auctionListModel;
  private final UserModel userModel;
  private final UserListModel userListModel;
  private final PropertyChangeSupport property;

  /**
   * Constructs a ListenerSubject with the specified models.
   *
   * @param auctionModel the auction model.
   * @param auctionListModel the auction list model.
   * @param userModel the user model.
   * @param userListModel the user list model.
   */
  public ListenerSubject(AuctionModel auctionModel, AuctionListModel auctionListModel, UserModel userModel, UserListModel userListModel)
  {
    this.auctionModel = auctionModel;
    this.auctionListModel = auctionListModel;
    this.userModel = userModel;
    this.userListModel = userListModel;
    property = new PropertyChangeSupport(this);
    addAsListener();
  }

  /**
   * Adds this ListenerSubject as a listener to the specified models.
   */
  private void addAsListener()
  {
    auctionModel.addListener("Auction", this);
    auctionModel.addListener("End", this);
    auctionModel.addListener("Notification", this);
    auctionModel.addListener("Bid", this);
    auctionModel.addListener("DeleteAuction", this);

    userListModel.addListener("Ban", this);

    userModel.addListener("Reset", this);
    userModel.addListener("Edit", this);
    userModel.addListener("DeleteAccount", this);
  }

  /**
   * Handles property change events and fires property change notifications.
   *
   * @param evt the property change event.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt)
  {
    property.firePropertyChange(evt);
  }

  /**
   * Adds a listener for a specific property change.
   *
   * @param propertyName the name of the property to listen for.
   * @param listener the listener to be added.
   */
  @Override
  public void addListener(String propertyName, PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a listener for a specific property change.
   *
   * @param propertyName the name of the property to stop listening for.
   * @param listener the listener to be removed.
   */
  @Override
  public void removeListener(String propertyName, PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }
}
