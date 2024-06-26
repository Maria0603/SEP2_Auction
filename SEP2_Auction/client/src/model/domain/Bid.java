package model.domain;

import java.io.Serial;
import java.io.Serializable;

/**
 * The Bid class represents a bid in an auction and contains information
 * about the auction ID, bidder, and bid amount. It implements Serializable
 * for object serialization.
 */
public class Bid implements Serializable {

  private final int auctionId;
  private final String bidder;
  private final int bidAmount;

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  /**
   * Constructs a new Bid object with the specified parameters.
   *
   * @param auctionId the ID of the auction.
   * @param bidder the name of the bidder.
   * @param bidAmount the amount of the bid.
   */
  public Bid(int auctionId, String bidder, int bidAmount) {
    this.auctionId = auctionId;
    this.bidder = bidder;
    this.bidAmount = bidAmount;
  }

  /**
   * Gets the ID of the auction.
   *
   * @return the auction ID.
   */
  public int getAuctionId() {
    return auctionId;
  }

  /**
   * Gets the name of the bidder.
   *
   * @return the name of the bidder.
   */
  public String getBidder() {
    return bidder;
  }

  /**
   * Gets the amount of the bid.
   *
   * @return the bid amount.
   */
  public int getBidAmount() {
    return bidAmount;
  }

  /**
   * Returns a string representation of the bid.
   *
   * @return a string representation of the bid.
   */
  @Override
  public String toString() {
    return "Bid{" + "auctionId=" + auctionId + ", bidder='" + bidder + '\'' + ", bidAmount=" + bidAmount + '}';
  }
}
