package model;

import model.domain.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class UserListCacheProxy extends Cache implements UserListModel, PropertyChangeListener
{
  private NotificationList notificationsCache;
  private UserListModelManager modelManager;
  private PropertyChangeSupport property;

  public UserListCacheProxy() throws SQLException, IOException
  {
    super();
    property = new PropertyChangeSupport(this);
    this.modelManager = new UserListModelManager();
    modelManager.addListener("Bid", this);
    modelManager.addListener("Notification", this);
    modelManager.addListener("Edit", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("Reset", this);
    modelManager.addListener("DeleteAccount", this);

    notificationsCache = new NotificationList();
  }

  @Override public NotificationList getNotifications(String receiver)
  {
    return notificationsCache;
  }

  @Override public ArrayList<User> getAllUsers() throws SQLException
  {
    return modelManager.getAllUsers();
  }


  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    modelManager.banParticipant(moderatorEmail, participantEmail, reason);
  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    return modelManager.extractBanningReason(email);
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    modelManager.unbanParticipant(moderatorEmail, participantEmail);
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
  private void updateCache(String userEmail) throws SQLException
  {
    notificationsCache = modelManager.getNotifications(userEmail);
  }
  private void updateBidIn(Bid bid, AuctionList cache)
  {
    if(cache.contains(bid.getAuctionId()))
    {
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
    }
  }

  private void receivedNotification(PropertyChangeEvent evt)
  {
    Notification notification = (Notification) evt.getNewValue();
    if (notification.getReceiver().equals(super.getUserEmail()))
      notificationsCache.addNotification(notification);
  }
  private void receivedBid(PropertyChangeEvent evt)
  {
    // we receive a bid
    Bid bid = (Bid) evt.getNewValue();
    System.out.println("received bid in cache; " + bid.getAuctionId() + " "
        + bid.getBidder() + "   " + bid.getBidAmount());


  }
  private void receivedEdit(PropertyChangeEvent evt)
  {
    try
    {
      updateCache(super.getUserEmail());

    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  private void receivedBanOrDeleteAccount(PropertyChangeEvent evt)
  {
    try
    {
      updateCache(super.getUserEmail());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }


  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    System.out.println("received "+evt.getPropertyName() + " in cache");
    switch (evt.getPropertyName())
    {
      case "Notification" -> receivedNotification(evt);
      case "Bid" -> receivedBid(evt);
      case "Edit" -> receivedEdit(evt);
      case "Ban", "DeleteAccount" -> receivedBanOrDeleteAccount(evt);
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
        evt.getNewValue());
  }
}
