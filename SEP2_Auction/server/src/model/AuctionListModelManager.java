package model;

import model.domain.*;
import persistence.AuctionListPersistence;
import persistence.AuctionListProtectionProxy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AuctionListModelManager implements AuctionListModel
{
  private PropertyChangeSupport property;
  private AuctionListPersistence auctionListDatabase;

  public AuctionListModelManager() throws SQLException, ClassNotFoundException
  {
    property = new PropertyChangeSupport(this);
    auctionListDatabase = new AuctionListProtectionProxy();
  }

  @Override public synchronized Auction getAuction(int ID) throws SQLException
  {
    return auctionListDatabase.getAuctionById(ID);
  }

  @Override public synchronized AuctionList getOngoingAuctions()
      throws SQLException
  {
    return auctionListDatabase.getOngoingAuctions();
  }


  @Override public synchronized AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    return auctionListDatabase.getPreviousBids(bidder);
  }

  @Override public synchronized AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    return auctionListDatabase.getCreatedAuctions(seller);
  }


  @Override public synchronized boolean isModerator(String email)
      throws SQLException
  {
    return auctionListDatabase.isModerator(email);
  }

  @Override public synchronized AuctionList getAllAuctions(
      String moderatorEmail) throws SQLException
  {
    return auctionListDatabase.getAllAuctions(moderatorEmail);
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

}
