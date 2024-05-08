package persistence;

import model.*;

import java.sql.SQLException;
import java.util.List;

public interface AuctionPersistence
{
  Auction saveAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData)
      throws SQLException;
  Auction getAuctionById(int id) throws SQLException;
  void markAsClosed(int id) throws SQLException;
  AuctionList getOngoingAuctions() throws SQLException;
  NotificationList getNotifications(String receiver) throws SQLException;
  Notification saveNotification(String content, String receiver) throws SQLException;
  Bid saveBid(String participantEmail, int bidAmount, int auctionId) throws SQLException;
  Bid getBidForAuction(int auctionId) throws SQLException;
  void updateCurrentBid(Bid currentBid) throws SQLException;
  User createUser(String firstname, String lastname, String email, String password, String phone) throws SQLException;
  User getUser(String email, String password) throws SQLException;
}
