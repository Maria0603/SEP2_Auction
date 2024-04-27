package persistence;

import model.Auction;

import java.sql.SQLException;

public interface AuctionPersistence
{
  Auction saveAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData) throws SQLException;
  Auction getAuctionById(int id) throws SQLException;
  void updateTime(int id, int seconds) throws SQLException;
  void markAsClosed(int id) throws SQLException;
}
