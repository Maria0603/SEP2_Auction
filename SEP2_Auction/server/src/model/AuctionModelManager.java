

package model;

import persistence.AuctionDatabase;
import persistence.AuctionPersistence;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;
  private AuctionPersistence auctionDatabase;
  private UserList userList;

  public AuctionModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    auctionDatabase = new AuctionDatabase();
    userList = new UserList();
  }

  @Override public synchronized Auction startAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException
  {
    Auction auction = auctionDatabase.saveAuction(title, description,
        reservePrice, buyoutPrice, minimumIncrement, auctionTime, imageData);
    property.firePropertyChange("Auction", null, auction);

    // auction.addListener("Time", this);
    auction.addListener("End", this);
    return auction;
  }

  @Override public synchronized Auction getAuction(int ID) throws SQLException
  {
    System.out.println("server received a request in getAuctionById");
    return auctionDatabase.getAuctionById(ID);
  }

  @Override public synchronized AuctionList getOngoingAuctions()
      throws SQLException
  {
    System.out.println("server received a request in getOngoingAuctions");
    return auctionDatabase.getOngoingAuctions();
  }


  @Override public NotificationList getNotifications(String receiver)
      throws SQLException
  {
    return auctionDatabase.getNotifications(receiver);
  }

  @Override public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws SQLException
  {
    Bid bid=auctionDatabase.saveBid(bidder, bidValue, auctionId);
    property.firePropertyChange("Bid", null, bid);
    return bid;

  @Override
  public void addUser(String firstname, String lastname, String email, String password, String phone) {
    //  TODO: Add to the database table
    userList.addUser(firstname,lastname,email,password,phone);
  }

  @Override
  public String getUser(String email, String password) {
    if(userList.passValidation(email,password)){
      return userList.getUser(email).getEmail();
    }
    return null;
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
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}
