package model.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class AuctionList implements Serializable
{
  private final ArrayList<Auction> auctions;
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  @Serial private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public AuctionList()
  {
    this.auctions = new ArrayList<>();
  }

  public void addAuction(Auction auction)
  {
    if (auction != null)
      auctions.add(auction);
  }

  public void removeAuction(int ID)
  {
    for (int i = 0; i < auctions.size(); i++)
      if (auctions.get(i).getID() == ID)
      {
        auctions.remove(i);
        i--;
      }
  }

  public Auction getAuctionByID(int ID)
  {
    for (Auction auction : auctions)
      if (auction.getID() == ID)
        return auction;
    throw new IllegalArgumentException("No auction with this ID.");
  }

  public Auction getAuction(int index)
  {
    return auctions.get(index);
  }

  public int getSize()
  {
    return auctions.size();
  }

  public String toString()
  {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < getSize(); i++)
      s.append(auctions.get(i)).append('\n');
    return s.toString();
  }

  public boolean contains(int auctionId)
  {
    for (int i = 0; i < auctions.size(); i++)
      if (auctions.get(i).getID() == auctionId)
        return true;
    return false;
  }
}
