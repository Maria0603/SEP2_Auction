package model;

import java.io.Serializable;

public class PriceConstraint implements Serializable
{
  ///////////////////////////////////////////////////////////////////
  //do not change this number
  private static final long serialVersionUID = 6529685098267757690L;
  //////////////////////////////////////////////////////////////////

  private int reservePrice, buyoutPrice, minimumIncrement;

  public PriceConstraint(int reservePrice, int buyoutPrice, int minimumIncrement)
  {
    setBuyoutPrice(buyoutPrice);
    setReservePrice(reservePrice);
    setMinimumIncrement(minimumIncrement);
  }
  public int getReservePrice()
  {
    return reservePrice;
  }

  public void setReservePrice(int reservePrice)
  {
    if (reservePrice < 0)
      throw new IllegalArgumentException(
          "The reserve price must be a positive number!");
    this.reservePrice = reservePrice;
  }

  public int getBuyoutPrice()
  {
    return buyoutPrice;
  }

  public void setBuyoutPrice(int buyoutPrice)
  {
    if (buyoutPrice <= reservePrice)
      throw new IllegalArgumentException(
          "The buyout price must be greater than the reserve price!");
    this.buyoutPrice = buyoutPrice;
  }

  public int getMinimumIncrement()
  {
    return minimumIncrement;
  }

  public void setMinimumIncrement(int minimumIncrement)
  {
    if (minimumIncrement < 1)
      throw new IllegalArgumentException(
          "The minimum bid increment must be at least 1!");
    this.minimumIncrement = minimumIncrement;
  }
}
