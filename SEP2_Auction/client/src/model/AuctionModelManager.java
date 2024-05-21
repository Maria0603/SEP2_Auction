
package model;

import mediator.AuctionClient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AuctionModelManager implements AuctionModel, PropertyChangeListener {
  private PropertyChangeSupport property;
  private AuctionClient client;

  public AuctionModelManager() throws IOException, SQLException {
    property = new PropertyChangeSupport(this);
    client = new AuctionClient();
    client.addListener("Auction", this);
    client.addListener("End", this);
    client.addListener("Bid", this);
    client.addListener("Notification", this);
    client.addListener("Edit", this);
    client.addListener("Ban", this);
    client.addListener("Reset", this);
    client.addListener("BuyOut", this);
  }

  @Override
  public Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData, String seller) throws SQLException, ClassNotFoundException {
    return client.startAuction(title, description, reservePrice, buyoutPrice,
        minimumIncrement, auctionTime, imageData, seller);
  }

  @Override
  public Auction getAuction(int ID) throws SQLException {
    return client.getAuction(ID);
  }

  @Override
  public AuctionList getOngoingAuctions() throws SQLException {
    return client.getOngoingAuctions();
  }

  @Override
  public NotificationList getNotifications(String receiver)
      throws SQLException {
    return client.getNotifications(receiver);
  }

  @Override
  public String addUser(String firstname, String lastname, String email, String password, String repeatedPassword,
      String phone, LocalDate birthday) throws SQLException {
    return client.addUser(firstname, lastname, email, password, repeatedPassword, phone, birthday);
  }

  @Override
  public String login(String email, String password) throws SQLException {
    return client.login(email, password);
  }

  @Override
  public AuctionList getPreviousBids(String bidder)
      throws SQLException {
    return client.getPreviousBids(bidder);
  }

  @Override
  public AuctionList getCreatedAuctions(String seller)
      throws SQLException {
    return client.getCreatedAuctions(seller);
  }

  @Override
  public void resetPassword(String userEmail, String oldPassword,
      String newPassword, String repeatPassword) throws SQLException {
    client.resetPassword(userEmail, oldPassword, newPassword, repeatPassword);
  }

  @Override
  public User getUser(String email) throws SQLException {
    return client.getUser(email);
  }

  @Override
  public User getModeratorInfo() throws SQLException {
    return client.getModeratorInfo();
  }

  @Override
  public boolean isModerator(String email) throws SQLException {
    return client.isModerator(email);
  }

  @Override
  public User editInformation(String oldEmail, String firstname,
      String lastname, String email, String password, String phone,
      LocalDate birthday) throws SQLException {
    return client.editInformation(oldEmail, firstname, lastname, email, password, phone, birthday);
  }

  @Override
  public AuctionList getAllAuctions() throws SQLException {
    return client.getAllAuctions();
  }

  @Override
  public Bid placeBid(String bidder, int bidValue, int auctionId)
      throws SQLException {
    return client.placeBid(bidder, bidValue, auctionId);
  }

  @Override
  public void buyOut(String bidder, int auctionId)
      throws SQLException {
    client.buyOut(bidder, auctionId);
  }

  @Override public ArrayList<User> getAllUsers()
      throws SQLException {
    return client.getAllUsers();
  }
  @Override public void banParticipant(String moderatorEmail,
      String participantEmail, String reason) throws SQLException
  {
    client.banParticipant(moderatorEmail, participantEmail, reason);
  }

  @Override public String extractBanningReason(String email) throws SQLException
  {
    return client.extractBanningReason(email);
  }

  @Override public void unbanParticipant(String moderatorEmail,
      String participantEmail) throws SQLException
  {
    client.unbanParticipant(moderatorEmail, participantEmail);
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
    // model manager property fires auction events further
    property.firePropertyChange(evt);
    if(evt.getPropertyName().equals("BuyOut")){
      System.out.println("received buyout" + " auction client" + evt.getNewValue().toString());
    }
  }
}
