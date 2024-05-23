

package model;

import persistence.AuctionDatabase;
import persistence.AuctionPersistence;
import persistence.ProtectionProxy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;
  private AuctionPersistence auctionDatabase;

  public AuctionModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    auctionDatabase = new ProtectionProxy();
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

  @Override public void buyOut(String current_bider, int auctionId)
      throws SQLException
  {
    try
    {
      auctionDatabase.buyOut(current_bider, auctionId);
      Auction auction = auctionDatabase.getAuctionById(auctionId);
      property.firePropertyChange("Time", null, auction.getStartTime());
      property.firePropertyChange("End", auctionId, auction);
      property.firePropertyChange("BuyOut", null, auction);
      System.out.println("received buyout");

      User seller = auctionDatabase.getUserInfo(auction.getSeller());
      User buyer = auctionDatabase.getUserInfo(auction.getCurrentBidder());

      String buyerNotification =
          "Congratulations! You've bought out item: " + auction.getItem()
              .getTitle() + "(" + auctionId + ") from " + seller.getEmail()
              + "(" + seller.getPhone() + ")";
      String sellerNotification =
          "Item: " + auction.getItem().getTitle() + "(" + auctionId
              + ") has been bought out by: " + buyer.getEmail() + "("
              + buyer.getPhone() + ")";

      Notification notificationOne = auctionDatabase.saveNotification(
          buyerNotification, buyer.getEmail());
      Notification notificationTwo = auctionDatabase.saveNotification(
          sellerNotification, seller.getEmail());

      property.firePropertyChange("Notification", null, notificationOne);
      property.firePropertyChange("Notification", null, notificationTwo);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
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

  @Override public synchronized void resetPassword(String userEmail,
      String oldPassword, String newPassword, String repeatPassword)
      throws SQLException
  {
    auctionDatabase.resetPassword(userEmail, oldPassword, newPassword,
        repeatPassword);
    property.firePropertyChange("Reset", null, userEmail);
  }

  @Override public synchronized User getUser(String email) throws SQLException
  {
    return auctionDatabase.getUserInfo(email);
  }

  @Override public synchronized User getModeratorInfo() throws SQLException
  {
    return auctionDatabase.getModeratorInfo();
  }

  @Override public synchronized boolean isModerator(String email)
      throws SQLException
  {
    return auctionDatabase.isModerator(email);
  }

  @Override public synchronized User editInformation(String oldEmail,
      String firstname, String lastname, String email, String password,
      String phone, LocalDate birthday) throws SQLException
  {
    User user = auctionDatabase.editInformation(oldEmail, firstname, lastname,
        email, password, phone, birthday);
    if (!oldEmail.equals(email))
    {
      property.firePropertyChange("Edit", oldEmail, email);
    }
    return user;
  }

  @Override public synchronized AuctionList getAllAuctions(String moderatorEmail) throws SQLException
  {
    return auctionDatabase.getAllAuctions(moderatorEmail);
  }

  @Override public ArrayList<User> getAllUsers(String moderatorEmail) throws SQLException
  {
    return auctionDatabase.getAllUsers(moderatorEmail);
  }

  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    try
    {
      auctionDatabase.banParticipant(moderatorEmail, participantEmail, reason);
    }
    catch (SQLException e)
    {
      if (e.getMessage().contains("successfully"))
        property.firePropertyChange("Ban", null, participantEmail);
      throw new SQLException(e.getMessage());
    }

  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    return auctionDatabase.extractBanningReason(email);
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    auctionDatabase.unbanParticipant(moderatorEmail, participantEmail);
  }

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws SQLException
  {
    String seller = auctionDatabase.getAuctionById(auctionId).getSeller();
    auctionDatabase.deleteAuction(moderatorEmail, auctionId, reason);

    Notification notification = auctionDatabase.saveNotification(
        "Your auction id #" + auctionId + " has been deleted. Reason: "
            + reason, seller);
    property.firePropertyChange("Notification", null, notification);
    property.firePropertyChange("Delete", null, auctionId);
    System.out.println("delete fired");
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

  private void sendContactInformation(int id) throws SQLException
  {
    Auction auction = auctionDatabase.getAuctionById(id);
    User seller = auctionDatabase.getUserInfo(auction.getSeller());
    User bidder = auctionDatabase.getUserInfo(auction.getCurrentBidder());
    int bid = auction.getCurrentBid();

    String contentForSeller =
        "Your Auction(id: " + id + ") has ended, Final bidder: " + bidder + "("
            + bidder.getPhone() + "), with bid of " + bid + ".";
    String contentForBidder =
        "You've won an Auction(id: " + id + "), sold by " + seller + "("
            + seller.getPhone() + "), with bid: " + bid + ".";

    Notification notificationOne = auctionDatabase.saveNotification(
        contentForSeller, seller.getEmail());
    Notification notificationTwo = auctionDatabase.saveNotification(
        contentForBidder, bidder.getEmail());

    property.firePropertyChange("Notification", null, notificationOne);
    property.firePropertyChange("Notification", null, notificationTwo);
  }
}
