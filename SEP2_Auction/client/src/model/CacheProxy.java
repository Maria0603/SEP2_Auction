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
  private AuctionList previouslyOpenedAuctions;
  private NotificationList notifications;
  private AuctionModelManager modelManager;
  private PropertyChangeSupport property;
  private ArrayList<Thread> timers;
  private String userEmail;

  private AuctionList previousBids;

  public CacheProxy() throws SQLException, IOException
  {
    property = new PropertyChangeSupport(this);
    this.modelManager = new AuctionModelManager();
    modelManager.addListener("Auction", this);
    modelManager.addListener("End", this);
    modelManager.addListener("Bid", this);
    modelManager.addListener("Notification", this);

    ongoingAuctionsCache = null;
    previouslyOpenedAuctions =new AuctionList();
    timers=new ArrayList<>();

    notifications=null;
    previousBids=null;

    userEmail =null;
  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller) throws SQLException, ClassNotFoundException
  {
    return modelManager.startAuction(title, description,
        reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    Auction auction;
    try
    {
      auction = previouslyOpenedAuctions.getAuctionByID(ID);
      for(int i=0; i<timers.size(); i++)
        if(timers.get(i).getId()==ID)
          timers.get(i).start();
      //else timers.get(i).interrupt();
    }
    catch(IllegalArgumentException e)
    {
      auction = modelManager.getAuction(ID);
      //add auction to cache
      previouslyOpenedAuctions.addAuction(auction);
      Timer timer = new Timer(timeLeft(Time.valueOf(LocalTime.now()), auction.getEndTime()) - 1, ID);
      timer.addListener("Time", this);
      timer.addListener("End", this);
      Thread t = new Thread(timer, String.valueOf(ID));
      //add timer to cache
      timers.add(t);
      t.start();
    }

    return auction;
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    if(ongoingAuctionsCache==null)
      ongoingAuctionsCache = modelManager.getOngoingAuctions();
    return ongoingAuctionsCache;
  }


  @Override public NotificationList getNotifications(String receiver)
      throws SQLException
  {
    if(notifications==null && receiver.equals(userEmail))
    {
      notifications = modelManager.getNotifications(receiver);
    }
    return notifications;
    //return modelManager.getNotifications(receiver);
  }

  @Override public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws SQLException
  {
    return modelManager.placeBid(bidder, bidValue, auctionId);
  }

  private long timeLeft(Time currentTime, Time end) {
    long currentSeconds = currentTime.toLocalTime().toSecondOfDay();
    long endSeconds = end.toLocalTime().toSecondOfDay();
    if (currentSeconds >= endSeconds)
      return 60 * 60 * 24 - (currentSeconds - endSeconds);
    else
      return endSeconds - currentSeconds;
  }
  @Override
  public String addUser(String firstname, String lastname, String email, String password, String repeatedPassword, String phone, LocalDate birthday
  ) throws SQLException {
    return modelManager.addUser(firstname,lastname,email,password,repeatedPassword, phone, birthday);
  }

  @Override
  public String login(String email, String password) throws SQLException {
    userEmail=modelManager.login(email, password);
    return userEmail;
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    if(previousBids==null && bidder.equals(userEmail))
    {
      previousBids = modelManager.getPreviousBids(bidder);
    }
    return previousBids;
  }

  @Override public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException
  {
    modelManager.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
  }

  @Override public User getParticipant(String email) throws SQLException
  {
    return modelManager.getParticipant(email);
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    return modelManager.isModerator(email);
  }

  @Override public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException
  {
    return modelManager.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
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

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    switch (evt.getPropertyName())
    {
      case "Auction":
        ongoingAuctionsCache.addAuction((Auction) evt.getNewValue());
        break;
      case "End":
        ongoingAuctionsCache.removeAuction(Integer.parseInt(evt.getOldValue().toString()));
        //closedAuctionsCache.addAuction((Auction) evt.getNewValue());
        break;
      case "Notification":
        Notification notification=(Notification) evt.getNewValue();
        if(notification.getReceiver().equals(userEmail))
          notifications.addNotification(notification);
        break;
      case "Bid":
        //we receive a bid
        Bid bid=(Bid)evt.getNewValue();
        //System.out.println("bid in cache"+ bid.getBidAmount());

        //obviously, for an ongoing auction, so we update the cache
        ongoingAuctionsCache.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
        ongoingAuctionsCache.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());

        //if we opened it before, we update that cache too
        if(previouslyOpenedAuctions.contains(bid.getAuctionId()))
        {
          previouslyOpenedAuctions.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
          previouslyOpenedAuctions.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());
        }
        //if someone else placed a bid for an auction where we previously bid
        //we update the cache
        if(previousBids.contains(bid.getAuctionId()))
        {
          previousBids.getAuctionByID(bid.getAuctionId()).setCurrentBidder(bid.getBidder());
          previousBids.getAuctionByID(bid.getAuctionId()).setCurrentBid(bid.getBidAmount());
        }
        //but if we placed our first bid for an auction, we add it in cache
        else if(bid.getBidder().equals(userEmail) && !previousBids.contains(bid.getAuctionId()))
        {
          //previousBids.addAuction(previouslyOpenedAuctions.getAuctionByID(bid.getAuctionId()));
          try
          {
            previousBids.addAuction(modelManager.getAuction(bid.getAuctionId()));
          }
          catch (SQLException e)
          {
            //
          }
        }

        break;
    }
    property.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
  }
}




