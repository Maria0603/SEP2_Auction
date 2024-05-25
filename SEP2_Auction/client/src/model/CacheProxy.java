package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CacheProxy implements AuctionModel, PropertyChangeListener
{
  private AuctionList ongoingAuctionsCache;
  private AuctionList allAuctionsCache;
  private NotificationList notificationsCache;
  private AuctionModelManager modelManager;
  private PropertyChangeSupport property;
  private String userEmail;

  private AuctionList previousBidsCache, createdAuctionsCache, previousOpenedAuctions;

  public CacheProxy() throws SQLException, IOException
  {
    property = new PropertyChangeSupport(this);
    this.modelManager = new AuctionModelManager();
    modelManager.addListener("Auction", this);
    modelManager.addListener("End", this);
    modelManager.addListener("Bid", this);
    modelManager.addListener("Notification", this);
    modelManager.addListener("Edit", this);
    modelManager.addListener("Ban", this);
    modelManager.addListener("Reset", this);
    modelManager.addListener("DeleteAuction", this);

    modelManager.addListener("DeleteAccount", this);

    ongoingAuctionsCache = new AuctionList();
    allAuctionsCache = new AuctionList();

    notificationsCache = new NotificationList();
    previousBidsCache = new AuctionList();
    createdAuctionsCache = new AuctionList();

    previousOpenedAuctions=new AuctionList();

    userEmail = null;
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller)
      throws SQLException, ClassNotFoundException
  {
    return modelManager.startAuction(title, description, reservePrice,
        buyoutPrice, minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    if(previousOpenedAuctions.contains(ID))
    {
      return previousOpenedAuctions.getAuctionByID(ID);
    }
    Auction auction = modelManager.getAuction(ID);
    startTimer(auction);
    previousOpenedAuctions.addAuction(auction);
    return auction;
  }

  private void startTimer(Auction auction)
  {
    if (auction.getStatus().equals("ONGOING"))
    {
      Timer timer = new Timer(
          timeLeft(Time.valueOf(LocalTime.now()), auction.getEndTime()) - 1,
          auction.getID());
      timer.addListener("Time", this);
      Thread t = new Thread(timer, String.valueOf(auction.getID()));
      t.start();
    }
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return ongoingAuctionsCache;
  }

  @Override public NotificationList getNotifications(String receiver)
  {
    return notificationsCache;
  }

  @Override public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws SQLException
  {
    return modelManager.placeBid(bidder, bidValue, auctionId);
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

  @Override public String addUser(String firstname, String lastname,
      String email, String password, String repeatedPassword, String phone,
      LocalDate birthday) throws SQLException
  {
    userEmail = modelManager.addUser(firstname, lastname, email, password,
        repeatedPassword, phone, birthday);

    updateCache(userEmail);
    return userEmail;
  }


  @Override public String login(String email, String password)
      throws SQLException
  {
    userEmail = modelManager.login(email, password);

    if (isModerator(userEmail))
    {
      allAuctionsCache = modelManager.getAllAuctions(userEmail);
    }
    updateCache(userEmail);
    return userEmail;
  }

  @Override public ArrayList<User> getAllUsers() throws SQLException
  {
    return modelManager.getAllUsers();
  }

  @Override public void buyout(String bidder, int auctionId) throws SQLException
  {
    modelManager.buyout(bidder, auctionId);
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    return previousBidsCache;
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    return createdAuctionsCache;
  }

  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    modelManager.resetPassword(userEmail, oldPassword, newPassword,
        repeatPassword);
  }

  @Override public User getUser(String email) throws SQLException
  {
    return modelManager.getUser(email);
  }

  @Override public User getModeratorInfo() throws SQLException
  {
    return modelManager.getModeratorInfo();
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    return modelManager.isModerator(email);
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
    return modelManager.editInformation(oldEmail, firstname, lastname, email,
        password, phone, birthday);
  }

  @Override public AuctionList getAllAuctions(String moderatorEmail)
      throws SQLException
  {
    return allAuctionsCache;
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

  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws SQLException
  {
    modelManager.deleteAuction(moderatorEmail, auctionId, reason);
  }

  @Override public void deleteAccount(String email, String password)
      throws SQLException
  {
    modelManager.deleteAccount(email, password);
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
    createdAuctionsCache = modelManager.getCreatedAuctions(userEmail);
    previousBidsCache = modelManager.getPreviousBids(userEmail);
    ongoingAuctionsCache = modelManager.getOngoingAuctions();
    previousOpenedAuctions=new AuctionList();
  }
  private void updateBidIn(Bid bid, AuctionList cache)
  {
    if(cache.contains(bid.getAuctionId()))
    {
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());
      cache.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
    }
  }
  private void receivedAuction(PropertyChangeEvent evt)
  {
    Auction auction = (Auction) evt.getNewValue();
    ongoingAuctionsCache.addAuction(auction);
    allAuctionsCache.addAuction(auction);
    if (userEmail.equals(auction.getSeller()))
      createdAuctionsCache.addAuction(auction);
  }
  private void receivedEnd(PropertyChangeEvent evt)
  {
    System.out.println(
        evt.getPropertyName() + "     " + evt.getOldValue() + "    "
            + evt.getNewValue());
    int auctionId=Integer.parseInt(evt.getOldValue().toString());
    ongoingAuctionsCache.removeAuction(auctionId);
    if(previousOpenedAuctions.contains(auctionId))
      previousOpenedAuctions.getAuctionByID(auctionId).setStatus("CLOSED");


    if (evt.getNewValue() instanceof Bid)
    {
      Bid buyout = (Bid) evt.getNewValue();
      Auction auction = null;
      try
      {
        auction = modelManager.getAuction(buyout.getAuctionId());
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      if (buyout.getBidder().equals(userEmail))
      {
        if (auction != null)
          previousBidsCache.addAuction(auction);
      }
      updateBidIn(buyout, allAuctionsCache);

      if (auction != null && auction.getSeller().equals(userEmail))
      {
        updateBidIn(buyout, createdAuctionsCache);
      }

      updateBidIn(buyout, previousOpenedAuctions);
    }
  }
  private void receivedNotification(PropertyChangeEvent evt)
  {
    Notification notification = (Notification) evt.getNewValue();
    if (notification.getReceiver().equals(userEmail))
      notificationsCache.addNotification(notification);
  }
  private void receivedBid(PropertyChangeEvent evt)
  {
    // we receive a bid
    Bid bid = (Bid) evt.getNewValue();
    System.out.println("received bid in cache; " + bid.getAuctionId() + " "
        + bid.getBidder() + "   " + bid.getBidAmount());

    // obviously, for an ongoing auction, so we update the cache
    updateBidIn(bid, ongoingAuctionsCache);
    updateBidIn(bid, allAuctionsCache);

    //if we placed the bid, we add the auction in cache
    if (!previousBidsCache.contains(bid.getAuctionId()) && bid.getBidder().equals(userEmail))
    {
      try
      {
        previousBidsCache.addAuction(getAuction(bid.getAuctionId()));
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    else
      // if someone else placed a bid for an auction where we previously bid
      // we update the cache
      updateBidIn(bid, previousBidsCache);

    updateBidIn(bid, createdAuctionsCache);
    updateBidIn(bid, previousOpenedAuctions);
  }
  private void receivedEdit(PropertyChangeEvent evt)
  {
    if (userEmail.equals(evt.getOldValue().toString()))
    {
      userEmail = evt.getNewValue().toString();
    }
    try
    {
      updateCache(userEmail);
      if (isModerator(userEmail))
        allAuctionsCache = modelManager.getAllAuctions(userEmail);
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
      updateCache(userEmail);
      if (isModerator(userEmail))
        allAuctionsCache = modelManager.getAllAuctions(userEmail);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  private void receivedDeleteAuction(PropertyChangeEvent evt)
  {
    int id = Integer.parseInt(evt.getNewValue().toString());
    ongoingAuctionsCache.removeAuction(id);
    previousBidsCache.removeAuction(id);
    createdAuctionsCache.removeAuction(id);
    allAuctionsCache.removeAuction(id);
    previousOpenedAuctions.removeAuction(id);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    System.out.println("received "+evt.getPropertyName() + " in cache");
    switch (evt.getPropertyName())
    {
      case "Auction" -> receivedAuction(evt);
      case "End" -> receivedEnd(evt);

      case "Notification" -> receivedNotification(evt);
      case "Bid" -> receivedBid(evt);
      case "Edit" -> receivedEdit(evt);
      case "Ban", "DeleteAccount" -> receivedBanOrDeleteAccount(evt);
      case "DeleteAuction" -> receivedDeleteAuction(evt);

    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
        evt.getNewValue());
  }
}