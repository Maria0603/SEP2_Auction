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
  Bid saveBid(int auctionId, String participantEmail, double bidAmount) throws SQLException;

  List<Bid> getBidsForAuction(int auctionId) throws SQLException;
  void updateCurrentBid(Bid currentBid) throws SQLException;
}
