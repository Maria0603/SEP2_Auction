package model;

import model.domain.Auction;
import model.domain.Bid;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;

public interface AuctionModel extends NamedPropertyChangeSubject
{
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData,
      String seller) throws SQLException, ClassNotFoundException;
  Auction getAuction(int ID) throws SQLException;
  Bid placeBid(String bidder, int bidValue, int auctionId) throws SQLException;
  void buyout(String bidder, int auctionId) throws SQLException;
  void deleteAuction(String moderatorEmail, int auctionId, String reason)
      throws SQLException;
}
