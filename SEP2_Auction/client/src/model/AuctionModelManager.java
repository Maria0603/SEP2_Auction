package model;

import mediator.AuctionClient;
import model.domain.Auction;
import model.domain.Bid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener
{
  private PropertyChangeSupport property;
  private AuctionClient client;

  public AuctionModelManager() throws IOException, SQLException
  {
    property = new PropertyChangeSupport(this);
    client = new AuctionClient();
    client.addListener("End", this);
    client.addListener("Bid", this);
    client.addListener("Edit", this);
    client.addListener("Ban", this);
    client.addListener("DeleteAuction", this);
    client.addListener("DeleteAccount", this);

  }

  @Override public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller)
      throws SQLException, ClassNotFoundException
  {
    return client.startAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData, seller);
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    return client.getAuction(ID);
  }

  @Override public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws SQLException
  {
    return client.placeBid(bidder, bidValue, auctionId);
  }

  @Override public void buyout(String bidder, int auctionId) throws SQLException
  {
    client.buyout(bidder, auctionId);
  }


  @Override public void deleteAuction(String moderatorEmail, int auctionId,
      String reason) throws SQLException
  {
    client.deleteAuction(moderatorEmail, auctionId, reason);
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
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}
