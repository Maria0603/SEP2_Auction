package model;

import model.domain.Auction;
import model.domain.Bid;
import utility.observer.javaobserver.NamedPropertyChangeSubject;

public interface AuctionModel extends NamedPropertyChangeSubject
{
  Auction startAuction(String title, String description, int reservePrice,
      int buyoutPrice, int minimumIncrement, int auctionTime, byte[] imageData,
      String seller) throws IllegalArgumentException, ClassNotFoundException;
  Auction getAuction(int ID) throws IllegalArgumentException;
  Bid placeBid(String bidder, int bidValue, int auctionId) throws IllegalArgumentException;
  void buyout(String bidder, int auctionId) throws IllegalArgumentException;
  void deleteAuction(String moderatorEmail, int auctionId, String reason)
      throws IllegalArgumentException;
}
