package model;

import persistence.AuctionDatabase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener {
  private PropertyChangeSupport property;
  private AuctionPersistence auctionDatabase;

  public AuctionModelManager() throws SQLException, ClassNotFoundException {
    property = new PropertyChangeSupport(this);
    auctionDatabase = new AuctionDatabase();
  }

  @Override
  public Auction startAuction(String title,
      String description, int reservePrice, int buyoutPrice,
      int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException {
    Auction auction = auctionDatabase.saveAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData);
    property.firePropertyChange("Auction", null, auction);

    // auction.addListener("Time", this);
    auction.addListener("End", this);
    return auction;
  }

  @Override
  public Auction getAuction(int ID) throws SQLException {
    return auctionDatabase.getAuctionById(ID);
  }

  @Override
  public AuctionList getOngoingAuctions() throws SQLException {
    return auctionDatabase.getOngoingAuctions();
  }

  @Override
  public void addListener(String propertyName,
      PropertyChangeListener listener) {
    property.addPropertyChangeListener(propertyName, listener);
  }

  @Override
  public void removeListener(String propertyName,
      PropertyChangeListener listener) {
    property.removePropertyChangeListener(propertyName, listener);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // update the time and status in the database
    /*
     * if(evt.getPropertyName().equals("Time") && (int)evt.getNewValue()%5==0)
     * {
     * try
     * {
     * auctionDatabase.updateTime((int)evt.getOldValue(), (int)evt.getNewValue());
     * }
     * catch(SQLException e)
     * {
     * e.printStackTrace();
     * }
     * }
     */
    if (evt.getPropertyName().equals("End")) {
      try {
        auctionDatabase.markAsClosed((int) evt.getOldValue());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    // model manager property fires auction events further
    property.firePropertyChange(evt);
  }
}