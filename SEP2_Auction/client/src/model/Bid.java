package model;

import java.io.Serial;
import java.io.Serializable;

public class Bid implements Serializable
{
  private final int auctionId;
  private final String bidder;
  private final int bidAmount;
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  @Serial private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  public Bid(int auctionId, String bidder, int bidAmount)
  {
    this.auctionId = auctionId;
    this.bidder = bidder;
    this.bidAmount = bidAmount;
  }

  public int getAuctionId()
  {
    return auctionId;
  }

  public String getBidder()
  {
    return bidder;
  }

  public int getBidAmount()
  {
    return bidAmount;
  }

  @Override public String toString()
  {
    return "Bid{" + ", auctionId=" + auctionId + ", participantEmail='" + bidder
        + '\'' + ", bidAmount=" + bidAmount + '}';
  }
}
