package persistence;

import model.domain.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface AuctionPersistence
{
  Auction saveAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData, String seller)
      throws SQLException;
  Auction getAuctionById(int id) throws SQLException;
  void markAsClosed(int id) throws SQLException;
  Notification saveNotification(String content, String receiver) throws SQLException;
  Bid saveBid(String participantEmail, int bidAmount, int auctionId) throws SQLException;
  Bid getCurrentBidForAuction(int auctionId) throws SQLException;
  User getUserInfo(String email) throws SQLException;
  Bid buyout(String bidder, int auctionId) throws SQLException;
  void deleteAuction(String moderatorEmail, int auctionId, String reason) throws SQLException;
}
