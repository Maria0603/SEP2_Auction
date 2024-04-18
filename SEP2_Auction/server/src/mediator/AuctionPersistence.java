package mediator;

import model.Auction;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AuctionPersistence
{
  ArrayList <Auction> loadOngoingAuctions() throws SQLException;
  ArrayList<Auction> loadClosedAuctions() throws SQLException;
  Auction saveAuction(int ID, String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime,
      String imagePath) throws SQLException;
  Auction getAuctionById(int id) throws SQLException;
  void removeAuction(Auction auction) throws SQLException;
  void clear() throws SQLException;
}
