package model;

public interface AuctionModel
{
  Auction startAuction(String title, String description, int reservePrice, int buyoutPrice, int minimumIncrement, int auctionTime, String imagePath);
  int generateID();
  Auction getAuction(int ID);

}
