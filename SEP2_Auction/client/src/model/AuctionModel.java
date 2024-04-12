package model;

import utility.observer.javaobserver.NamedPropertyChangeSubject;

public interface AuctionModel extends NamedPropertyChangeSubject
{
  Auction startAuction(int ID, String title, String description, int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime, String imagePath);
  int generateID();
  Auction getAuction(int ID);

}
