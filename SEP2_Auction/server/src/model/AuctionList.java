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
    auctions.remove(getAuction(ID));
  }

  public Auction getAuction(int ID)
  {
    for (int i = 0; i < auctions.size(); i++)
      if (auctions.get(i).getID() == ID)
        return auctions.get(i);
    throw new IllegalArgumentException("No auction with this ID.");
  }

  public int getSize()
  {
    return auctions.size();
  }
}
