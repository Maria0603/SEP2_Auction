package model;

import java.io.Serializable;
import java.util.ArrayList;

public class AuctionList implements Serializable
{
  private ArrayList<AuctionShortVersion> auctions;
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public AuctionList()
  {
    this.auctions = new ArrayList<>();
  }

  public void addAuction(AuctionShortVersion auction)
  {
    if (auction != null)
      auctions.add(auction);
  }

  public void removeAuction(AuctionShortVersion auction)
  {
    auctions.remove(auction);
  }

  public void removeAuction(int ID)
  {
    auctions.remove(getAuctionByID(ID));
  }

  public AuctionShortVersion getAuctionByID(int ID)
  {
    for (AuctionShortVersion auction : auctions)
      if (auction.getId() == ID)
        return auction;
    throw new IllegalArgumentException("No auction with this ID.");
  }
  public AuctionShortVersion getAuction(int index)
  {
    return auctions.get(index);
  }

  public int getSize()
  {
    return auctions.size();
  }
}
