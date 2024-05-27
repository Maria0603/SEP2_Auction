package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ListenerSubject implements NamedPropertyChangeSubject,
    PropertyChangeListener, ListenerSubjectInterface
{
  private AuctionModel auctionModel;
  private AuctionListModel auctionListModel;
  private UserModel userModel;
  private UserListModel userListModel;
  private PropertyChangeSupport property;
  public ListenerSubject(AuctionModel auctionModel, AuctionListModel auctionListModel, UserModel userModel, UserListModel userListModel)
  {
    this.auctionModel=auctionModel;
    this.auctionListModel=auctionListModel;
    this.userModel=userModel;
    this.userListModel=userListModel;
    property=new PropertyChangeSupport(this);
    addAsListener();
  }
  private void addAsListener()
  {
    auctionModel.addListener("Auction", this);
    auctionModel.addListener("End", this);
    auctionModel.addListener("Notification", this);
    auctionModel.addListener("Bid", this);
    auctionModel.addListener("DeleteAuction", this);
    userModel.addListener("Ban", this);

    userListModel.addListener("Reset", this);

    userListModel.addListener("Edit", this);
    userListModel.addListener("DeleteAccount", this);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    System.out.println("fired " + evt.getPropertyName() + " in listenerSubject");
    property.firePropertyChange(evt);
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
