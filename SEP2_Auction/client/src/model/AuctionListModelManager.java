package model;

import mediator.AuctionListClient;
import model.domain.Auction;
import model.domain.AuctionList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;

public class AuctionListModelManager
    implements AuctionListModel, PropertyChangeListener
{
  private final PropertyChangeSupport property;
  private final AuctionListClient client;

  public AuctionListModelManager() throws IOException, SQLException
  {
    property = new PropertyChangeSupport(this);
    client = new AuctionListClient();
    client.addListener("Auction", this);
    client.addListener("End", this);
    client.addListener("Bid", this);
    client.addListener("Edit", this);
    client.addListener("Ban", this);
    client.addListener("DeleteAuction", this);
    client.addListener("DeleteAccount", this);
  }

  @Override public AuctionList getOngoingAuctions() throws SQLException
  {
    return client.getOngoingAuctions();
  }

  @Override public AuctionList getPreviousBids(String bidder)
      throws SQLException
  {
    return client.getPreviousBids(bidder);
  }

  @Override public AuctionList getCreatedAuctions(String seller)
      throws SQLException
  {
    return client.getCreatedAuctions(seller);
  }

  @Override public AuctionList getAllAuctions(String moderatorEmail)
      throws SQLException
  {
    return client.getAllAuctions(moderatorEmail);
  }

  @Override public Auction getAuction(int ID) throws SQLException
  {
    return client.getAuction(ID);
  }

  @Override public boolean isModerator(String email) throws SQLException
  {
    return client.isModerator(email);
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
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
    property.removePropertyChangeListener(listener);
  }
}
