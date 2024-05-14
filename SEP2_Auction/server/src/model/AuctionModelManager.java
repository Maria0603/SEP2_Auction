

package model;

import persistence.AuctionDatabase;
import persistence.AuctionPersistence;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.time.LocalDate;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;
  private AuctionPersistence auctionDatabase;

  public AuctionModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    auctionDatabase = new AuctionDatabase();
  }

  @Override public synchronized Auction startAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws SQLException
  {
    Auction auction = auctionDatabase.saveAuction(title, description,
        reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData,
        seller);
    property.firePropertyChange("Auction", null, auction);
    ServerTimer timer = new ServerTimer(auction.getStartTime(),
        auction.getEndTime(), auction.getID());
    timer.addListener("End", this);
    new Thread(timer).start();

    // auction.addListener("Time", this);
    //auction.addListener("End", this);
    return auction;
  }

  @Override public synchronized Auction getAuction(int ID) throws SQLException
  {
    return auctionDatabase.getAuctionById(ID);
  }

  @Override public synchronized AuctionList getOngoingAuctions()
      throws SQLException
  {
    return auctionDatabase.getOngoingAuctions();
  }

  @Override public synchronized NotificationList getNotifications(
      String receiver) throws SQLException
  {
    return auctionDatabase.getNotifications(receiver);
  }

  @Override public synchronized Bid placeBid(String bidder, int bidValue,
      int auctionId) throws SQLException
  {
    //we extract the existing bidder
    Bid existingBid = auctionDatabase.getCurrentBidForAuction(auctionId);
    Bid bid = auctionDatabase.saveBid(bidder, bidValue, auctionId);
    //if they exist, we send the notification in the event
    if (existingBid != null)
    {
      Notification notification = auctionDatabase.saveNotification(
          "Your bid has been beaten for auction ID: " + auctionId + ".",
          existingBid.getBidder());
      property.firePropertyChange("Notification", null, notification);
    }
    property.firePropertyChange("Bid", null, bid);
    return bid;
  }

  @Override public synchronized String addUser(String firstname,
      String lastname, String email, String password, String repeatedPassword,
      String phone, LocalDate birthday) throws SQLException
  {
    return auctionDatabase.createUser(firstname, lastname, email, password,
        repeatedPassword, phone, birthday).getEmail();
  }

  @Override public synchronized String login(String email, String password)
      throws SQLException
  {
    //  TODO: add validation in database
    return auctionDatabase.login(email, password);
  }

  @Override public synchronized AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    return auctionDatabase.getPreviousBids(bidder);
  }

  @Override public synchronized AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    return auctionDatabase.getCreatedAuctions(seller);
  }

  @Override public synchronized void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    auctionDatabase.resetPassword(userEmail, oldPassword, newPassword,
        repeatPassword);
  }

  @Override public synchronized User getUser(String email) throws SQLException
  {
    return auctionDatabase.getUserInfo(email);
  }

  @Override public synchronized User getModeratorInfo() throws SQLException
  {
    return auctionDatabase.getModeratorInfo();
  }

  @Override public synchronized boolean isModerator(String email) throws SQLException
  {
    return auctionDatabase.isModerator(email);
  }

  @Override public synchronized User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
    User user = auctionDatabase.editInformation(oldEmail, firstname, lastname,
        email, password, phone, birthday);
    if (!oldEmail.equals(email))
      property.firePropertyChange("Edit", oldEmail, email);
    return user;
  }

  @Override public synchronized AuctionList getAllAuctions() throws SQLException
  {
    return auctionDatabase.getAllAuctions();
  }

  @Override public synchronized void addListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void removeListener(String propertyName,
      PropertyChangeListener listener)
  {
    property.removePropertyChangeListener(propertyName, listener);
  }

  @Override public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("End"))
    {
      try
      {
        auctionDatabase.markAsClosed((int) evt.getOldValue());
        sendContactInformation((int) evt.getOldValue());
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
  private void sendContactInformation(int id) throws SQLException {
    Auction auction = auctionDatabase.getAuctionById(id);
    String seller = auction.getSeller();
    String bidder = auction.getCurrentBidder();
    int bid = auction.getCurrentBid();

    String contentForSeller = "Your Auction(id: " + id + ") has ended, Final bidder: " + bidder + ", with bid of " + bid + ".";
    String contentForBidder = "You've won an Auction (id: " + id + "), sold by " + seller + ", with bid: " + bid + ".";

    Notification notificationOne = auctionDatabase.saveNotification(contentForSeller,seller);
    Notification notificationTwo = auctionDatabase.saveNotification(contentForBidder,bidder);

    property.firePropertyChange("Notification", null, notificationOne);
    property.firePropertyChange("Notification", null, notificationTwo);
  }
}
