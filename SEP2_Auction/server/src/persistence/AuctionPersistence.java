package persistence;

import model.Auction;
import model.AuctionModel;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AuctionPersistence
{
  Auction saveAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData) throws SQLException;
  Auction getAuctionById(int id) throws SQLException;
  void markAsClosed(int id) throws SQLException;
}
