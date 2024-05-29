package model.domain;

import java.io.Serial;
import java.io.Serializable;

/**
 * The PriceConstraint class represents the price constraints of an auction,
 * including the reserve price, buyout price, and minimum increment for bids.
 * It implements Serializable for object serialization.
 */
public class PriceConstraint implements Serializable {

  ///////////////////////////////////////////////////////////////////
  // Do not change this number
  @Serial
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private int reservePrice;
  private int buyoutPrice;
  private int minimumIncrement;

  /**
   * Constructs a new PriceConstraint object with the specified parameters.
   *
   * @param reservePrice the reserve price of the auction.
   * @param buyoutPrice the buyout price of the auction.
   * @param minimumIncrement the minimum increment for bids.
   */
  public PriceConstraint(int reservePrice, int buyoutPrice, int minimumIncrement) {
    setBuyoutPrice(buyoutPrice);
    setReservePrice(reservePrice);
    setMinimumIncrement(minimumIncrement);
  }

  /**
   * Gets the reserve price of the auction.
   *
   * @return the reserve price of the auction.
   */
  public int getReservePrice() {
    return reservePrice;
  }

  /**
   * Sets the reserve price of the auction.
   *
   * @param reservePrice the reserve price to set.
   */
  public void setReservePrice(int reservePrice) {
    this.reservePrice = reservePrice;
  }

  /**
   * Gets the buyout price of the auction.
   *
   * @return the buyout price of the auction.
   */
  public int getBuyoutPrice() {
    return buyoutPrice;
  }

  /**
   * Sets the buyout price of the auction.
   *
   * @param buyoutPrice the buyout price to set.
   */
  public void setBuyoutPrice(int buyoutPrice) {
    this.buyoutPrice = buyoutPrice;
  }

  /**
   * Gets the minimum increment for bids.
   *
   * @return the minimum increment for bids.
   */
  public int getMinimumIncrement() {
    return minimumIncrement;
  }

  /**
   * Sets the minimum increment for bids.
   *
   * @param minimumIncrement the minimum increment to set.
   */
  public void setMinimumIncrement(int minimumIncrement) {
    this.minimumIncrement = minimumIncrement;
  }
}
