package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

import java.sql.SQLException;

public interface AuctionModel extends NamedPropertyChangeSubject
{
  Auction startAuction(String title, String description,
      int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime,
      byte[] imageData) throws SQLException;
  Auction getAuction(int ID) throws SQLException;
}
