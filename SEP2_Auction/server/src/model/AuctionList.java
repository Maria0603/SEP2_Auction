package model;

import java.util.ArrayList;

public class AuctionList
{
  private ArrayList<Auction> auctions;

  public AuctionList()
  {
    this.auctions = new ArrayList<>();
  }

  public void addAuction(Auction auction)
  {
    if (auction != null)
      auctions.add(auction);
  }

  public void removeAuction(Auction auction)
  {
    auctions.remove(auction);
  }

  public void removeAuction(int ID)
  {
    auctions.remove(getAuctionByID(ID));
  }

  public Auction getAuctionByID(int ID)
  {
    for (Auction auction : auctions)
      if (auction.getID() == ID)
        return auction;
    throw new IllegalArgumentException("No auction with this ID.");
  }

  public int getSize()
  {
    return auctions.size();
  }
}
