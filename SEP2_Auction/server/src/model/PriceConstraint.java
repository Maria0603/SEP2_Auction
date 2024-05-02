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
    this.reservePrice = reservePrice;
  }

  public int getBuyoutPrice()
  {
    return buyoutPrice;
  }

  public void setBuyoutPrice(int buyoutPrice)
  {
    this.buyoutPrice = buyoutPrice;
  }

  public int getMinimumIncrement()
  {
    return minimumIncrement;
  }

  public void setMinimumIncrement(int minimumIncrement)
  {
    this.minimumIncrement = minimumIncrement;
  }
}
